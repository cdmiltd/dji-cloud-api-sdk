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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.speaker;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcSpeakerPlayVolumeSetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_speaker_play_volume_set} 指令 data JSON（含 psdk_index + play_volume）
 *       能反序列化为 record，snake_case {@code psdk_index} / {@code play_volume}
 *       自动映射到 camelCase {@code psdkIndex} / {@code playVolume}</li>
 *   <li>缺失 {@code psdk_index} 或 {@code play_volume} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class DrcSpeakerPlayVolumeSetRequestTest {

    private static final int PSDK_INDEX = 1;

    @Test
    @DisplayName("反序列化：{\"psdk_index\":1,\"play_volume\":80} → psdkIndex/playVolume 正确绑定")
    void testDeserialize() {
        String json = "{\"psdk_index\":" + PSDK_INDEX + ",\"play_volume\":80}";
        DrcSpeakerPlayVolumeSetRequest req = MessageCodec.fromJson(json, DrcSpeakerPlayVolumeSetRequest.class);
        assertEquals(PSDK_INDEX, req.psdkIndex());
        assertEquals(80, req.playVolume());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"psdk_index\" 与 \"play_volume\" snake_case 字段")
    void testSerialize() {
        DrcSpeakerPlayVolumeSetRequest req = new DrcSpeakerPlayVolumeSetRequest(PSDK_INDEX, 80);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":" + PSDK_INDEX),
                "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"play_volume\":80"),
                "JSON 应含 \"play_volume\":80，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DrcSpeakerPlayVolumeSetRequest original = new DrcSpeakerPlayVolumeSetRequest(PSDK_INDEX, 100);
        String json = MessageCodec.toJson(original);
        DrcSpeakerPlayVolumeSetRequest back = MessageCodec.fromJson(json, DrcSpeakerPlayVolumeSetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 psdk_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPsdkIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"play_volume\":80}", DrcSpeakerPlayVolumeSetRequest.class));
    }

    @Test
    @DisplayName("缺失 play_volume 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPlayVolumeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"psdk_index\":" + PSDK_INDEX + "}",
                        DrcSpeakerPlayVolumeSetRequest.class));
    }
}
