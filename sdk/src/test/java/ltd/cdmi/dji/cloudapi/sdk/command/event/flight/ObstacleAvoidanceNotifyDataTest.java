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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ObstacleAvoidanceNotifyData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class ObstacleAvoidanceNotifyDataTest {

    private static final String SAMPLE_JSON =
            "{\"wayline_uuid\":\"uuid-001\",\"flight_id\":\"flight-001\","
            + "\"obstacles\":[{\"id\":\"obs-1\",\"type\":1,\"timestamp\":1700000000000,"
            + "\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0,"
            + "\"wayline_id\":\"wl-1\",\"waypoint_index\":2}],"
            + "\"is_final_report\":true}";

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 obstacles 嵌套列表）")
    void testDeserialize() {
        ObstacleAvoidanceNotifyData data = MessageCodec.fromJson(SAMPLE_JSON, ObstacleAvoidanceNotifyData.class);
        assertEquals("uuid-001", data.waylineUuid());
        assertEquals("flight-001", data.flightId());
        assertTrue(data.isFinalReport());
        // 嵌套 obstacles 列表
        assertNotNull(data.obstacles());
        assertEquals(1, data.obstacles().size());
        ObstacleAvoidanceNotifyData.ObstacleInfo info = data.obstacles().get(0);
        assertEquals("obs-1", info.id());
        assertEquals(1, info.type());
        assertEquals(1700000000000L, info.timestamp());
        assertEquals(22.5, info.latitude());
        assertEquals(113.9, info.longitude());
        assertEquals(50.0, info.height());
        assertEquals("wl-1", info.waylineId());
        assertEquals(2, info.waypointIndex());
    }

    @Test
    @DisplayName("反序列化：多元素 obstacles 集合验证")
    void testDeserializeMultipleObstacles() {
        String json = "{\"wayline_uuid\":\"u\",\"flight_id\":\"f\","
                + "\"obstacles\":[{\"id\":\"a\",\"type\":1,\"timestamp\":1,\"latitude\":1.0,"
                + "\"longitude\":2.0,\"height\":3.0,\"wayline_id\":\"w\",\"waypoint_index\":0},"
                + "{\"id\":\"b\",\"type\":2,\"timestamp\":2,\"latitude\":4.0,"
                + "\"longitude\":5.0,\"height\":6.0,\"wayline_id\":\"w\",\"waypoint_index\":1}],"
                + "\"is_final_report\":false}";
        ObstacleAvoidanceNotifyData data = MessageCodec.fromJson(json, ObstacleAvoidanceNotifyData.class);
        assertEquals(2, data.obstacles().size());
        assertEquals("a", data.obstacles().get(0).id());
        assertEquals("b", data.obstacles().get(1).id());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 wayline_uuid/is_final_report/waypoint_index）")
    void testSerialize() {
        ObstacleAvoidanceNotifyData.ObstacleInfo info = new ObstacleAvoidanceNotifyData.ObstacleInfo(
                "obs-1", 1, 1700000000000L, 22.5, 113.9, 50.0, "wl-1", 2);
        ObstacleAvoidanceNotifyData data = new ObstacleAvoidanceNotifyData(
                "uuid-001", "flight-001", List.of(info), true);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"wayline_uuid\":\"uuid-001\""), "JSON 应含 wayline_uuid，实际: " + json);
        assertTrue(json.contains("\"is_final_report\":true"), "JSON 应含 is_final_report，实际: " + json);
        assertTrue(json.contains("\"waypoint_index\":2"), "JSON 应含 waypoint_index，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        ObstacleAvoidanceNotifyData.ObstacleInfo info = new ObstacleAvoidanceNotifyData.ObstacleInfo(
                "obs-1", 1, 1700000000000L, 22.5, 113.9, 50.0, "wl-1", 2);
        ObstacleAvoidanceNotifyData original = new ObstacleAvoidanceNotifyData(
                "uuid-001", "flight-001", List.of(info), true);
        String json = MessageCodec.toJson(original);
        ObstacleAvoidanceNotifyData back = MessageCodec.fromJson(json, ObstacleAvoidanceNotifyData.class);
        assertEquals(original, back);
    }
}
