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

import ltd.cdmi.dji.cloudapi.sdk.websocket.WsBizCode;
import ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.DeviceOsdPushData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.MapElementPushData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.MapGroupRefreshData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.WsEmptyData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link MessageCodec#parseWs(String, Class)} 的 WebSocket 推送类型安全解析。
 *
 * <p><b>核心证明</b>：{@code parseWs} 返回 {@link WsPushMessage}<T>，其 {@code data()}
 * 是调用方指定的 POJO 类型（编译期确定）。{@code msg.data().host().latitude()} 编译通过
 * 即证明类型安全——无需 cast，无需 instanceof。
 *
 * <p>这是与 MQTT 通道 {@code parse} 对称的 WebSocket 通道 API：每个 switch case 只需
 * 1 行 parseWs 调用，其余是类型安全的业务代码。SDK 不接管路由，调用方用 switch 按
 * {@link WsBizCode} 分发。
 *
 * <p>对照历史决策：本测试佐证"方案 1b（parseWs + 泛型 WsPushMessage<T>）"真正消除
 * instanceof——而非 registry 隐式转换的"类型安全假象"（data 仍 Object，调用方仍要 cast）。
 */
class MessageCodecParseWsTest {

    // ==================== 类型安全证明 ====================

    @Test
    @DisplayName("parseWs 返回 WsPushMessage<DeviceOsdPushData> — data().host().latitude() 直接访问，无 cast")
    void testParseWsReturnsTypedData() {
        // Given: device_osd 推送（data 结构来自 simulator SituationAwarenessWsHandler 已验证字段）
        String payload = "{\"biz_code\":\"device_osd\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"sn\":\"1ZND001\",\"host\":{"
                + "\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0,"
                + "\"attitude_head\":180.0,\"elevation\":100.0,"
                + "\"horizontal_speed\":10.0,\"vertical_speed\":0.0}}}";

        // When: parseWs 指定 DeviceOsdPushData.class
        WsPushMessage<DeviceOsdPushData> msg = MessageCodec.parseWs(payload, DeviceOsdPushData.class);

        // Then: data() 是 DeviceOsdPushData，直接访问嵌套字段——无 cast，无 instanceof
        // 如果 data() 是 Object，以下行不会编译
        assertEquals("device_osd", msg.bizCode());
        assertEquals("1.0", msg.version());
        assertEquals(1700000000000L, msg.timestamp());
        assertEquals("1ZND001", msg.data().sn());
        assertEquals(22.5, msg.data().host().latitude(), 0.001);
        assertEquals(113.9, msg.data().host().longitude(), 0.001);
        assertEquals(50.0, msg.data().host().height(), 0.001);
        assertEquals(180.0, msg.data().host().attitudeHead(), 0.001);
        assertEquals(10.0, msg.data().host().horizontalSpeed(), 0.001);
    }

    @Test
    @DisplayName("parseWs 返回 WsPushMessage<MapGroupRefreshData> — data().ids() 直接访问，无 cast")
    void testParseWsMapGroupRefresh() {
        String payload = "{\"biz_code\":\"map_group_refresh\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"ids\":[\"group-1\",\"group-2\"]}}";

        WsPushMessage<MapGroupRefreshData> msg = MessageCodec.parseWs(payload, MapGroupRefreshData.class);

        assertEquals("map_group_refresh", msg.bizCode());
        assertEquals(2, msg.data().ids().size());
        assertEquals("group-1", msg.data().ids().get(0));
        assertEquals("group-2", msg.data().ids().get(1));
    }

    @Test
    @DisplayName("parseWs 返回 WsPushMessage<MapElementPushData> — create/update/delete 共用同一 POJO")
    void testParseWsMapElementCreate() {
        String payload = "{\"biz_code\":\"map_element_create\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"id\":\"elem-1\",\"group_id\":\"group-1\",\"name\":\"Pin 1\",\"resource\":{}}}";

        WsPushMessage<MapElementPushData> msg = MessageCodec.parseWs(payload, MapElementPushData.class);

        assertEquals("map_element_create", msg.bizCode());
        assertEquals("elem-1", msg.data().id());
        assertEquals("group-1", msg.data().groupId());
        assertEquals("Pin 1", msg.data().name());
        // resource 子结构 simulator 未访问字段，SDK 用 Object 持有（@Inferred，待验证）
        assertNotNull(msg.data().resource());
    }

    // ==================== 空 data 场景 ====================

    @Test
    @DisplayName("device_online/offline/update_topo 的 data 为空对象 — 用 WsEmptyData 承接")
    void testParseWsEmptyData() {
        String payload = "{\"biz_code\":\"device_online\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{}}";

        WsPushMessage<WsEmptyData> msg = MessageCodec.parseWs(payload, WsEmptyData.class);

        assertEquals("device_online", msg.bizCode());
        assertNotNull(msg.data(), "空对象 data 应反序列化为空 record 实例（非 null）");
    }

    // ==================== 边界场景 ====================

    @Test
    @DisplayName("JSON 无 data 字段 — msg.data() 为 null")
    void testParseWsMissingDataField() {
        String payload = "{\"biz_code\":\"device_online\",\"version\":\"1.0\",\"timestamp\":1700000000000}";

        WsPushMessage<WsEmptyData> msg = MessageCodec.parseWs(payload, WsEmptyData.class);

        assertEquals("device_online", msg.bizCode());
        assertNull(msg.data(), "JSON 无 data 字段时 data() 应为 null");
    }

    @Test
    @DisplayName("无效 JSON — 抛出 IllegalStateException")
    void testParseWsInvalidJsonThrows() {
        String invalidJson = "{broken json";

        assertThrows(IllegalStateException.class,
                () -> MessageCodec.parseWs(invalidJson, DeviceOsdPushData.class),
                "无效 JSON 应抛出 IllegalStateException");
    }

    // ==================== 典型调用方代码模式 ====================

    @Test
    @DisplayName("典型调用方模式 — switch bizCode + parseWs，每 case 1 行样板")
    void testTypicalCallerPattern() {
        String[] payloads = {
            // device_osd
            "{\"biz_code\":\"device_osd\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"sn\":\"SN1\",\"host\":{\"latitude\":22.5,\"longitude\":113.9,"
                + "\"height\":50.0,\"attitude_head\":180.0,\"elevation\":100.0,"
                + "\"horizontal_speed\":10.0,\"vertical_speed\":0.0}}}",
            // map_group_refresh
            "{\"biz_code\":\"map_group_refresh\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"ids\":[\"g1\"]}}",
            // device_online（空 data）
            "{\"biz_code\":\"device_online\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{}}",
        };

        for (String payload : payloads) {
            // 先 peek biz_code 决定用哪个 POJO——与 MQTT 的 extractMethod 对称
            String bizCode = MessageCodec.extractBizCode(payload);
            var code = WsBizCode.fromCode(bizCode);

            switch (code.orElse(null)) {
                case DEVICE_OSD -> {
                    // 1 行 parseWs + N 行类型安全业务逻辑
                    var msg = MessageCodec.parseWs(payload, DeviceOsdPushData.class);
                    assertEquals("device_osd", msg.bizCode());
                    assertNotNull(msg.data().host());
                }
                case MAP_GROUP_REFRESH -> {
                    var msg = MessageCodec.parseWs(payload, MapGroupRefreshData.class);
                    assertEquals(1, msg.data().ids().size());
                }
                case DEVICE_ONLINE, DEVICE_OFFLINE, DEVICE_UPDATE_TOPO -> {
                    var msg = MessageCodec.parseWs(payload, WsEmptyData.class);
                    assertNotNull(msg.data());
                }
                case null -> {
                    // 未处理的 biz_code
                }
                default -> {
                }
            }
        }
    }
}
