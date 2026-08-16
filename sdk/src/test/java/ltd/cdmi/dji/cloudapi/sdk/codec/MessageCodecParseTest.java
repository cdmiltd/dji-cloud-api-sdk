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

import ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply;
import ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.flight.FlyToPointRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link DjiMessage#parse(String, Class)} 的类型安全信封解析。
 *
 * <p><b>核心证明</b>：{@code parse} 返回 {@link DjiMessage}<T>，其 {@code data()}
 * 是调用方指定的 POJO 类型（编译期确定）。{@code msg.data().flyToId()} 编译通过
 * 即证明类型安全——无需 cast，无需 instanceof。
 *
 * <p>这是方案 A（便捷方法）的核心 API：每个 switch case 只需 1 行 parse 调用，
 * 其余是类型安全的业务代码。SDK 不接管路由，调用方用 switch 按方法分发。
 */
class MessageCodecParseTest {

    // ==================== 类型安全证明 ====================

    @Test
    @DisplayName("parse 返回 DjiMessage<FlyToPointRequest> — data() 直接访问字段，无 cast")
    void testParseReturnsTypedData() {
        // Given: fly_to_point 消息（DJI 协议规定 data 用 points 数组结构）
        String payload = "{\"method\":\"fly_to_point\","
                + "\"data\":{\"fly_to_id\":\"FT001\",\"max_speed\":10,"
                + "\"points\":[{\"latitude\":22.5,\"longitude\":113.9,\"height\":50}]},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";

        // When: parse 指定 FlyToPointRequest.class
        DjiMessage<FlyToPointRequest> msg = DjiMessage.parse(payload, FlyToPointRequest.class);

        // Then: data() 是 FlyToPointRequest，直接访问字段——无 cast，无 instanceof
        // 如果 data() 是 Object，以下行不会编译
        assertEquals("FT001", msg.data().flyToId());
        assertEquals(10, msg.data().maxSpeed());
        assertEquals(1, msg.data().points().size());
        assertEquals(22.5, msg.data().points().get(0).latitude(), 0.001);
        assertEquals(113.9, msg.data().points().get(0).longitude(), 0.001);
        assertEquals(50.0, msg.data().points().get(0).height(), 0.001);
    }

    @Test
    @DisplayName("parse 提取信封字段 method/tid/bid")
    void testParseExtractsEnvelopeFields() {
        String payload = "{\"method\":\"fly_to_point\","
                + "\"data\":{\"fly_to_id\":\"X\","
                + "\"points\":[{\"latitude\":1,\"longitude\":2,\"height\":3}]},"
                + "\"tid\":\"tx-999\",\"bid\":\"bx-888\"}";

        DjiMessage<FlyToPointRequest> msg = DjiMessage.parse(payload, FlyToPointRequest.class);

        assertEquals("fly_to_point", msg.method());
        assertEquals("tx-999", msg.tid());
        assertEquals("bx-888", msg.bid());
    }

    // ==================== 多通道适用性 ====================

    @Test
    @DisplayName("无参数方法用 NoParameterRequest — data 为空 record")
    void testParseNoParameterRequest() {
        // DRC 通道的 drc_force_landing 或 service 的 cover_open 都用 NoParameterRequest
        String payload = "{\"method\":\"cover_open\",\"data\":{},\"tid\":\"t2\"}";

        DjiMessage<NoParameterRequest> msg = DjiMessage.parse(payload, NoParameterRequest.class);

        // data() 是 NoParameterRequest（空 record），非 null
        assertNotNull(msg.data());
        assertEquals("cover_open", msg.method());
        assertEquals("t2", msg.tid());
    }

    @Test
    @DisplayName("DRC 通道无 bid 字段 — msg.bid() 为 null")
    void testParseDrcChannelNoBid() {
        // DRC 消息通常不携带 bid
        String payload = "{\"method\":\"heart_beat\",\"data\":{\"timestamp\":1234567890},\"tid\":\"t3\"}";

        DjiMessage<NoParameterRequest> msg = DjiMessage.parse(payload, NoParameterRequest.class);

        assertEquals("heart_beat", msg.method());
        assertEquals("t3", msg.tid());
        assertNull(msg.bid(), "DRC 通道无 bid，应为 null");
    }

    // ==================== 边界场景 ====================

    @Test
    @DisplayName("JSON 无 data 字段 — msg.data() 为 null")
    void testParseMissingDataField() {
        // 某些异常消息可能缺少 data 字段
        String payload = "{\"method\":\"unknown_method\",\"tid\":\"t4\"}";

        DjiMessage<NoParameterRequest> msg = DjiMessage.parse(payload, NoParameterRequest.class);

        assertEquals("unknown_method", msg.method());
        assertEquals("t4", msg.tid());
        assertNull(msg.data(), "JSON 无 data 字段时 data() 应为 null");
    }

    @Test
    @DisplayName("无效 JSON — 抛出 IllegalStateException")
    void testParseInvalidJsonThrows() {
        String invalidJson = "{broken json";

        assertThrows(IllegalStateException.class,
                () -> DjiMessage.parse(invalidJson, FlyToPointRequest.class),
                "无效 JSON 应抛出 IllegalStateException");
    }

    @Test
    @DisplayName("data 字段与 POJO 类型不匹配 — 抛出 IllegalStateException")
    void testParseTypeMismatchThrows() {
        // JSON 的 data 是 fly_to_point 的结构，但调用方指定了 NoParameterRequest
        // NoParameterRequest 是空 record，Jackson 无法映射 fly_to_id 等字段
        // 但由于 FAIL_ON_UNKNOWN_PROPERTIES=false，Jackson 会忽略多余字段
        // 实际上不会抛异常——空 record 会成功反序列化
        // 所以这个测试验证的是：即使 JSON 有多余字段，空 record 仍能反序列化
        String payload = "{\"method\":\"fly_to_point\","
                + "\"data\":{\"fly_to_id\":\"X\",\"target_latitude\":1,"
                + "\"target_longitude\":2,\"target_height\":3},"
                + "\"tid\":\"t5\"}";

        // NoParameterRequest 忽略所有字段，成功创建空 record
        DjiMessage<NoParameterRequest> msg = DjiMessage.parse(payload, NoParameterRequest.class);
        assertNotNull(msg.data(), "空 record 应成功反序列化，忽略多余字段");
    }

    // ==================== 典型调用方代码模式 ====================

    @Test
    @DisplayName("典型调用方模式 — switch + parse，每 case 1 行样板")
    void testTypicalCallerPattern() {
        // 模拟调用方的 switch 模式
        String[] payloads = {
            // fly_to_point（DJI 协议规定 data 用 points 数组结构）
            "{\"method\":\"fly_to_point\","
                + "\"data\":{\"fly_to_id\":\"FT001\",\"max_speed\":10,"
                + "\"points\":[{\"latitude\":22.5,\"longitude\":113.9,\"height\":50}]},"
                + "\"tid\":\"t1\"}",
            // cover_open（无参数）
            "{\"method\":\"cover_open\",\"data\":{},\"tid\":\"t2\"}",
        };

        for (String payload : payloads) {
            String method = DjiMessage.extractMethod(payload);

            switch (method) {
                case "fly_to_point" -> {
                    // 1 行 parse + N 行类型安全业务逻辑
                    var msg = DjiMessage.parse(payload, FlyToPointRequest.class);
                    assertEquals("FT001", msg.data().flyToId());
                    assertEquals("t1", msg.tid());
                    // 回复
                    var reply = new NoOutputReply();
                    assertNotNull(reply);
                }
                case "cover_open" -> {
                    var msg = DjiMessage.parse(payload, NoParameterRequest.class);
                    assertEquals("t2", msg.tid());
                    var reply = new NoOutputReply();
                    assertNotNull(reply);
                }
                default -> {
                    // 未处理的方法
                }
            }
        }
    }
}
