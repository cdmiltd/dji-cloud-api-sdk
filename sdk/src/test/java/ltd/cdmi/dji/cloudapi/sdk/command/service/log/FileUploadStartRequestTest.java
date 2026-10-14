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

package ltd.cdmi.dji.cloudapi.sdk.command.service.log;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 改动 {@link FileUploadStartRequest} 的 wire key 与往返闭环。
 *
 * <p>新增字段：顶层 {@code file_store_dir}/{@code file_type}；
 * {@code FileUploadFile.device_sn}/{@code result}；{@code FileUploadBoot.start_time/end_time/size}。
 */
class FileUploadStartRequestTest {

    @Test
    @DisplayName("序列化：新字段 wire key（file_store_dir/file_type/device_sn/result/start_time/end_time/size）")
    void testSerialize() {
        FileUploadStartRequest req = new FileUploadStartRequest(
                "bucket", "region",
                new FileUploadStartRequest.Credentials("ak", "sk", 3600L, "token"),
                "endpoint", "ali", "log", 0,
                new FileUploadStartRequest.FileUploadParams(List.of(
                        new FileUploadStartRequest.FileUploadFile("0", "SN", 0, "obj",
                                List.of(new FileUploadStartRequest.FileUploadFile.FileUploadBoot(1, 100L, 200L, 300L))))));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"file_store_dir\":\"log\""), json);
        assertTrue(json.contains("\"file_type\":0"), json);
        assertTrue(json.contains("\"device_sn\":\"SN\""), json);
        assertTrue(json.contains("\"start_time\":100"), json);
        assertTrue(json.contains("\"end_time\":200"), json);
        assertTrue(json.contains("\"size\":300"), json);
        assertTrue(json.contains("\"access_key_id\":\"ak\""), json);
        assertEquals(req, MessageCodec.fromJson(json, FileUploadStartRequest.class));
    }

    @Test
    @DisplayName("反序列化：result 可选缺席为 null")
    void testOptionalResultNull() {
        String json = "{\"bucket\":\"b\",\"params\":{\"files\":[{\"module\":\"0\",\"device_sn\":\"SN\"}]}}";
        FileUploadStartRequest req = MessageCodec.fromJson(json, FileUploadStartRequest.class);
        assertEquals("b", req.bucket());
        FileUploadStartRequest.FileUploadFile file = req.params().files().get(0);
        assertEquals("SN", file.deviceSn());
        assertNull(file.result(), "result 缺席应为 null");
    }
}
