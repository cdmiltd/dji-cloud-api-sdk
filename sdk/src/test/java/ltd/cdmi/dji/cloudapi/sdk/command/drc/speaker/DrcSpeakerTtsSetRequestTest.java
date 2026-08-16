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
 * 验证 {@link DrcSpeakerTtsSetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_speaker_tts_set} 指令 data JSON
 *       （含 psdk_index + volume + type + language + speed 五字段）
 *       能反序列化为 record，snake_case {@code psdk_index} 自动映射到 camelCase {@code psdkIndex}</li>
 *   <li>缺失任一必填字段（psdk_index/volume/type/language/speed）时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class DrcSpeakerTtsSetRequestTest {

    private static final int PSDK_INDEX = 1;

    private static final String SAMPLE_JSON =
            "{\"psdk_index\":1,\"volume\":80,\"type\":0,\"language\":0,\"speed\":5}";

    @Test
    @DisplayName("反序列化：完整 JSON → 五字段正确绑定")
    void testDeserialize() {
        DrcSpeakerTtsSetRequest req = MessageCodec.fromJson(SAMPLE_JSON, DrcSpeakerTtsSetRequest.class);
        assertEquals(PSDK_INDEX, req.psdkIndex());
        assertEquals(80, req.volume());
        assertEquals(0, req.type());
        assertEquals(0, req.language());
        assertEquals(5, req.speed());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 psdk_index/volume/type/language/speed snake_case 字段")
    void testSerialize() {
        DrcSpeakerTtsSetRequest req = new DrcSpeakerTtsSetRequest(PSDK_INDEX, 80, 0, 0, 5);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":" + PSDK_INDEX),
                "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"volume\":80"), "JSON 应含 \"volume\":80，实际: " + json);
        assertTrue(json.contains("\"type\":0"), "JSON 应含 \"type\":0，实际: " + json);
        assertTrue(json.contains("\"language\":0"), "JSON 应含 \"language\":0，实际: " + json);
        assertTrue(json.contains("\"speed\":5"), "JSON 应含 \"speed\":5，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DrcSpeakerTtsSetRequest original = new DrcSpeakerTtsSetRequest(PSDK_INDEX, 100, 1, 1, 10);
        String json = MessageCodec.toJson(original);
        DrcSpeakerTtsSetRequest back = MessageCodec.fromJson(json, DrcSpeakerTtsSetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 psdk_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPsdkIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"volume\":80,\"type\":0,\"language\":0,\"speed\":5}",
                        DrcSpeakerTtsSetRequest.class));
    }

    @Test
    @DisplayName("缺失 volume 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingVolumeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"psdk_index\":" + PSDK_INDEX + ",\"type\":0,\"language\":0,\"speed\":5}",
                        DrcSpeakerTtsSetRequest.class));
    }

    @Test
    @DisplayName("缺失 type 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingTypeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"psdk_index\":" + PSDK_INDEX + ",\"volume\":80,\"language\":0,\"speed\":5}",
                        DrcSpeakerTtsSetRequest.class));
    }

    @Test
    @DisplayName("缺失 language 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingLanguageThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"psdk_index\":" + PSDK_INDEX + ",\"volume\":80,\"type\":0,\"speed\":5}",
                        DrcSpeakerTtsSetRequest.class));
    }

    @Test
    @DisplayName("缺失 speed 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingSpeedThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"psdk_index\":" + PSDK_INDEX + ",\"volume\":80,\"type\":0,\"language\":0}",
                        DrcSpeakerTtsSetRequest.class));
    }
}
