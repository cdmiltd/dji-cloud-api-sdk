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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ConfigReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：config 回复 data（result + app_id + app_license + url + token，
 * 非 output 包裹）能反序列化为 record；缺失 result 时构造器抛 NPE。
 * app_id/app_license 等为可空字段（@Inferred）。
 */
class ConfigReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"app_id\":\"app123\",\"app_license\":\"license456\","
            + "\"url\":\"https://example.com\",\"token\":\"tok\"}";

    @Test
    @DisplayName("反序列化：完整 JSON → 字段正确绑定（app_id/app_license 直挂 data）")
    void testDeserialize() {
        ConfigReply reply = MessageCodec.fromJson(SAMPLE_JSON, ConfigReply.class);
        assertEquals(0, reply.result());
        assertEquals("app123", reply.appId());
        assertEquals("license456", reply.appLicense());
        assertEquals("https://example.com", reply.url());
        assertEquals("tok", reply.token());
    }

    @Test
    @DisplayName("序列化：ConfigReply → JSON 含 app_id 与 app_license（snake_case）")
    void testSerialize() {
        ConfigReply reply = new ConfigReply(0, "app123", "license456", "https://example.com", "tok");
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"app_id\":\"app123\""), "JSON 应含 app_id，实际: " + json);
        assertTrue(json.contains("\"app_license\":\"license456\""), "JSON 应含 app_license，实际: " + json);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        ConfigReply original = new ConfigReply(0, "app123", "license456", "url", "tok");
        String json = MessageCodec.toJson(original);
        ConfigReply back = MessageCodec.fromJson(json, ConfigReply.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("可空字段：url/token 省略时为 null（非必填，无 requireNonNull）")
    void testOptionalFieldsNull() {
        String json = "{\"result\":0,\"app_id\":\"app\",\"app_license\":\"lic\"}";
        ConfigReply reply = MessageCodec.fromJson(json, ConfigReply.class);
        assertEquals(0, reply.result());
        assertEquals("app", reply.appId());
        assertEquals("lic", reply.appLicense());
        assertNull(reply.url(), "url 省略时应为 null");
        assertNull(reply.token(), "token 省略时应为 null");
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"app_id\":\"app\"}", ConfigReply.class));
    }
}
