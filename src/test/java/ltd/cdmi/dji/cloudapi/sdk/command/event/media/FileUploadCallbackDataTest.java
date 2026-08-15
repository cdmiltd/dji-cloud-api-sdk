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

package ltd.cdmi.dji.cloudapi.sdk.command.event.media;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FileUploadCallbackData} 的 Jackson 反序列化、序列化与往返闭环（4 层嵌套结构）。
 */
class FileUploadCallbackDataTest {

    private static final String SAMPLE_JSON =
            "{\"file\":{\"cloud_to_cloud_id\":\"ccid-001\","
            + "\"ext\":{\"drone_model_key\":\"0-67-0\",\"flight_id\":\"flight-001\","
            + "\"is_original\":true,\"payload_model_key\":\"1-42-0\"},"
            + "\"metadata\":{\"absolute_altitude\":100.5,\"create_time\":\"2026-08-15 10:00:00\","
            + "\"gimbal_yaw_degree\":\"45.0\",\"relative_altitude\":50.0,"
            + "\"shoot_position\":{\"lat\":22.5,\"lng\":113.9}},"
            + "\"name\":\"IMG_0001.jpg\",\"object_key\":\"media/IMG_0001.jpg\",\"path\":\"/media\"}}";

    @Test
    @DisplayName("反序列化：snake_case JSON → 4 层嵌套 record")
    void testDeserialize() {
        FileUploadCallbackData data = MessageCodec.fromJson(SAMPLE_JSON, FileUploadCallbackData.class);
        FileUploadCallbackData.MediaFile file = data.file();
        assertNotNull(file);
        assertEquals("ccid-001", file.cloudToCloudId());
        assertEquals("IMG_0001.jpg", file.name());
        assertEquals("media/IMG_0001.jpg", file.objectKey());
        assertEquals("/media", file.path());
        // 第 2 层 ext
        assertEquals("0-67-0", file.ext().droneModelKey());
        assertEquals("flight-001", file.ext().flightId());
        assertTrue(file.ext().isOriginal());
        assertEquals("1-42-0", file.ext().payloadModelKey());
        // 第 3 层 metadata
        assertEquals(100.5, file.metadata().absoluteAltitude());
        assertEquals("2026-08-15 10:00:00", file.metadata().createTime());
        assertEquals("45.0", file.metadata().gimbalYawDegree());
        assertEquals(50.0, file.metadata().relativeAltitude());
        // 第 4 层 shoot_position
        assertEquals(22.5, file.metadata().shootPosition().lat());
        assertEquals(113.9, file.metadata().shootPosition().lng());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 cloud_to_cloud_id/gimbal_yaw_degree/shoot_position）")
    void testSerialize() {
        FileUploadCallbackData.ShootPosition pos = new FileUploadCallbackData.ShootPosition(22.5, 113.9);
        FileUploadCallbackData.MediaFileMetadata metadata = new FileUploadCallbackData.MediaFileMetadata(
                100.5, "2026-08-15 10:00:00", "45.0", 50.0, pos);
        FileUploadCallbackData.MediaFileExt ext = new FileUploadCallbackData.MediaFileExt(
                "0-67-0", "flight-001", true, "1-42-0");
        FileUploadCallbackData.MediaFile file = new FileUploadCallbackData.MediaFile(
                "ccid-001", ext, metadata, "IMG_0001.jpg", "media/IMG_0001.jpg", "/media");
        FileUploadCallbackData data = new FileUploadCallbackData(file);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"cloud_to_cloud_id\":\"ccid-001\""), "JSON 应含 cloud_to_cloud_id，实际: " + json);
        assertTrue(json.contains("\"gimbal_yaw_degree\":\"45.0\""), "JSON 应含 gimbal_yaw_degree，实际: " + json);
        assertTrue(json.contains("\"shoot_position\""), "JSON 应含 shoot_position，实际: " + json);
        assertTrue(json.contains("\"is_original\":true"), "JSON 应含 is_original，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变（4 层嵌套完整保持）")
    void testRoundTrip() {
        FileUploadCallbackData.ShootPosition pos = new FileUploadCallbackData.ShootPosition(22.5, 113.9);
        FileUploadCallbackData.MediaFileMetadata metadata = new FileUploadCallbackData.MediaFileMetadata(
                100.5, "2026-08-15 10:00:00", "45.0", 50.0, pos);
        FileUploadCallbackData.MediaFileExt ext = new FileUploadCallbackData.MediaFileExt(
                "0-67-0", "flight-001", true, "1-42-0");
        FileUploadCallbackData.MediaFile file = new FileUploadCallbackData.MediaFile(
                "ccid-001", ext, metadata, "IMG_0001.jpg", "media/IMG_0001.jpg", "/media");
        FileUploadCallbackData original = new FileUploadCallbackData(file);
        String json = MessageCodec.toJson(original);
        FileUploadCallbackData back = MessageCodec.fromJson(json, FileUploadCallbackData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 file 字段：反序列化抛 IllegalStateException")
    void testMissingFileThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", FileUploadCallbackData.class));
    }
}
