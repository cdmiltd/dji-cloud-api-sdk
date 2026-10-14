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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ConfigReply} 的 Jackson 反序列化、序列化、往返闭环与可空字段。
 *
 * <p><b>核心证明</b>：config 回复 data（{@code app_id/app_key/app_license/ntp_server_host/ntp_server_port}，
 * 非 output 包裹，<b>无 result</b>）能反序列化为 record；省略的可选字段为 null。
 */
class ConfigReplyTest {

    private static final String SAMPLE_JSON =
            "{\"app_id\":\"app123\",\"app_key\":\"key456\",\"app_license\":\"license789\","
            + "\"ntp_server_host\":\"ntp.example.com\",\"ntp_server_port\":123}";

    @Test
    @DisplayName("反序列化：完整 JSON → 字段正确绑定（直挂 data，无 result）")
    void testDeserialize() {
        ConfigReply reply = MessageCodec.fromJson(SAMPLE_JSON, ConfigReply.class);
        assertEquals("app123", reply.appId());
        assertEquals("key456", reply.appKey());
        assertEquals("license789", reply.appLicense());
        assertEquals("ntp.example.com", reply.ntpServerHost());
        assertEquals(123, reply.ntpServerPort());
    }

    @Test
    @DisplayName("序列化：ConfigReply → JSON 含 app_id/app_key/app_license/ntp_server_host/ntp_server_port（snake_case）")
    void testSerialize() {
        ConfigReply reply = new ConfigReply("app123", "key456", "license789", "ntp.example.com", 123);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"app_id\":\"app123\""), "JSON 应含 app_id，实际: " + json);
        assertTrue(json.contains("\"app_key\":\"key456\""), "JSON 应含 app_key，实际: " + json);
        assertTrue(json.contains("\"app_license\":\"license789\""), "JSON 应含 app_license，实际: " + json);
        assertTrue(json.contains("\"ntp_server_host\":\"ntp.example.com\""), "JSON 应含 ntp_server_host，实际: " + json);
        assertTrue(json.contains("\"ntp_server_port\":123"), "JSON 应含 ntp_server_port，实际: " + json);
        assertFalse(json.contains("\"result\""), "JSON 不应含 result 字段，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        ConfigReply original = new ConfigReply("app123", "key456", "license789", "ntp.example.com", 123);
        String json = MessageCodec.toJson(original);
        ConfigReply back = MessageCodec.fromJson(json, ConfigReply.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("可空字段：ntp_server_host/ntp_server_port 省略时为 null（无 requireNonNull）")
    void testOptionalFieldsNull() {
        String json = "{\"app_id\":\"app\",\"app_key\":\"key\",\"app_license\":\"lic\"}";
        ConfigReply reply = MessageCodec.fromJson(json, ConfigReply.class);
        assertEquals("app", reply.appId());
        assertEquals("key", reply.appKey());
        assertEquals("lic", reply.appLicense());
        assertNull(reply.ntpServerHost(), "ntp_server_host 省略时应为 null");
        assertNull(reply.ntpServerPort(), "ntp_server_port 省略时应为 null");
    }
}
