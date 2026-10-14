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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * v1.18.0.0 新增飞行器嵌套结构解析测试：
 * {@link FtsInfo}（快传）与 {@link CameraInfo} 的 M4D/M4TD 扩增字段 + {@link IrMeteringPoint}/{@link IrMeteringArea}。
 */
class DroneNestedV118Test {

    @Test
    @DisplayName("FtsInfo：字段名与类型映射")
    void testParseFtsInfo() {
        String json = "{\"fts_status\":1,\"fts_verification_code\":\"AB12CD34\","
                + "\"fts_valid_duration\":600,\"fts_timestamp\":1700000000,"
                + "\"fts_sn\":\"fts_sn_1\",\"fts_firmware_version\":\"1.0.0\"}";

        FtsInfo info = MessageCodec.fromJson(json, FtsInfo.class);

        assertEquals(1, info.ftsStatus());
        assertEquals("AB12CD34", info.ftsVerificationCode());
        assertEquals(600, info.ftsValidDuration());
        assertEquals(1700000000, info.ftsTimestamp());
        assertEquals("fts_sn_1", info.ftsSn());
        assertEquals("1.0.0", info.ftsFirmwareVersion());
    }

    @Test
    @DisplayName("CameraInfo：M4D/M4TD 扩增字段 + 嵌套红外测温")
    void testParseCameraInfoExpandedFields() {
        String json = "{\"payload_index\":\"99-0-0\",\"camera_mode\":1,"
                + "\"zoom_factor\":2.5,\"ir_zoom_factor\":2.0,"
                + "\"photo_storage_settings\":[\"current\",\"vision\"],"
                + "\"video_storage_settings\":[\"current\"],"
                + "\"wide_exposure_mode\":1,\"wide_iso\":8,\"wide_shutter_speed\":45,\"wide_exposure_value\":16,"
                + "\"zoom_exposure_mode\":2,\"zoom_iso\":8,\"zoom_shutter_speed\":45,\"zoom_exposure_value\":16,"
                + "\"zoom_focus_mode\":1,\"zoom_focus_value\":100,\"zoom_max_focus_value\":200,"
                + "\"zoom_min_focus_value\":0,\"zoom_calibrate_farthest_focus_value\":34,"
                + "\"zoom_calibrate_nearest_focus_value\":64,\"zoom_focus_state\":2,"
                + "\"ir_metering_mode\":2,"
                + "\"ir_metering_point\":{\"x\":0.1,\"y\":0.2,\"temperature\":36.5},"
                + "\"ir_metering_area\":{\"x\":0.1,\"y\":0.2,\"width\":0.3,\"height\":0.4,"
                + "\"aver_temperature\":30.0,"
                + "\"min_temperature_point\":{\"x\":0.11,\"y\":0.21,\"temperature\":28.0},"
                + "\"max_temperature_point\":{\"x\":0.12,\"y\":0.22,\"temperature\":40.0}}}";

        CameraInfo info = MessageCodec.fromJson(json, CameraInfo.class);

        assertEquals("99-0-0", info.payloadIndex());
        assertEquals(2.5, info.zoomFactor(), 0.001);
        assertEquals(2.0, info.irZoomFactor(), 0.001);
        assertEquals(2, info.photoStorageSettings().size());
        assertEquals("vision", info.photoStorageSettings().get(1));
        assertEquals(1, info.wideExposureMode());
        assertEquals(8, info.wideIso());
        assertEquals(45, info.wideShutterSpeed());
        assertEquals(16, info.wideExposureValue());
        assertEquals(2, info.zoomExposureMode());
        assertEquals(1, info.zoomFocusMode());
        assertEquals(2, info.zoomFocusState());
        assertEquals(2, info.irMeteringMode());
        assertEquals(36.5, info.irMeteringPoint().temperature(), 0.001);
        assertEquals(30.0, info.irMeteringArea().averTemperature(), 0.001);
        assertEquals(28.0, info.irMeteringArea().minTemperaturePoint().temperature(), 0.001);
        assertEquals(40.0, info.irMeteringArea().maxTemperaturePoint().temperature(), 0.001);
    }

    @Test
    @DisplayName("CameraInfo：扩增字段缺席时为 null（旧固件兼容）")
    void testCameraInfoExpandedFieldsAbsent() {
        CameraInfo info = MessageCodec.fromJson("{\"payload_index\":\"99-0-0\"}", CameraInfo.class);

        assertNull(info.wideExposureMode());
        assertNull(info.zoomFocusState());
        assertNull(info.photoStorageSettings());
        assertNull(info.irMeteringMode());
        assertNull(info.irMeteringArea());
    }
}
