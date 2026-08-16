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

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlightAreasSyncProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class FlightAreasSyncProgressDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 file.name/checksum 嵌套）")
    void testDeserialize() {
        String json = "{\"status\":\"synchronized\",\"reason\":0,"
                + "\"file\":{\"name\":\"areas.geofence\",\"checksum\":\"abc123\"}}";
        FlightAreasSyncProgressData data = MessageCodec.fromJson(json, FlightAreasSyncProgressData.class);
        assertEquals("synchronized", data.status());
        assertEquals(0, data.reason());
        assertEquals("areas.geofence", data.file().name());
        assertEquals("abc123", data.file().checksum());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 status/file/checksum）")
    void testSerialize() {
        FlightAreasSyncProgressData.SyncFile file =
                new FlightAreasSyncProgressData.SyncFile("areas.geofence", "abc123");
        FlightAreasSyncProgressData data = new FlightAreasSyncProgressData("synchronized", 0, file);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"status\":\"synchronized\""), "JSON 应含 status，实际: " + json);
        assertTrue(json.contains("\"file\""), "JSON 应含 file，实际: " + json);
        assertTrue(json.contains("\"checksum\":\"abc123\""), "JSON 应含 checksum，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        FlightAreasSyncProgressData.SyncFile file =
                new FlightAreasSyncProgressData.SyncFile("areas.geofence", "abc123");
        FlightAreasSyncProgressData original = new FlightAreasSyncProgressData("synchronized", 0, file);
        String json = MessageCodec.toJson(original);
        FlightAreasSyncProgressData back = MessageCodec.fromJson(json, FlightAreasSyncProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 status：反序列化抛 IllegalStateException")
    void testMissingStatusThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"reason\":0}", FlightAreasSyncProgressData.class));
    }
}
