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
import ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link TakeoffToPointProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class TakeoffToPointProgressDataTest {

    private static final String SAMPLE_JSON =
            "{\"status\":\"wayline_progress\",\"result\":0,\"flight_id\":\"flight-001\","
            + "\"track_id\":\"track-001\",\"way_point_index\":1,\"remaining_distance\":120.5,"
            + "\"remaining_time\":30.0,\"planned_path_points\":["
            + "{\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0}]}";

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 flight_id/track_id/planned_path_points）")
    void testDeserialize() {
        TakeoffToPointProgressData data = MessageCodec.fromJson(SAMPLE_JSON, TakeoffToPointProgressData.class);
        assertEquals("wayline_progress", data.status());
        assertEquals(0, data.result());
        assertEquals("flight-001", data.flightId());
        assertEquals("track-001", data.trackId());
        assertEquals(1, data.wayPointIndex());
        assertEquals(120.5, data.remainingDistance());
        assertEquals(30.0, data.remainingTime());
        // 嵌套 planned_path_points
        assertNotNull(data.plannedPathPoints());
        assertEquals(1, data.plannedPathPoints().size());
        PathPoint point = data.plannedPathPoints().get(0);
        assertEquals(22.5, point.latitude());
        assertEquals(113.9, point.longitude());
        assertEquals(50.0, point.height());
    }

    @Test
    @DisplayName("反序列化：多元素 planned_path_points 集合验证")
    void testDeserializeMultiplePathPoints() {
        String json = "{\"status\":\"ok\",\"result\":0,\"flight_id\":\"f\",\"track_id\":\"t\","
                + "\"planned_path_points\":["
                + "{\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0},"
                + "{\"latitude\":22.6,\"longitude\":114.0,\"height\":60.0}]}";
        TakeoffToPointProgressData data = MessageCodec.fromJson(json, TakeoffToPointProgressData.class);
        assertEquals(2, data.plannedPathPoints().size());
        assertEquals(22.6, data.plannedPathPoints().get(1).latitude());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 flight_id/track_id/planned_path_points）")
    void testSerialize() {
        PathPoint point = new PathPoint(22.5, 113.9, 50.0);
        TakeoffToPointProgressData data = new TakeoffToPointProgressData(
                "wayline_progress", 0, "flight-001", "track-001", 1, 120.5, 30.0, List.of(point));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"flight_id\":\"flight-001\""), "JSON 应含 flight_id，实际: " + json);
        assertTrue(json.contains("\"track_id\":\"track-001\""), "JSON 应含 track_id，实际: " + json);
        assertTrue(json.contains("\"planned_path_points\""), "JSON 应含 planned_path_points，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PathPoint point = new PathPoint(22.5, 113.9, 50.0);
        TakeoffToPointProgressData original = new TakeoffToPointProgressData(
                "wayline_progress", 0, "flight-001", "track-001", 1, 120.5, 30.0, List.of(point));
        String json = MessageCodec.toJson(original);
        TakeoffToPointProgressData back = MessageCodec.fromJson(json, TakeoffToPointProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 flight_id：反序列化抛 IllegalStateException")
    void testMissingFlightIdThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"status\":\"ok\",\"result\":0,\"track_id\":\"t\"}",
                        TakeoffToPointProgressData.class));
    }
}
