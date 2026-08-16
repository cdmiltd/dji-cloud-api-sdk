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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link CloudControlAuthNotifyData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class CloudControlAuthNotifyDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record（含 output.status）")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"status\":\"ok\"}}";
        CloudControlAuthNotifyData data = MessageCodec.fromJson(json, CloudControlAuthNotifyData.class);
        assertEquals(0, data.result());
        assertEquals("ok", data.output().status());
    }

    @Test
    @DisplayName("反序列化：output.status=failed 场景")
    void testDeserializeFailedStatus() {
        String json = "{\"result\":1,\"output\":{\"status\":\"failed\"}}";
        CloudControlAuthNotifyData data = MessageCodec.fromJson(json, CloudControlAuthNotifyData.class);
        assertEquals(1, data.result());
        assertEquals("failed", data.output().status());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON")
    void testSerialize() {
        CloudControlAuthNotifyData.Output output = new CloudControlAuthNotifyData.Output("canceled");
        CloudControlAuthNotifyData data = new CloudControlAuthNotifyData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 \"result\":0，实际: " + json);
        assertTrue(json.contains("\"output\""), "JSON 应含 \"output\"，实际: " + json);
        assertTrue(json.contains("\"status\":\"canceled\""), "JSON 应含 \"status\":\"canceled\"，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        CloudControlAuthNotifyData.Output output = new CloudControlAuthNotifyData.Output("ok");
        CloudControlAuthNotifyData original = new CloudControlAuthNotifyData(0, output);
        String json = MessageCodec.toJson(original);
        CloudControlAuthNotifyData back = MessageCodec.fromJson(json, CloudControlAuthNotifyData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化抛 IllegalStateException（requireNonNull 保护）")
    void testMissingResultThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{\"status\":\"ok\"}}", CloudControlAuthNotifyData.class));
    }
}
