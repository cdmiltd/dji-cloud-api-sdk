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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link JoystickInvalidNotifyData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class JoystickInvalidNotifyDataTest {

    @Test
    @DisplayName("反序列化：reason int → record")
    void testDeserialize() {
        String json = "{\"reason\":0}";
        JoystickInvalidNotifyData data = MessageCodec.fromJson(json, JoystickInvalidNotifyData.class);
        assertEquals(0, data.reason());
    }

    @Test
    @DisplayName("反序列化：5 个 reason 枚举值全覆盖（0-4）")
    void testDeserializeAllReasonValues() {
        for (int reason = 0; reason <= 4; reason++) {
            String json = "{\"reason\":" + reason + "}";
            JoystickInvalidNotifyData data = MessageCodec.fromJson(json, JoystickInvalidNotifyData.class);
            assertEquals(reason, data.reason(), "reason=" + reason + " 应正确反序列化");
        }
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"reason\"")
    void testSerialize() {
        JoystickInvalidNotifyData data = new JoystickInvalidNotifyData(1);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"reason\":1"), "JSON 应含 \"reason\":1，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        JoystickInvalidNotifyData original = new JoystickInvalidNotifyData(3);
        String json = MessageCodec.toJson(original);
        JoystickInvalidNotifyData back = MessageCodec.fromJson(json, JoystickInvalidNotifyData.class);
        assertEquals(original, back);
    }
}
