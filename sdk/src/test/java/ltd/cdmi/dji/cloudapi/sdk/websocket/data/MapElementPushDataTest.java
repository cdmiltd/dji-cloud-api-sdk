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

package ltd.cdmi.dji.cloudapi.sdk.websocket.data;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MapElementPushData} 地图元素变更推送 data 测试。
 *
 * <p>验证 {@code {id, group_id, name, resource}} 结构的 snake_case ↔ camelCase 映射
 * （重点 {@code group_id} ↔ {@code groupId}），以及 {@code resource} 子结构以
 * {@code Object}（Map）持有。create/update/delete 三个 biz_code 共用同一 POJO。
 */
class MapElementPushDataTest {

    private static final String JSON = "{"
            + "\"id\":\"elem-1\","
            + "\"group_id\":\"group-1\","
            + "\"name\":\"Pin 1\","
            + "\"resource\":{\"type\":\"pin\",\"coordinate\":[22.5,113.9]}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record（group_id → groupId）")
    void testDeserialize() {
        MapElementPushData data = MessageCodec.fromJson(JSON, MapElementPushData.class);
        assertEquals("elem-1", data.id());
        assertEquals("group-1", data.groupId());
        assertEquals("Pin 1", data.name());
        assertNotNull(data.resource());
        assertTrue(data.resource() instanceof Map, "resource 反序列化为 Map");
        assertEquals("pin", ((Map<?, ?>) data.resource()).get("type"));
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（groupId → group_id）")
    void testSerialize() {
        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("type", "pin");
        MapElementPushData data = new MapElementPushData("e1", "g1", "Name", resource);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"id\":\"e1\""));
        assertTrue(json.contains("\"group_id\":\"g1\""));
        assertTrue(json.contains("\"name\":\"Name\""));
        assertTrue(json.contains("\"type\":\"pin\""));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("type", "pin");
        resource.put("id", "r1");
        MapElementPushData original = new MapElementPushData("e1", "g1", "N", resource);
        String json = MessageCodec.toJson(original);
        MapElementPushData round = MessageCodec.fromJson(json, MapElementPushData.class);
        assertEquals(original.id(), round.id());
        assertEquals(original.groupId(), round.groupId());
        assertEquals(original.name(), round.name());
        assertEquals(original.resource(), round.resource());
    }

    @Test
    @DisplayName("parseWs 兼容 create/update/delete 三个 biz_code 共用同一 POJO")
    void testParseWsForAllElementOps() {
        for (String biz : new String[]{"map_element_create", "map_element_update", "map_element_delete"}) {
            String payload = "{\"biz_code\":\"" + biz + "\",\"version\":\"1.0\",\"timestamp\":0,"
                    + "\"data\":{\"id\":\"e1\",\"group_id\":\"g1\",\"name\":\"N\",\"resource\":{}}}";
            var msg = WsPushMessage.parse(payload, MapElementPushData.class);
            assertEquals(biz, msg.bizCode());
            assertEquals("e1", msg.data().id());
            assertEquals("g1", msg.data().groupId());
        }
    }

    @Test
    @DisplayName("record 访问器：id/groupId/name/resource")
    void testRecordAccessors() {
        Object resource = Map.of("type", "pin");
        MapElementPushData data = new MapElementPushData("e", "g", "n", resource);
        assertEquals("e", data.id());
        assertEquals("g", data.groupId());
        assertEquals("n", data.name());
        assertEquals(resource, data.resource());
    }
}
