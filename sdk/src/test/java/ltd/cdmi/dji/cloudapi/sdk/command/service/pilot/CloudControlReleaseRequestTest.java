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
 * 验证 {@link CloudControlReleaseRequest} 的 Jackson 反序列化、序列化与往返闭环。
 */
class CloudControlReleaseRequestTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record")
    void testDeserialize() {
        String json = "{\"control_keys\":[\"flight\"]}";
        CloudControlReleaseRequest req = MessageCodec.fromJson(json, CloudControlReleaseRequest.class);
        assertNotNull(req.controlKeys());
        assertEquals(1, req.controlKeys().size());
        assertEquals("flight", req.controlKeys().get(0));
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON")
    void testSerialize() {
        CloudControlReleaseRequest req = new CloudControlReleaseRequest(List.of("flight"));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"control_keys\""), "应序列化为 control_keys");
        assertTrue(json.contains("\"flight\""), "应包含 flight 值");
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        CloudControlReleaseRequest original = new CloudControlReleaseRequest(List.of("flight"));
        String json = MessageCodec.toJson(original);
        CloudControlReleaseRequest back = MessageCodec.fromJson(json, CloudControlReleaseRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("DJI 文档示例：完整 services 请求 data 反序列化")
    void testDocExample() {
        String json = "{\"control_keys\":[\"flight\"]}";
        CloudControlReleaseRequest req = MessageCodec.fromJson(json, CloudControlReleaseRequest.class);
        assertEquals(List.of("flight"), req.controlKeys());
    }
}
