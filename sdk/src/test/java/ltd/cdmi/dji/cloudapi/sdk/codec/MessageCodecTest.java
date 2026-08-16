// Copyright (C) 2026 CDMI.LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ltd.cdmi.dji.cloudapi.sdk.codec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MessageCodec} 单元测试。
 *
 * <p>覆盖 DJI JSON 编解码核心行为：SNAKE_CASE 命名策略双向匹配、常用字段提取
 * （method / tid / result / data）与序列化 snake_case 输出。
 *
 * <p>测试依据：MessageCodec 内部 ObjectMapper 配置
 * {@code FAIL_ON_UNKNOWN_PROPERTIES=false} + {@link com.fasterxml.jackson.databind.PropertyNamingStrategies#SNAKE_CASE}，
 * DJI JSON 用 snake_case（mode_code），Java record 用 camelCase（modeCode）。
 */
class MessageCodecTest {

    /**
     * 测试用 OSD record，字段 modeCode 对应 DJI JSON 的 mode_code。
     * 用 Integer（包装类型）而非 int，便于检测缺失字段时是否为 null。
     */
    record TestOsd(Integer modeCode) {}

    /**
     * 验证 SNAKE_CASE 命名策略生效：DJI JSON 用 snake_case（mode_code），
     * Java record 字段用 camelCase（modeCode），反序列化后字段应被正确填充（非 null）。
     *
     * <p>Given: DJI OSD JSON {@code {"mode_code": 0}}
     * When:  用 MessageCodec.fromJson 反序列化为 TestOsd
     * Then:  modeCode 不为 null 且等于 0（证明 SNAKE_CASE 策略生效，否则字段为 null）
     */
    @Test
    void testSnakeCaseDeserialization() {
        String json = "{\"mode_code\":0}";
        TestOsd osd = MessageCodec.fromJson(json, TestOsd.class);
        assertNotNull(osd.modeCode(), "modeCode 应被 SNAKE_CASE 策略正确填充，不应为 null");
        assertEquals(0, osd.modeCode(), "mode_code=0 应映射到 modeCode=0");
    }

    /**
     * 验证从 DJI 消息 JSON 中提取 method 字段。
     *
     * <p>Given: JSON {@code {"method":"flighttask_prepare","data":{}}}
     * When:  调用 DjiMessage.extractMethod
     * Then:  返回 "flighttask_prepare"
     */
    @Test
    void testExtractMethod() {
        String json = "{\"method\":\"flighttask_prepare\",\"data\":{}}";
        assertEquals("flighttask_prepare", DjiMessage.extractMethod(json),
                "应正确提取顶层 method 字段");
    }

    /**
     * 验证从 DJI 消息 JSON 中提取 tid 字段（事务 ID）。
     *
     * <p>Given: JSON {@code {"tid":"abc123","method":"test"}}
     * When:  调用 DjiMessage.extractTid
     * Then:  返回 "abc123"
     */
    @Test
    void testExtractTid() {
        String json = "{\"tid\":\"abc123\",\"method\":\"test\"}";
        assertEquals("abc123", DjiMessage.extractTid(json),
                "应正确提取顶层 tid 字段");
    }

    /**
     * 验证从 DJI 回复 JSON 中提取 data.result 字段。
     *
     * <p>DJI services_reply 的 result 位于 data.result（非顶层 result）。
     *
     * <p>Given: JSON {@code {"data":{"result":0}}} 与 {@code {"data":{"result":210229}}}
     * When:  调用 DjiMessage.extractResult
     * Then:  分别返回 0（成功）与 210229（绑定码错误）
     */
    @Test
    void testExtractResult() {
        String success = "{\"data\":{\"result\":0}}";
        assertEquals(0, DjiMessage.extractResult(success),
                "data.result=0 应提取为 0");

        String bindError = "{\"data\":{\"result\":210229}}";
        assertEquals(210229, DjiMessage.extractResult(bindError),
                "data.result=210229（组织ID与绑定码错误）应提取为 210229");
    }

    /**
     * 验证 result 缺失时返回默认值 -1。
     *
     * <p>Given: JSON {@code {"method":"test"}}（无 data.result）
     * When:  调用 DjiMessage.extractResult
     * Then:  返回 -1（缺失默认值，区别于合法的 result=0）
     */
    @Test
    void testExtractResultMissing() {
        String json = "{\"method\":\"test\"}";
        assertEquals(-1, DjiMessage.extractResult(json),
                "data.result 缺失时应返回默认值 -1");
    }

    /**
     * 验证从 DJI 消息 JSON 中提取 data 字段并转为 Java 对象。
     *
     * <p>Given: JSON {@code {"data":{"key":"value"}}}
     * When:  调用 DjiMessage.extractData
     * Then:  返回非 null 对象（data 存在）
     */
    @Test
    void testExtractData() {
        String json = "{\"data\":{\"key\":\"value\"}}";
        Object data = DjiMessage.extractData(json);
        assertNotNull(data, "data 存在时应返回非 null 对象");
    }

    /**
     * 验证序列化输出含 snake_case 字段名。
     *
     * <p>Given: TestOsd(modeCode=1)
     * When:  调用 MessageCodec.toJson 序列化
     * Then:  JSON 含 "mode_code" 字段名（SNAKE_CASE 策略对序列化同样生效）
     */
    @Test
    void testToJson() {
        TestOsd osd = new TestOsd(1);
        String json = MessageCodec.toJson(osd);
        assertTrue(json.contains("mode_code"),
                "序列化 JSON 应含 snake_case 字段名 mode_code，实际: " + json);
    }

    /**
     * 验证 NON_NULL 配置：null 字段不序列化。
     *
     * <p>Given: TestOsd(modeCode=null)
     * When:  调用 MessageCodec.toJson 序列化
     * Then:  JSON 不含 "mode_code" 键（null 字段被忽略），输出 "{}"
     *
     * <p>此配置支持 DJI 协议的「部分字段构造」场景——如机场 OSD 分多条推送，
     * 每条只含部分字段，null 字段自动忽略不输出，避免平台收到多余字段。
     */
    @Test
    void testNonNullSerializationExcludesNullFields() {
        TestOsd osd = new TestOsd(null);
        String json = MessageCodec.toJson(osd);
        assertFalse(json.contains("mode_code"),
                "null 字段 mode_code 不应序列化，实际: " + json);
    }
}
