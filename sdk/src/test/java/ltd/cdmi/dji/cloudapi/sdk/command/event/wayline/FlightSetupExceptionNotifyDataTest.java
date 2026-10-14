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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 {@link FlightSetupExceptionNotifyData} 的 wire key 与往返闭环。
 */
class FlightSetupExceptionNotifyDataTest {

    @Test
    @DisplayName("反序列化：sn/timeout_time/timestamp/flight_type")
    void testDeserialize() {
        String json = "{\"sn\":\"SN\",\"timeout_time\":60,\"timestamp\":1700000000000,\"flight_type\":1}";
        FlightSetupExceptionNotifyData data = MessageCodec.fromJson(json, FlightSetupExceptionNotifyData.class);
        assertEquals("SN", data.sn());
        assertEquals(60, data.timeoutTime());
        assertEquals(1700000000000L, data.timestamp());
        assertEquals(1, data.flightType());
    }

    @Test
    @DisplayName("序列化：wire key timeout_time 与往返")
    void testSerialize() {
        FlightSetupExceptionNotifyData data = new FlightSetupExceptionNotifyData("SN", 60, 1700000000000L, 2);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"timeout_time\":60"), json);
        assertTrue(json.contains("\"flight_type\":2"), json);
        assertEquals(data, MessageCodec.fromJson(json, FlightSetupExceptionNotifyData.class));
    }
}
