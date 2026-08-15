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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FileuploadProgressData} 的 Jackson 反序列化、序列化与往返闭环（4 层嵌套 + 列表）。
 */
class FileuploadProgressDataTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"status\":\"in_progress\",\"ext\":{\"files\":["
            + "{\"module\":\"flight_log\",\"size\":1024,\"device_sn\":\"sn-001\","
            + "\"key\":\"log/file.log\",\"fingerprint\":\"fp-001\","
            + "\"progress\":{\"current_step\":1,\"finish_time\":1700000000000,\"progress\":50,"
            + "\"result\":0,\"status\":\"uploading\",\"total_step\":2,\"upload_rate\":100}}]}}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → 4 层嵌套 record（含 ext.files 列表）")
    void testDeserialize() {
        FileuploadProgressData data = MessageCodec.fromJson(SAMPLE_JSON, FileuploadProgressData.class);
        assertEquals(0, data.result());
        assertEquals("in_progress", data.output().status());
        // ext.files 列表
        List<FileuploadProgressData.FileItem> files = data.output().ext().files();
        assertNotNull(files);
        assertEquals(1, files.size());
        FileuploadProgressData.FileItem file = files.get(0);
        assertEquals("flight_log", file.module());
        assertEquals(1024L, file.size());
        assertEquals("sn-001", file.deviceSn());
        assertEquals("log/file.log", file.key());
        assertEquals("fp-001", file.fingerprint());
        // 第 4 层 progress
        FileuploadProgressData.FileProgress progress = file.progress();
        assertEquals(1, progress.currentStep());
        assertEquals(1700000000000L, progress.finishTime());
        assertEquals(50, progress.progress());
        assertEquals(0, progress.result());
        assertEquals("uploading", progress.status());
        assertEquals(2, progress.totalStep());
        assertEquals(100, progress.uploadRate());
    }

    @Test
    @DisplayName("反序列化：多元素 files 集合验证")
    void testDeserializeMultipleFiles() {
        String json = "{\"result\":0,\"output\":{\"status\":\"in_progress\",\"ext\":{\"files\":["
                + "{\"module\":\"a\",\"size\":1,\"device_sn\":\"s1\",\"key\":\"k1\",\"fingerprint\":\"f1\","
                + "\"progress\":{\"current_step\":1,\"finish_time\":1,\"progress\":10,\"result\":0,"
                + "\"status\":\"uploading\",\"total_step\":2,\"upload_rate\":10}},"
                + "{\"module\":\"b\",\"size\":2,\"device_sn\":\"s2\",\"key\":\"k2\",\"fingerprint\":\"f2\","
                + "\"progress\":{\"current_step\":2,\"finish_time\":2,\"progress\":20,\"result\":0,"
                + "\"status\":\"ok\",\"total_step\":2,\"upload_rate\":20}}]}}}";
        FileuploadProgressData data = MessageCodec.fromJson(json, FileuploadProgressData.class);
        assertEquals(2, data.output().ext().files().size());
        assertEquals("a", data.output().ext().files().get(0).module());
        assertEquals("b", data.output().ext().files().get(1).module());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 device_sn/finish_time/upload_rate）")
    void testSerialize() {
        FileuploadProgressData.FileProgress progress = new FileuploadProgressData.FileProgress(
                1, 1700000000000L, 50, 0, "uploading", 2, 100);
        FileuploadProgressData.FileItem file = new FileuploadProgressData.FileItem(
                "flight_log", 1024L, "sn-001", "log/file.log", "fp-001", progress);
        FileuploadProgressData.Ext ext = new FileuploadProgressData.Ext(List.of(file));
        FileuploadProgressData.Output output = new FileuploadProgressData.Output("in_progress", ext);
        FileuploadProgressData data = new FileuploadProgressData(0, output);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"device_sn\":\"sn-001\""), "JSON 应含 device_sn，实际: " + json);
        assertTrue(json.contains("\"finish_time\":1700000000000"), "JSON 应含 finish_time，实际: " + json);
        assertTrue(json.contains("\"upload_rate\":100"), "JSON 应含 upload_rate，实际: " + json);
        assertTrue(json.contains("\"current_step\":1"), "JSON 应含 current_step，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变（4 层嵌套 + 列表完整保持）")
    void testRoundTrip() {
        FileuploadProgressData.FileProgress progress = new FileuploadProgressData.FileProgress(
                1, 1700000000000L, 50, 0, "uploading", 2, 100);
        FileuploadProgressData.FileItem file = new FileuploadProgressData.FileItem(
                "flight_log", 1024L, "sn-001", "log/file.log", "fp-001", progress);
        FileuploadProgressData.Ext ext = new FileuploadProgressData.Ext(List.of(file));
        FileuploadProgressData.Output output = new FileuploadProgressData.Output("in_progress", ext);
        FileuploadProgressData original = new FileuploadProgressData(0, output);
        String json = MessageCodec.toJson(original);
        FileuploadProgressData back = MessageCodec.fromJson(json, FileuploadProgressData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化抛 IllegalStateException")
    void testMissingResultThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"output\":{\"status\":\"ok\"}}", FileuploadProgressData.class));
    }
}
