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
 * 验证 {@link InFlightWaylineProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class InFlightWaylineProgressDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 in_flight_wayline_id/progress.percent）")
    void testDeserialize() {
        String json = "{\"in_flight_wayline_id\":\"wl-001\",\"progress\":{\"percent\":60},"
                + "\"status\":0,\"result\":0,\"way_point_index\":3}";
        InFlightWaylineProgressData data = MessageCodec.fromJson(json, InFlightWaylineProgressData.class);
        assertEquals("wl-001", data.inFlightWaylineId());
        assertNotNull(data.progress());
        assertEquals(60, data.progress().percent());
        assertEquals(0, data.status());
        assertEquals(0, data.result());
        assertEquals(3, data.wayPointIndex());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 in_flight_wayline_id/way_point_index）")
    void testSerialize() {
        InFlightWaylineProgressData.Progress progress = new InFlightWaylineProgressData.Progress(60);
        InFlightWaylineProgressData data = new InFlightWaylineProgressData(
                "wl-001", progress, 0, 0, 3);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"in_flight_wayline_id\":\"wl-001\""), "JSON 应含 in_flight_wayline_id，实际: " + json);
        assertTrue(json.contains("\"way_point_index\":3"), "JSON 应含 way_point_index，实际: " + json);
        assertTrue(json.contains("\"percent\":60"), "JSON 应含 percent，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        InFlightWaylineProgressData.Progress progress = new InFlightWaylineProgressData.Progress(75);
        InFlightWaylineProgressData original = new InFlightWaylineProgressData(
                "wl-001", progress, 1, 0, 5);
        String json = MessageCodec.toJson(original);
        InFlightWaylineProgressData back = MessageCodec.fromJson(json, InFlightWaylineProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 in_flight_wayline_id：反序列化抛 IllegalStateException")
    void testMissingInFlightWaylineIdThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"progress\":{\"percent\":60}}", InFlightWaylineProgressData.class));
    }
}
