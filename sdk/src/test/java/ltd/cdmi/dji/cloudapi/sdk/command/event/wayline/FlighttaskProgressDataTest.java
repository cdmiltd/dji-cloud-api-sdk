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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskProgressData} 的 Jackson 反序列化、序列化与往返闭环（4 层嵌套结构）。
 */
class FlighttaskProgressDataTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"ext\":{\"current_waypoint_index\":1,\"flight_id\":\"flight-001\","
            + "\"media_count\":5,\"track_id\":\"track-001\",\"wayline_id\":10,"
            + "\"wayline_mission_state\":1,\"break_point\":{\"index\":2,\"state\":0,\"progress\":50.5,"
            + "\"wayline_id\":10,\"break_reason\":1,\"latitude\":22.5,\"longitude\":113.9,"
            + "\"height\":50.0,\"attitude_head\":45.0}},"
            + "\"progress\":{\"current_step\":1,\"percent\":50},\"status\":\"in_progress\"}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → 4 层嵌套 record（含 ext/break_point/progress）")
    void testDeserialize() {
        FlighttaskProgressData data = MessageCodec.fromJson(SAMPLE_JSON, FlighttaskProgressData.class);
        assertEquals(0, data.result());
        FlighttaskProgressData.Output output = data.output();
        assertNotNull(output);
        assertEquals("in_progress", output.status());
        assertEquals(1, output.progress().currentStep());
        assertEquals(50, output.progress().percent());
        // ext 嵌套
        FlighttaskProgressData.Ext ext = output.ext();
        assertEquals(1, ext.currentWaypointIndex());
        assertEquals("flight-001", ext.flightId());
        assertEquals(5, ext.mediaCount());
        assertEquals("track-001", ext.trackId());
        assertEquals(10, ext.waylineId());
        assertEquals(1, ext.waylineMissionState());
        // break_point 嵌套（第 4 层）
        FlighttaskProgressData.BreakPoint bp = ext.breakPoint();
        assertEquals(2, bp.index());
        assertEquals(0, bp.state());
        assertEquals(50.5, bp.progress());
        assertEquals(10, bp.waylineId());
        assertEquals(1, bp.breakReason());
        assertEquals(22.5, bp.latitude());
        assertEquals(113.9, bp.longitude());
        assertEquals(50.0, bp.height());
        assertEquals(45.0, bp.attitudeHead());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 flight_id/break_point/current_step/wayline_mission_state）")
    void testSerialize() {
        FlighttaskProgressData.BreakPoint bp = new FlighttaskProgressData.BreakPoint(
                2, 0, 50.5, 10, 1, 22.5, 113.9, 50.0, 45.0);
        FlighttaskProgressData.Ext ext = new FlighttaskProgressData.Ext(
                1, "flight-001", 5, "track-001", 10, 1, bp);
        FlighttaskProgressData.Progress progress = new FlighttaskProgressData.Progress(1, 50);
        FlighttaskProgressData.Output output = new FlighttaskProgressData.Output(ext, progress, "in_progress");
        FlighttaskProgressData data = new FlighttaskProgressData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"flight_id\":\"flight-001\""), "JSON 应含 flight_id，实际: " + json);
        assertTrue(json.contains("\"break_point\""), "JSON 应含 break_point，实际: " + json);
        assertTrue(json.contains("\"current_step\":1"), "JSON 应含 current_step，实际: " + json);
        assertTrue(json.contains("\"wayline_mission_state\":1"), "JSON 应含 wayline_mission_state，实际: " + json);
        assertTrue(json.contains("\"attitude_head\":45.0"), "JSON 应含 attitude_head，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变（4 层嵌套完整保持）")
    void testRoundTrip() {
        FlighttaskProgressData.BreakPoint bp = new FlighttaskProgressData.BreakPoint(
                2, 0, 50.5, 10, 1, 22.5, 113.9, 50.0, 45.0);
        FlighttaskProgressData.Ext ext = new FlighttaskProgressData.Ext(
                1, "flight-001", 5, "track-001", 10, 1, bp);
        FlighttaskProgressData.Progress progress = new FlighttaskProgressData.Progress(1, 50);
        FlighttaskProgressData.Output output = new FlighttaskProgressData.Output(ext, progress, "in_progress");
        FlighttaskProgressData original = new FlighttaskProgressData(0, output);
        String json = MessageCodec.toJson(original);
        FlighttaskProgressData back = MessageCodec.fromJson(json, FlighttaskProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化抛 IllegalStateException")
    void testMissingResultThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"output\":{\"status\":\"ok\"}}", FlighttaskProgressData.class));
    }
}
