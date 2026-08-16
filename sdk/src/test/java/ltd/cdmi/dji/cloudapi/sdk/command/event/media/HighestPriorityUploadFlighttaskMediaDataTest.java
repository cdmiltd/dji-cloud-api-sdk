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

package ltd.cdmi.dji.cloudapi.sdk.command.event.media;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link HighestPriorityUploadFlighttaskMediaData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class HighestPriorityUploadFlighttaskMediaDataTest {

    @Test
    @DisplayName("反序列化：flight_id → record")
    void testDeserialize() {
        String json = "{\"flight_id\":\"flight-001\"}";
        HighestPriorityUploadFlighttaskMediaData data =
                MessageCodec.fromJson(json, HighestPriorityUploadFlighttaskMediaData.class);
        assertEquals("flight-001", data.flightId());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"flight_id\"")
    void testSerialize() {
        HighestPriorityUploadFlighttaskMediaData data =
                new HighestPriorityUploadFlighttaskMediaData("flight-001");
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"flight_id\":\"flight-001\""), "JSON 应含 flight_id，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        HighestPriorityUploadFlighttaskMediaData original =
                new HighestPriorityUploadFlighttaskMediaData("flight-001");
        String json = MessageCodec.toJson(original);
        HighestPriorityUploadFlighttaskMediaData back = MessageCodec.fromJson(json, HighestPriorityUploadFlighttaskMediaData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 flight_id 字段：反序列化抛 IllegalStateException")
    void testMissingFlightIdThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", HighestPriorityUploadFlighttaskMediaData.class));
    }
}
