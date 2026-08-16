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

import ltd.cdmi.dji.cloudapi.sdk.http.HttpResponseEnvelope;
import ltd.cdmi.dji.cloudapi.sdk.http.StsCredentials;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link HttpResponseEnvelope#parse(String, Class)} 的 HTTP 响应类型安全解析。
 *
 * <p><b>核心证明</b>：{@code parseHttp} 返回 {@link HttpResponseEnvelope}<T>，其
 * {@code data()} 是调用方指定的 POJO 类型（编译期确定）。{@code resp.data().bucket()}
 * 编译通过即证明类型安全——无需 cast，无需 instanceof。
 *
 * <p>这是与 MQTT 通道 {@code parse} 和 WebSocket 通道 {@code parseWs} 对称的 HTTP 通道 API。
 * DJI HTTP API 响应统一信封为 {@code {"code":0, "message":"...", "data":{...}}}。
 *
 * <p><b>Bug 回归测试</b>：{@code testParseHttpFixesFromJsonBug} 佐证旧写法
 * {@code fromJson(resp.body(), StsCredentials.class)} 会因业务数据在 {@code data} 内层
 * 而非顶层导致字段全 null，{@code parseHttp} 正确提取 {@code data} 子节点反序列化。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/basic-concept/https.html">
 * DJI HTTPS Response 规范</a>
 */
class MessageCodecParseHttpTest {

    /** STS 成功响应的测试 JSON（data 字段结构来自 simulator StorageApi 已验证字段） */
    private static final String STS_SUCCESS = "{\"code\":0,\"message\":\"success\","
            + "\"data\":{\"bucket\":\"dji-bucket\","
            + "\"endpoint\":\"oss-cn-hangzhou.aliyuncs.com\","
            + "\"region\":\"cn-hangzhou\","
            + "\"provider\":\"aliyun\","
            + "\"credentials\":{\"access_key_id\":\"STS.xxx\",\"access_key_secret\":\"sec\","
            + "\"security_token\":\"tok\",\"expire\":3600},"
            + "\"object_key_prefix\":\"prefix/flight1\"}}";

    // ==================== 类型安全证明 ====================

    @Test
    @DisplayName("parseHttp 返回 HttpResponseEnvelope<StsCredentials> — data().bucket() 直接访问，无 cast")
    void testParseHttpReturnsTypedData() {
        // When: parseHttp 指定 StsCredentials.class
        HttpResponseEnvelope<StsCredentials> resp = HttpResponseEnvelope.parse(STS_SUCCESS, StsCredentials.class);

        // Then: data() 是 StsCredentials，直接访问字段——无 cast，无 instanceof
        // 如果 data() 是 Object，以下行不会编译
        assertEquals(0, resp.code());
        assertEquals("success", resp.message());
        assertEquals("dji-bucket", resp.data().bucket());
        assertEquals("oss-cn-hangzhou.aliyuncs.com", resp.data().endpoint());
        assertEquals("cn-hangzhou", resp.data().region());
        assertEquals("aliyun", resp.data().provider());
        assertEquals("prefix/flight1", resp.data().objectKeyPrefix());
        assertNotNull(resp.data().credentials(), "credentials 子结构不应为 null");
    }

    @Test
    @DisplayName("parseHttp 提取信封字段 code/message")
    void testParseHttpExtractsEnvelopeFields() {
        String payload = "{\"code\":0,\"message\":\"OK\",\"data\":{\"bucket\":\"b\"}}";

        HttpResponseEnvelope<StsCredentials> resp = HttpResponseEnvelope.parse(payload, StsCredentials.class);

        assertEquals(0, resp.code());
        assertEquals("OK", resp.message());
    }

    // ==================== Bug 回归测试 ====================

    @Test
    @DisplayName("fromJson 直接反序列化 HTTP 响应 → 字段全 null（Bug 复现），parseHttp 修复")
    void testParseHttpFixesFromJsonBug() {
        // Bug 复现：fromJson 把 code/message/data 当 StsCredentials 顶层字段
        // FAIL_ON_UNKNOWN_PROPERTIES=false 静默忽略未知字段，bucket 等在 data 内层 → null
        StsCredentials broken = MessageCodec.fromJson(STS_SUCCESS, StsCredentials.class);
        assertNull(broken.bucket(), "fromJson Bug: bucket 在 data 内层，顶层无此字段 → null");
        assertNull(broken.objectKeyPrefix(), "fromJson Bug: object_key_prefix 在 data 内层 → null");

        // 修复验证：parseHttp 正确提取 data 子节点反序列化
        HttpResponseEnvelope<StsCredentials> fixed = HttpResponseEnvelope.parse(STS_SUCCESS, StsCredentials.class);
        assertEquals("dji-bucket", fixed.data().bucket(), "parseHttp 修复: data 子节点正确反序列化");
        assertEquals("prefix/flight1", fixed.data().objectKeyPrefix(), "SNAKE_CASE 自动映射 object_key_prefix → objectKeyPrefix");
    }

    // ==================== 错误响应场景 ====================

    @Test
    @DisplayName("code 非 0 的错误响应 — data 为 null，message 含错误描述")
    void testParseHttpErrorResponse() {
        String payload = "{\"code\":401,\"message\":\"云平台登录失效，请重新登录\",\"data\":null}";

        HttpResponseEnvelope<StsCredentials> resp = HttpResponseEnvelope.parse(payload, StsCredentials.class);

        assertEquals(401, resp.code());
        assertEquals("云平台登录失效，请重新登录", resp.message());
        assertNull(resp.data(), "错误响应 data 应为 null");
    }

    // ==================== 边界场景 ====================

    @Test
    @DisplayName("JSON 无 data 字段 — resp.data() 为 null")
    void testParseHttpMissingDataField() {
        String payload = "{\"code\":0,\"message\":\"success\"}";

        HttpResponseEnvelope<StsCredentials> resp = HttpResponseEnvelope.parse(payload, StsCredentials.class);

        assertEquals(0, resp.code());
        assertEquals("success", resp.message());
        assertNull(resp.data(), "JSON 无 data 字段时 data() 应为 null");
    }

    @Test
    @DisplayName("无效 JSON — 抛出 IllegalStateException")
    void testParseHttpInvalidJsonThrows() {
        String invalidJson = "{broken json";

        assertThrows(IllegalStateException.class,
                () -> HttpResponseEnvelope.parse(invalidJson, StsCredentials.class),
                "无效 JSON 应抛出 IllegalStateException");
    }

    // ==================== 典型调用方代码模式 ====================

    @Test
    @DisplayName("典型调用方模式 — parseHttp + code 检查 + 类型安全数据访问")
    void testTypicalCallerPattern() {
        // 成功响应
        HttpResponseEnvelope<StsCredentials> ok = HttpResponseEnvelope.parse(STS_SUCCESS, StsCredentials.class);
        if (ok.code() == 0) {
            // 类型安全访问，无 cast 无 instanceof
            assertEquals("dji-bucket", ok.data().bucket());
            assertEquals("aliyun", ok.data().provider());
        } else {
            throw new AssertionError("不应进入错误分支");
        }

        // 错误响应
        String errorJson = "{\"code\":500,\"message\":\"internal error\",\"data\":null}";
        HttpResponseEnvelope<StsCredentials> err = HttpResponseEnvelope.parse(errorJson, StsCredentials.class);
        if (err.code() != 0) {
            assertEquals("internal error", err.message());
            assertNull(err.data());
        } else {
            throw new AssertionError("不应进入成功分支");
        }
    }
}
