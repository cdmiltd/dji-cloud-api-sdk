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

package ltd.cdmi.dji.cloudapi.sdk.command.request.wayline;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskResourceGetRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：flighttask_resource_get 请求 data（flight_id 必填）
 * 能反序列化为 record；缺失 flight_id 时构造器抛 NPE。
 */
class FlighttaskResourceGetRequestTest {

    private static final String SAMPLE_JSON = "{\"flight_id\":\"FLIGHT001\"}";

    @Test
    @DisplayName("反序列化：{\"flight_id\":\"FLIGHT001\"} → flightId 正确绑定")
    void testDeserialize() {
        FlighttaskResourceGetRequest req = MessageCodec.fromJson(SAMPLE_JSON, FlighttaskResourceGetRequest.class);
        assertEquals("FLIGHT001", req.flightId());
    }

    @Test
    @DisplayName("序列化：FlighttaskResourceGetRequest → JSON 含 flight_id")
    void testSerialize() {
        FlighttaskResourceGetRequest req = new FlighttaskResourceGetRequest("FLIGHT001");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"flight_id\":\"FLIGHT001\""), "JSON 应含 flight_id，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        FlighttaskResourceGetRequest original = new FlighttaskResourceGetRequest("FLIGHT001");
        String json = MessageCodec.toJson(original);
        FlighttaskResourceGetRequest back = MessageCodec.fromJson(json, FlighttaskResourceGetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 flight_id 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingFlightIdThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", FlighttaskResourceGetRequest.class));
    }
}
