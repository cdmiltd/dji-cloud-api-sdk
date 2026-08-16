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
 * 验证 {@link DrcSpeakerPlayStopRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_speaker_play_stop} 指令 data JSON（仅含 psdk_index）
 *       能反序列化为 record，snake_case {@code psdk_index} 自动映射到 camelCase {@code psdkIndex}</li>
 *   <li>缺失 {@code psdk_index} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class DrcSpeakerPlayStopRequestTest {

    private static final int PSDK_INDEX = 1;

    @Test
    @DisplayName("反序列化：{\"psdk_index\":1} → psdkIndex 正确绑定")
    void testDeserialize() {
        String json = "{\"psdk_index\":" + PSDK_INDEX + "}";
        DrcSpeakerPlayStopRequest req = MessageCodec.fromJson(json, DrcSpeakerPlayStopRequest.class);
        assertEquals(PSDK_INDEX, req.psdkIndex());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"psdk_index\" snake_case 字段")
    void testSerialize() {
        DrcSpeakerPlayStopRequest req = new DrcSpeakerPlayStopRequest(PSDK_INDEX);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":" + PSDK_INDEX),
                "JSON 应含 psdk_index，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DrcSpeakerPlayStopRequest original = new DrcSpeakerPlayStopRequest(PSDK_INDEX);
        String json = MessageCodec.toJson(original);
        DrcSpeakerPlayStopRequest back = MessageCodec.fromJson(json, DrcSpeakerPlayStopRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 psdk_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPsdkIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", DrcSpeakerPlayStopRequest.class));
    }
}
