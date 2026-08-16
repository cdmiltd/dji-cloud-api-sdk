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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ConfigRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：config 请求 data（config_type=json, config_scope=product）
 * 能反序列化为 record；缺失任一必填字段时构造器抛 NPE。
 */
class ConfigRequestTest {

    private static final String SAMPLE_JSON =
            "{\"config_type\":\"json\",\"config_scope\":\"product\"}";

    @Test
    @DisplayName("反序列化：{\"config_type\":\"json\",\"config_scope\":\"product\"} → 字段正确绑定")
    void testDeserialize() {
        ConfigRequest req = MessageCodec.fromJson(SAMPLE_JSON, ConfigRequest.class);
        assertEquals("json", req.configType());
        assertEquals("product", req.configScope());
    }

    @Test
    @DisplayName("序列化：ConfigRequest → JSON 含 config_type 与 config_scope")
    void testSerialize() {
        ConfigRequest req = new ConfigRequest("json", "product");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"config_type\":\"json\""), "JSON 应含 config_type，实际: " + json);
        assertTrue(json.contains("\"config_scope\":\"product\""), "JSON 应含 config_scope，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        ConfigRequest original = new ConfigRequest("json", "product");
        String json = MessageCodec.toJson(original);
        ConfigRequest back = MessageCodec.fromJson(json, ConfigRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 config_type 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingConfigTypeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"config_scope\":\"product\"}", ConfigRequest.class));
    }

    @Test
    @DisplayName("缺失 config_scope 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingConfigScopeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"config_type\":\"json\"}", ConfigRequest.class));
    }
}
