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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcInfraredFillLightEnableRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_infrared_fill_light_enable} 指令 data JSON（含 payload_index + enable）
 *       能反序列化为 record，snake_case {@code payload_index} 自动映射到 camelCase {@code payloadIndex}</li>
 *   <li>缺失 {@code payload_index} 或 {@code enable} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变（true/false 两种取值）</li>
 * </ol>
 */
class DrcInfraredFillLightEnableRequestTest {

    private static final String PAYLOAD_INDEX = "165-0-7";

    @Test
    @DisplayName("反序列化：{\"payload_index\":\"165-0-7\",\"enable\":true} → payloadIndex/enable 正确绑定")
    void testDeserialize() {
        String json = "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"enable\":true}";
        DrcInfraredFillLightEnableRequest req = MessageCodec.fromJson(json, DrcInfraredFillLightEnableRequest.class);
        assertEquals(PAYLOAD_INDEX, req.payloadIndex());
        assertTrue(req.enable());
    }

    @Test
    @DisplayName("反序列化：enable=false → Boolean 正确绑定")
    void testDeserializeFalse() {
        String json = "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"enable\":false}";
        DrcInfraredFillLightEnableRequest req = MessageCodec.fromJson(json, DrcInfraredFillLightEnableRequest.class);
        assertFalse(req.enable());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"payload_index\" 与 \"enable\" snake_case 字段")
    void testSerialize() {
        DrcInfraredFillLightEnableRequest req = new DrcInfraredFillLightEnableRequest(PAYLOAD_INDEX, true);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"payload_index\":\"" + PAYLOAD_INDEX + "\""),
                "JSON 应含 payload_index，实际: " + json);
        assertTrue(json.contains("\"enable\":true"), "JSON 应含 \"enable\":true，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变（true/false 两种取值）")
    void testRoundTrip() {
        for (Boolean enable : new Boolean[]{true, false}) {
            DrcInfraredFillLightEnableRequest original = new DrcInfraredFillLightEnableRequest(PAYLOAD_INDEX, enable);
            String json = MessageCodec.toJson(original);
            DrcInfraredFillLightEnableRequest back = MessageCodec.fromJson(json, DrcInfraredFillLightEnableRequest.class);
            assertEquals(original, back, "Round-trip 失败: " + enable);
        }
    }

    @Test
    @DisplayName("缺失 payload_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPayloadIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"enable\":true}", DrcInfraredFillLightEnableRequest.class));
    }

    @Test
    @DisplayName("缺失 enable 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingEnableThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"payload_index\":\"" + PAYLOAD_INDEX + "\"}",
                        DrcInfraredFillLightEnableRequest.class));
    }
}
