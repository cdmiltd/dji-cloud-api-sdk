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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcCameraNightModeSetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_camera_night_mode_set} 指令 data JSON（含 payload_index + mode）
 *       能反序列化为 record，snake_case {@code payload_index} 自动映射到 camelCase {@code payloadIndex}</li>
 *   <li>缺失 {@code payload_index} 或 {@code mode} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class DrcCameraNightModeSetRequestTest {

    private static final String PAYLOAD_INDEX = "165-0-7";

    @Test
    @DisplayName("反序列化：{\"payload_index\":\"165-0-7\",\"mode\":2} → payloadIndex/mode 正确绑定")
    void testDeserialize() {
        String json = "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"mode\":2}";
        DrcCameraNightModeSetRequest req = MessageCodec.fromJson(json, DrcCameraNightModeSetRequest.class);
        assertEquals(PAYLOAD_INDEX, req.payloadIndex());
        assertEquals(2, req.mode());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"payload_index\" 与 \"mode\" snake_case 字段")
    void testSerialize() {
        DrcCameraNightModeSetRequest req = new DrcCameraNightModeSetRequest(PAYLOAD_INDEX, 2);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"payload_index\":\"" + PAYLOAD_INDEX + "\""),
                "JSON 应含 payload_index，实际: " + json);
        assertTrue(json.contains("\"mode\":2"), "JSON 应含 \"mode\":2，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DrcCameraNightModeSetRequest original = new DrcCameraNightModeSetRequest(PAYLOAD_INDEX, 0);
        String json = MessageCodec.toJson(original);
        DrcCameraNightModeSetRequest back = MessageCodec.fromJson(json, DrcCameraNightModeSetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 payload_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPayloadIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"mode\":1}", DrcCameraNightModeSetRequest.class));
    }

    @Test
    @DisplayName("缺失 mode 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingModeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"payload_index\":\"" + PAYLOAD_INDEX + "\"}",
                        DrcCameraNightModeSetRequest.class));
    }
}
