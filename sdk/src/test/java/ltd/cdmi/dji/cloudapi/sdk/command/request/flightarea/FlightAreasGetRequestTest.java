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

package ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlightAreasGetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：flight_areas_get 请求 data 为空（无参数），
 * 无参 record 能正确序列化/反序列化。
 */
class FlightAreasGetRequestTest {

    @Test
    @DisplayName("反序列化：空 JSON {} → 无参 record")
    void testDeserialize() {
        FlightAreasGetRequest req = MessageCodec.fromJson("{}", FlightAreasGetRequest.class);
        // 无参 record，仅需验证不抛异常
        assertTrue(req != null, "反序列化结果不应为 null");
    }

    @Test
    @DisplayName("序列化：无参 record → JSON 为空对象 {}")
    void testSerialize() {
        FlightAreasGetRequest req = new FlightAreasGetRequest();
        String json = MessageCodec.toJson(req);
        assertEquals("{}", json, "无参 record 序列化应为 {}，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        FlightAreasGetRequest original = new FlightAreasGetRequest();
        String json = MessageCodec.toJson(original);
        FlightAreasGetRequest back = MessageCodec.fromJson(json, FlightAreasGetRequest.class);
        assertEquals(original, back);
    }
}
