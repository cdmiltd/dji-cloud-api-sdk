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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlightAreasDroneLocationData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class FlightAreasDroneLocationDataTest {

    private static final String SAMPLE_JSON =
            "{\"drone_locations\":[{\"area_distance\":120.5,\"area_id\":\"area-001\",\"is_in_area\":true}]}";

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 drone_locations 嵌套列表）")
    void testDeserialize() {
        FlightAreasDroneLocationData data = MessageCodec.fromJson(SAMPLE_JSON, FlightAreasDroneLocationData.class);
        assertNotNull(data.droneLocations());
        assertEquals(1, data.droneLocations().size());
        FlightAreasDroneLocationData.DroneLocationItem item = data.droneLocations().get(0);
        assertEquals(120.5, item.areaDistance());
        assertEquals("area-001", item.areaId());
        assertTrue(item.isInArea());
    }

    @Test
    @DisplayName("反序列化：多元素 drone_locations 集合验证")
    void testDeserializeMultipleItems() {
        String json = "{\"drone_locations\":["
                + "{\"area_distance\":1.0,\"area_id\":\"a\",\"is_in_area\":false},"
                + "{\"area_distance\":2.0,\"area_id\":\"b\",\"is_in_area\":true}]}";
        FlightAreasDroneLocationData data = MessageCodec.fromJson(json, FlightAreasDroneLocationData.class);
        assertEquals(2, data.droneLocations().size());
        assertEquals("a", data.droneLocations().get(0).areaId());
        assertEquals("b", data.droneLocations().get(1).areaId());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 drone_locations/area_id/is_in_area）")
    void testSerialize() {
        FlightAreasDroneLocationData.DroneLocationItem item =
                new FlightAreasDroneLocationData.DroneLocationItem(120.5, "area-001", true);
        FlightAreasDroneLocationData data = new FlightAreasDroneLocationData(List.of(item));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"drone_locations\""), "JSON 应含 drone_locations，实际: " + json);
        assertTrue(json.contains("\"area_id\":\"area-001\""), "JSON 应含 area_id，实际: " + json);
        assertTrue(json.contains("\"is_in_area\":true"), "JSON 应含 is_in_area，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        FlightAreasDroneLocationData.DroneLocationItem item =
                new FlightAreasDroneLocationData.DroneLocationItem(120.5, "area-001", true);
        FlightAreasDroneLocationData original = new FlightAreasDroneLocationData(List.of(item));
        String json = MessageCodec.toJson(original);
        FlightAreasDroneLocationData back = MessageCodec.fromJson(json, FlightAreasDroneLocationData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 drone_locations 字段：反序列化抛 IllegalStateException")
    void testMissingListThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", FlightAreasDroneLocationData.class));
    }
}
