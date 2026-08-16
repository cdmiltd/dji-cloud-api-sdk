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

package ltd.cdmi.dji.cloudapi.sdk.command.service.pilot;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 {@link CloudControlAuthRequest} 的 Jackson 反序列化、序列化与往返闭环。
 */
class CloudControlAuthRequestTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record")
    void testDeserialize() {
        String json = "{\"user_id\":\"u123\",\"user_callsign\":\"alpha\",\"control_keys\":[\"flight\"]}";
        CloudControlAuthRequest req = MessageCodec.fromJson(json, CloudControlAuthRequest.class);
        assertEquals("u123", req.userId());
        assertEquals("alpha", req.userCallsign());
        assertEquals(List.of("flight"), req.controlKeys());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON")
    void testSerialize() {
        CloudControlAuthRequest req = new CloudControlAuthRequest("u123", "alpha", List.of("flight"));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"user_id\""), "应序列化为 user_id");
        assertTrue(json.contains("\"user_callsign\""), "应序列化为 user_callsign");
        assertTrue(json.contains("\"control_keys\""), "应序列化为 control_keys");
        assertTrue(json.contains("\"flight\""), "应包含 flight 值");
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        CloudControlAuthRequest original = new CloudControlAuthRequest("u123", "alpha", List.of("flight"));
        String json = MessageCodec.toJson(original);
        CloudControlAuthRequest back = MessageCodec.fromJson(json, CloudControlAuthRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("DJI 文档示例：完整 services 请求 data 反序列化")
    void testDocExample() {
        String json = "{\"user_id\":\"xxxxxxxxxxx\",\"user_callsign\":\"xxxxxxx\",\"control_keys\":[\"flight\"]}";
        CloudControlAuthRequest req = MessageCodec.fromJson(json, CloudControlAuthRequest.class);
        assertEquals("xxxxxxxxxxx", req.userId());
        assertEquals("xxxxxxx", req.userCallsign());
        assertEquals(List.of("flight"), req.controlKeys());
    }
}
