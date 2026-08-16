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

package ltd.cdmi.dji.cloudapi.sdk.command.event.system;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link OtaProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class OtaProgressDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 output.status/progress.percent/current_step）")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"status\":\"in_progress\","
                + "\"progress\":{\"percent\":50,\"current_step\":\"download_firmware\"}}}";
        OtaProgressData data = MessageCodec.fromJson(json, OtaProgressData.class);
        assertEquals(0, data.result());
        assertEquals("in_progress", data.output().status());
        assertEquals(50, data.output().progress().percent());
        assertEquals("download_firmware", data.output().progress().currentStep());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 current_step）")
    void testSerialize() {
        OtaProgressData.Progress progress = new OtaProgressData.Progress(50, "download_firmware");
        OtaProgressData.Output output = new OtaProgressData.Output("in_progress", progress);
        OtaProgressData data = new OtaProgressData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"current_step\":\"download_firmware\""), "JSON 应含 current_step，实际: " + json);
        assertTrue(json.contains("\"percent\":50"), "JSON 应含 percent，实际: " + json);
        assertTrue(json.contains("\"status\":\"in_progress\""), "JSON 应含 status，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        OtaProgressData.Progress progress = new OtaProgressData.Progress(75, "upgrade_firmware");
        OtaProgressData.Output output = new OtaProgressData.Output("in_progress", progress);
        OtaProgressData original = new OtaProgressData(0, output);
        String json = MessageCodec.toJson(original);
        OtaProgressData back = MessageCodec.fromJson(json, OtaProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化抛 IllegalStateException")
    void testMissingResultThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"output\":{\"status\":\"ok\"}}", OtaProgressData.class));
    }
}
