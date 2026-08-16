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

package ltd.cdmi.dji.cloudapi.sdk.websocket;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.DeviceOsdPushData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.WsEmptyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WsPushMessage} WebSocket 推送消息信封 record 测试。
 *
 * <p>验证泛型 record 的序列化/反序列化（重点 {@code biz_code} ↔ {@code bizCode}）
 * 与 {@link WsPushMessage#parse} 类型安全解析。
 */
class WsPushMessageTest {

    private static final String JSON = "{"
            + "\"biz_code\":\"device_osd\","
            + "\"version\":\"1.0\","
            + "\"timestamp\":1700000000000,"
            + "\"data\":{\"sn\":\"SN1\"}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record（biz_code → bizCode）")
    @SuppressWarnings("rawtypes")
    void testDeserialize() {
        WsPushMessage msg = MessageCodec.fromJson(JSON, WsPushMessage.class);
        assertEquals("device_osd", msg.bizCode());
        assertEquals("1.0", msg.version());
        assertEquals(1700000000000L, msg.timestamp());
        assertNotNull(msg.data());
        assertTrue(msg.data() instanceof Map, "data 应反序列化为 Map");
        assertEquals("SN1", ((Map<?, ?>) msg.data()).get("sn"));
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（bizCode → biz_code）")
    void testSerialize() {
        WsPushMessage<String> msg = new WsPushMessage<>("map_group_refresh", "1.0", 123L, "payload");
        String json = MessageCodec.toJson(msg);
        assertTrue(json.contains("\"biz_code\":\"map_group_refresh\""));
        assertTrue(json.contains("\"version\":\"1.0\""));
        assertTrue(json.contains("\"timestamp\":123"));
        assertTrue(json.contains("\"data\":\"payload\""));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        WsPushMessage<String> original = new WsPushMessage<>("device_online", "1.0", 999L, "abc");
        String json = MessageCodec.toJson(original);
        WsPushMessage<String> round = MessageCodec.fromJson(json, WsPushMessage.class);
        assertEquals(original.bizCode(), round.bizCode());
        assertEquals(original.version(), round.version());
        assertEquals(original.timestamp(), round.timestamp());
        assertEquals(original.data(), round.data());
    }

    @Test
    @DisplayName("record 访问器：bizCode/version/timestamp/data")
    void testRecordAccessors() {
        WsPushMessage<Integer> msg = new WsPushMessage<>("device_osd", "1.0", 1L, 42);
        assertEquals("device_osd", msg.bizCode());
        assertEquals("1.0", msg.version());
        assertEquals(1L, msg.timestamp());
        assertEquals(42, msg.data());
    }

    // ==================== parseWs 集成 ====================

    @Test
    @DisplayName("parseWs：信封解析 + data 反序列化为指定 POJO（DeviceOsdPushData）")
    void testParseWsTyped() {
        String payload = "{\"biz_code\":\"device_osd\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"sn\":\"1ZND001\",\"host\":{\"latitude\":22.5,\"longitude\":113.9}}}";
        WsPushMessage<DeviceOsdPushData> msg = WsPushMessage.parse(payload, DeviceOsdPushData.class);
        assertEquals("device_osd", msg.bizCode());
        assertEquals("1.0", msg.version());
        assertEquals(1700000000000L, msg.timestamp());
        assertNotNull(msg.data());
        assertEquals("1ZND001", msg.data().sn());
        assertEquals(22.5, msg.data().host().latitude(), 0.001);
    }

    @Test
    @DisplayName("parseWs：JSON 无 data 字段时 data() 为 null")
    void testParseWsMissingData() {
        String payload = "{\"biz_code\":\"device_online\",\"version\":\"1.0\",\"timestamp\":0}";
        WsPushMessage<WsEmptyData> msg = WsPushMessage.parse(payload, WsEmptyData.class);
        assertEquals("device_online", msg.bizCode());
        assertNull(msg.data());
    }

    @Test
    @DisplayName("parseWs：data 为 null 字面量时 data() 为 null")
    void testParseWsNullData() {
        String payload = "{\"biz_code\":\"device_online\",\"version\":\"1.0\",\"timestamp\":0,\"data\":null}";
        WsPushMessage<WsEmptyData> msg = WsPushMessage.parse(payload, WsEmptyData.class);
        assertEquals("device_online", msg.bizCode());
        assertNull(msg.data());
    }
}
