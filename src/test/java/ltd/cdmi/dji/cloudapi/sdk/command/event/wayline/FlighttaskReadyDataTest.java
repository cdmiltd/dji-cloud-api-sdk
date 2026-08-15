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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskReadyData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class FlighttaskReadyDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 flight_ids 字符串列表）")
    void testDeserialize() {
        String json = "{\"flight_ids\":[\"flight-001\",\"flight-002\"]}";
        FlighttaskReadyData data = MessageCodec.fromJson(json, FlighttaskReadyData.class);
        assertNotNull(data.flightIds());
        assertEquals(2, data.flightIds().size());
        assertEquals("flight-001", data.flightIds().get(0));
        assertEquals("flight-002", data.flightIds().get(1));
    }

    @Test
    @DisplayName("反序列化：空 flight_ids 列表")
    void testDeserializeEmptyList() {
        String json = "{\"flight_ids\":[]}";
        FlighttaskReadyData data = MessageCodec.fromJson(json, FlighttaskReadyData.class);
        assertNotNull(data.flightIds());
        assertTrue(data.flightIds().isEmpty());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 flight_ids）")
    void testSerialize() {
        FlighttaskReadyData data = new FlighttaskReadyData(List.of("flight-001", "flight-002"));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"flight_ids\""), "JSON 应含 flight_ids，实际: " + json);
        assertTrue(json.contains("\"flight-001\""), "JSON 应含 flight-001，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        FlighttaskReadyData original = new FlighttaskReadyData(List.of("flight-001", "flight-002"));
        String json = MessageCodec.toJson(original);
        FlighttaskReadyData back = MessageCodec.fromJson(json, FlighttaskReadyData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 flight_ids 字段：反序列化抛 IllegalStateException")
    void testMissingFlightIdsThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", FlighttaskReadyData.class));
    }
}
