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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 DRC 相机/云台/PSDK 请求 POJO 的 JSON 序列化 wire key 与往返闭环。
 *
 * <p>覆盖 A16/A17/A18 各组共 31 个 drc_ 相机相关请求 data。
 */
class DrcCameraRequestTest {

    private static <T> void roundTrip(T value, Class<T> type, String... wireKeys) {
        String json = MessageCodec.toJson(value);
        for (String key : wireKeys) {
            assertTrue(json.contains("\"" + key + "\""),
                    type.getSimpleName() + " JSON 应含 wire key " + key + "，实际: " + json);
        }
        assertEquals(value, MessageCodec.fromJson(json, type),
                type.getSimpleName() + " 往返应保持相等");
    }

    @Test
    @DisplayName("A16 组：photo_size/video_resolution/video_storage/aim/aperture/dewarping")
    void testGroupA16() {
        roundTrip(new DrcCameraPhotoSizeSetRequest("1-0-0", 4), DrcCameraPhotoSizeSetRequest.class,
                "payload_index", "photo_size");
        roundTrip(new DrcVideoResolutionSetRequest("1-0-0", 1), DrcVideoResolutionSetRequest.class,
                "payload_index", "video_resolution");
        roundTrip(new DrcVideoStorageSetRequest("1-0-0", List.of("current")), DrcVideoStorageSetRequest.class,
                "payload_index", "video_storage_settings");
        roundTrip(new DrcCameraAimRequest("1-0-0", "wide", true, 0.5, 0.5), DrcCameraAimRequest.class,
                "payload_index", "camera_type", "locked", "x", "y");
        roundTrip(new DrcCameraApertureValueSetRequest("1-0-0", "wide", 280), DrcCameraApertureValueSetRequest.class,
                "payload_index", "camera_type", "aperture_value");
        roundTrip(new DrcCameraDewarpingSetRequest("1-0-0", "wide", 1), DrcCameraDewarpingSetRequest.class,
                "payload_index", "camera_type", "dewarping_state");
    }

    @Test
    @DisplayName("A17 组：exposure_mode/exposure/focal_length/focus_mode/focus_value/frame_zoom/iso/look_at")
    void testGroupA17() {
        roundTrip(new DrcCameraExposureModeSetRequest("1-0-0", "wide", 1), DrcCameraExposureModeSetRequest.class,
                "payload_index", "camera_type", "exposure_mode");
        roundTrip(new DrcCameraExposureSetRequest("1-0-0", "wide", "1"), DrcCameraExposureSetRequest.class,
                "payload_index", "camera_type", "exposure_value");
        roundTrip(new DrcCameraFocalLengthSetRequest("1-0-0", "zoom", 5.0), DrcCameraFocalLengthSetRequest.class,
                "payload_index", "camera_type", "zoom_factor");
        roundTrip(new DrcCameraFocusModeSetRequest("1-0-0", "wide", 2), DrcCameraFocusModeSetRequest.class,
                "payload_index", "camera_type", "focus_mode");
        roundTrip(new DrcCameraFocusValueSetRequest("1-0-0", "wide", 100), DrcCameraFocusValueSetRequest.class,
                "payload_index", "camera_type", "focus_value");
        roundTrip(new DrcCameraFrameZoomRequest("1-0-0", "wide", true, 0.1, 0.2, 0.3, 0.4), DrcCameraFrameZoomRequest.class,
                "payload_index", "camera_type", "locked", "x", "y", "width", "height");
        roundTrip(new DrcCameraIsoSetRequest("1-0-0", "wide", 5), DrcCameraIsoSetRequest.class,
                "payload_index", "camera_type", "iso_value");
        roundTrip(new DrcCameraLookAtRequest("1-0-0", true, 30.0, 120.0, 50.0), DrcCameraLookAtRequest.class,
                "payload_index", "locked", "latitude", "longitude", "height");
    }

    @Test
    @DisplayName("A18 组：shutter/mode/photo/recording/screen/gimbal/storage/ir metering")
    void testGroupA18() {
        roundTrip(new DrcCameraMechanicalShutterSetRequest("1-0-0", "wide", 1), DrcCameraMechanicalShutterSetRequest.class,
                "payload_index", "camera_type", "mechanical_shutter_state");
        roundTrip(new DrcCameraShutterSetRequest("1-0-0", "wide", 60), DrcCameraShutterSetRequest.class,
                "payload_index", "camera_type", "shutter_value");
        roundTrip(new DrcIntervalPhotoSetRequest("1-0-0", 5.0), DrcIntervalPhotoSetRequest.class,
                "payload_index", "interval");
        roundTrip(new DrcCameraModeSwitchRequest("1-0-0", CameraMode.VIDEO), DrcCameraModeSwitchRequest.class,
                "payload_index", "camera_mode");
        roundTrip(new DrcCameraPhotoStopRequest("1-0-0"), DrcCameraPhotoStopRequest.class, "payload_index");
        roundTrip(new DrcCameraPhotoTakeRequest("1-0-0"), DrcCameraPhotoTakeRequest.class, "payload_index");
        roundTrip(new DrcCameraPhotoFormatSetRequest("1-0-0", 7), DrcCameraPhotoFormatSetRequest.class,
                "payload_index", "photo_format");
        roundTrip(new DrcCameraPointFocusActionRequest("1-0-0", "wide", 0.5, 0.5), DrcCameraPointFocusActionRequest.class,
                "payload_index", "camera_type", "x", "y");
        roundTrip(new DrcCameraRecordingStartRequest("1-0-0"), DrcCameraRecordingStartRequest.class, "payload_index");
        roundTrip(new DrcCameraRecordingStopRequest("1-0-0"), DrcCameraRecordingStopRequest.class, "payload_index");
        roundTrip(new DrcCameraScreenDragRequest("1-0-0", true, 0.5, -0.5), DrcCameraScreenDragRequest.class,
                "payload_index", "locked", "pitch_speed", "yaw_speed");
        roundTrip(new DrcCameraScreenSplitRequest("1-0-0", true), DrcCameraScreenSplitRequest.class,
                "payload_index", "enable");
        roundTrip(new DrcGimbalResetRequest("1-0-0", 0), DrcGimbalResetRequest.class,
                "payload_index", "reset_mode");
        roundTrip(new DrcPhotoStorageSetRequest("1-0-0", List.of("current")), DrcPhotoStorageSetRequest.class,
                "payload_index", "photo_storage_settings");
        roundTrip(new DrcIrMeteringAreaSetRequest("1-0-0", 0.1, 0.2, 0.3, 0.4), DrcIrMeteringAreaSetRequest.class,
                "payload_index", "x", "y", "width", "height");
        roundTrip(new DrcIrMeteringModeSetRequest("1-0-0", 2), DrcIrMeteringModeSetRequest.class,
                "payload_index", "mode");
        roundTrip(new DrcIrMeteringPointSetRequest("1-0-0", 0.5, 0.5), DrcIrMeteringPointSetRequest.class,
                "payload_index", "x", "y");
    }
}
