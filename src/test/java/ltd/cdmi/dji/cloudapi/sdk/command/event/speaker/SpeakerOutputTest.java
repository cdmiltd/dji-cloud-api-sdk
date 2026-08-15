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

package ltd.cdmi.dji.cloudapi.sdk.command.event.speaker;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link SpeakerOutput} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p>SpeakerOutput 为 speaker_tts_play_start_progress 与 speaker_audio_play_start_progress 事件
 * 共享的 output record，含 SpeakerProgress 嵌套结构。
 */
class SpeakerOutputTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 progress.percent/step_key 嵌套）")
    void testDeserialize() {
        String json = "{\"psdk_index\":1,\"status\":\"in_progress\",\"md5\":\"abc123\","
                + "\"progress\":{\"percent\":50,\"step_key\":\"play\"}}";
        SpeakerOutput output = MessageCodec.fromJson(json, SpeakerOutput.class);
        assertEquals(1, output.psdkIndex());
        assertEquals("in_progress", output.status());
        assertEquals("abc123", output.md5());
        assertEquals(50, output.progress().percent());
        assertEquals("play", output.progress().stepKey());
    }

    @Test
    @DisplayName("反序列化：缺失可选字段 md5/progress 容错")
    void testDeserializeMissingOptionalFields() {
        String json = "{\"psdk_index\":1,\"status\":\"ok\"}";
        SpeakerOutput output = MessageCodec.fromJson(json, SpeakerOutput.class);
        assertEquals(1, output.psdkIndex());
        assertEquals("ok", output.status());
        assertNull(output.md5());
        assertNull(output.progress());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 psdk_index/step_key）")
    void testSerialize() {
        SpeakerOutput.SpeakerProgress progress = new SpeakerOutput.SpeakerProgress(50, "play");
        SpeakerOutput output = new SpeakerOutput(1, "in_progress", "abc123", progress);
        String json = MessageCodec.toJson(output);
        assertTrue(json.contains("\"psdk_index\":1"), "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"step_key\":\"play\""), "JSON 应含 step_key，实际: " + json);
        assertTrue(json.contains("\"md5\":\"abc123\""), "JSON 应含 md5，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        SpeakerOutput.SpeakerProgress progress = new SpeakerOutput.SpeakerProgress(50, "play");
        SpeakerOutput original = new SpeakerOutput(1, "in_progress", "abc123", progress);
        String json = MessageCodec.toJson(original);
        SpeakerOutput back = MessageCodec.fromJson(json, SpeakerOutput.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 psdk_index：反序列化抛 IllegalStateException")
    void testMissingPsdkIndexThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"status\":\"ok\"}", SpeakerOutput.class));
    }
}
