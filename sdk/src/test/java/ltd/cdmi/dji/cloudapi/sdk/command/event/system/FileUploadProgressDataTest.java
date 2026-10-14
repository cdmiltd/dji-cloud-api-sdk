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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 {@link FileUploadProgressData} 的 wire key 与往返闭环。
 */
class FileUploadProgressDataTest {

    @Test
    @DisplayName("反序列化：result + output{status, ext{files[]{...progress}}} 完整结构")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"status\":\"ok\",\"ext\":{\"files\":["
                + "{\"module\":\"media\",\"size\":1024,\"device_sn\":\"SN\",\"key\":\"k\","
                + "\"fingerprint\":\"fp\",\"progress\":{\"progress\":50,\"finish_time\":2000,\"upload_rate\":10}}]}}}";
        FileUploadProgressData data = MessageCodec.fromJson(json, FileUploadProgressData.class);
        assertEquals(0, data.result());
        assertEquals("ok", data.output().status());
        FileUploadProgressData.FileItem item = data.output().ext().files().get(0);
        assertEquals("media", item.module());
        assertEquals(1024L, item.size());
        assertEquals("SN", item.deviceSn());
        assertEquals(50, item.progress().progress());
        assertEquals(2000L, item.progress().finishTime());
        assertEquals(10, item.progress().uploadRate());
    }

    @Test
    @DisplayName("序列化：device_sn / finish_time / upload_rate wire key")
    void testSerialize() {
        FileUploadProgressData data = new FileUploadProgressData(0,
                new FileUploadProgressData.Output("ok",
                        new FileUploadProgressData.Ext(List.of(
                                new FileUploadProgressData.FileItem("media", 1024L, "SN", "k", "fp",
                                        new FileUploadProgressData.Progress(50, 2000L, 10))))));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"device_sn\":\"SN\""), json);
        assertTrue(json.contains("\"finish_time\":2000"), json);
        assertTrue(json.contains("\"upload_rate\":10"), json);
        assertEquals(data, MessageCodec.fromJson(json, FileUploadProgressData.class));
    }
}
