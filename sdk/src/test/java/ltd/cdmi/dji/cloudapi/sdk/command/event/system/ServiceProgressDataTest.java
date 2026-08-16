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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ServiceProgressData} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p>本 POJO 覆盖 13 个设备管理类 service 指令的通用进度结构。
 */
class ServiceProgressDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 output.status/progress.percent/step_key）")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"status\":\"in_progress\","
                + "\"progress\":{\"percent\":50,\"step_key\":\"download_firmware\"}}}";
        ServiceProgressData data = MessageCodec.fromJson(json, ServiceProgressData.class);
        assertEquals(0, data.result());
        assertEquals("in_progress", data.output().status());
        assertEquals(50, data.output().progress().percent());
        assertEquals("download_firmware", data.output().progress().stepKey());
    }

    @Test
    @DisplayName("反序列化：step_key 可选字段缺失容错")
    void testDeserializeMissingStepKey() {
        String json = "{\"result\":0,\"output\":{\"status\":\"ok\",\"progress\":{\"percent\":100}}}";
        ServiceProgressData data = MessageCodec.fromJson(json, ServiceProgressData.class);
        assertEquals("ok", data.output().status());
        assertEquals(100, data.output().progress().percent());
        assertTrue(null == data.output().progress().stepKey(), "step_key 缺失应为 null");
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 step_key）")
    void testSerialize() {
        ServiceProgressData.Progress progress = new ServiceProgressData.Progress(50, "download_firmware");
        ServiceProgressData.Output output = new ServiceProgressData.Output("in_progress", progress);
        ServiceProgressData data = new ServiceProgressData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"step_key\":\"download_firmware\""), "JSON 应含 step_key，实际: " + json);
        assertTrue(json.contains("\"percent\":50"), "JSON 应含 percent，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        ServiceProgressData.Progress progress = new ServiceProgressData.Progress(75, "upgrade_firmware");
        ServiceProgressData.Output output = new ServiceProgressData.Output("in_progress", progress);
        ServiceProgressData original = new ServiceProgressData(0, output);
        String json = MessageCodec.toJson(original);
        ServiceProgressData back = MessageCodec.fromJson(json, ServiceProgressData.class);
        assertEquals(original, back);
    }
}
