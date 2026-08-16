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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link SpeakerAudioPlayStartProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p>output 字段复用同包共享 record {@link SpeakerOutput}（含 SpeakerProgress 嵌套结构）。
 */
class SpeakerAudioPlayStartProgressDataTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"psdk_index\":1,\"status\":\"in_progress\","
            + "\"md5\":\"abc123\",\"progress\":{\"percent\":50,\"step_key\":\"play\"}}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → record（复用 SpeakerOutput 含 progress 嵌套）")
    void testDeserialize() {
        SpeakerAudioPlayStartProgressData data =
                MessageCodec.fromJson(SAMPLE_JSON, SpeakerAudioPlayStartProgressData.class);
        assertEquals(0, data.result());
        SpeakerOutput output = data.output();
        assertNotNull(output);
        assertEquals(1, output.psdkIndex());
        assertEquals("in_progress", output.status());
        assertEquals("abc123", output.md5());
        // 嵌套 progress
        assertEquals(50, output.progress().percent());
        assertEquals("play", output.progress().stepKey());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 psdk_index/step_key）")
    void testSerialize() {
        SpeakerOutput.SpeakerProgress progress = new SpeakerOutput.SpeakerProgress(50, "play");
        SpeakerOutput output = new SpeakerOutput(1, "in_progress", "abc123", progress);
        SpeakerAudioPlayStartProgressData data = new SpeakerAudioPlayStartProgressData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"psdk_index\":1"), "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"step_key\":\"play\""), "JSON 应含 step_key，实际: " + json);
        assertTrue(json.contains("\"percent\":50"), "JSON 应含 percent，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        SpeakerOutput.SpeakerProgress progress = new SpeakerOutput.SpeakerProgress(50, "play");
        SpeakerOutput output = new SpeakerOutput(1, "in_progress", "abc123", progress);
        SpeakerAudioPlayStartProgressData original = new SpeakerAudioPlayStartProgressData(0, output);
        String json = MessageCodec.toJson(original);
        SpeakerAudioPlayStartProgressData back =
                MessageCodec.fromJson(json, SpeakerAudioPlayStartProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化抛 IllegalStateException")
    void testMissingResultThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"output\":{\"psdk_index\":1,\"status\":\"ok\"}}",
                        SpeakerAudioPlayStartProgressData.class));
    }
}
