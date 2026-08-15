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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.up;

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证 {@link MessageCodec#parse(String, Class)} 对 DRC 上行推送消息的类型安全解析。
 *
 * <p><b>核心证明</b>：DRC 上行消息信封为 {@code {method, data, seq}}（无 tid/bid），
 * {@link MessageCodec#parse} 仍能解析出 {@link DjiMessage}<T>，{@code data()} 是调用方指定的 POJO 类型。
 *
 * <p>注：{@link DjiMessage} 信封不含 seq 字段，调用方需自行从 JSON 顶层取 seq。
 */
class DrcUpPushDataParseTest {

    // ==================== 简单结构解析（验证 SNAKE_CASE 映射） ====================

    @Test
    @DisplayName("osd_info_push — 10 字段平铺结构")
    void testParseOsdInfoPushData() {
        // simulator DeviceSimulator.buildOsdInfo 实际推送格式
        String payload = "{\"method\":\"osd_info_push\","
                + "\"data\":{\"attitude_head\":123.4,\"latitude\":22.5,\"longitude\":113.9,"
                + "\"height\":50.0,\"speed_x\":10.0,\"speed_y\":0.0,\"speed_z\":0.5,"
                + "\"gimbal_pitch\":-30.0,\"gimbal_roll\":0.0,\"gimbal_yaw\":45.0},"
                + "\"seq\":1001}";

        DjiMessage<OsdInfoPushData> msg = MessageCodec.parse(payload, OsdInfoPushData.class);

        assertEquals("osd_info_push", msg.method());
        OsdInfoPushData data = msg.data();
        assertEquals(123.4, data.attitudeHead(), 0.001);
        assertEquals(22.5, data.latitude(), 0.001);
        assertEquals(113.9, data.longitude(), 0.001);
        assertEquals(50.0, data.height(), 0.001);
        assertEquals(10.0, data.speedX(), 0.001);
        assertEquals(0.0, data.speedY(), 0.001);
        assertEquals(0.5, data.speedZ(), 0.001);
        assertEquals(-30.0, data.gimbalPitch(), 0.001);
        assertEquals(0.0, data.gimbalRoll(), 0.001);
        assertEquals(45.0, data.gimbalYaw(), 0.001);
    }

    @Test
    @DisplayName("hsi_info_push — 含 List<Integer> aroundDistances")
    void testParseHsiInfoPushData() {
        String payload = "{\"method\":\"hsi_info_push\","
                + "\"data\":{\"up_distance\":10000,\"down_distance\":10000,"
                + "\"up_enable\":true,\"up_work\":true,\"down_enable\":true,\"down_work\":true,"
                + "\"left_enable\":true,\"left_work\":true,\"right_enable\":true,\"right_work\":true,"
                + "\"front_enable\":true,\"front_work\":true,\"back_enable\":true,\"back_work\":true,"
                + "\"vertical_enable\":true,\"vertical_work\":true,"
                + "\"horizontal_enable\":true,\"horizontal_work\":true,"
                + "\"around_distances\":[]},"
                + "\"seq\":1002}";

        DjiMessage<HsiInfoPushData> msg = MessageCodec.parse(payload, HsiInfoPushData.class);

        HsiInfoPushData data = msg.data();
        assertEquals(10000, data.upDistance());
        assertEquals(10000, data.downDistance());
        assertEquals(true, data.upEnable());
        assertEquals(true, data.horizontalWork());
        assertNotNull(data.aroundDistances());
        assertEquals(0, data.aroundDistances().size());
    }

    @Test
    @DisplayName("delay_info_push — 含 List<LiveviewDelay> 嵌套结构")
    void testParseDelayInfoPushData() {
        String payload = "{\"method\":\"delay_info_push\","
                + "\"data\":{\"sdr_cmd_delay\":10,"
                + "\"liveview_delay_list\":["
                + "{\"video_id\":\"drone_sn/165-0-7/normal-0\",\"liveview_delay_time\":60},"
                + "{\"video_id\":\"drone_sn/165-0-7/zoom-0\",\"liveview_delay_time\":80}"
                + "]},"
                + "\"seq\":1003}";

        DjiMessage<DelayInfoPushData> msg = MessageCodec.parse(payload, DelayInfoPushData.class);

        DelayInfoPushData data = msg.data();
        assertEquals(10, data.sdrCmdDelay());
        assertEquals(2, data.liveviewDelayList().size());
        assertEquals("drone_sn/165-0-7/normal-0", data.liveviewDelayList().get(0).videoId());
        assertEquals(60, data.liveviewDelayList().get(0).liveviewDelayTime());
        assertEquals(80, data.liveviewDelayList().get(1).liveviewDelayTime());
    }

    @Test
    @DisplayName("drc_drone_state_push — 5 字段平铺")
    void testParseDroneStatePushData() {
        String payload = "{\"method\":\"drc_drone_state_push\","
                + "\"data\":{\"mode_code\":1,\"stealth_state\":0,\"night_lights_state\":1,"
                + "\"landing_type\":0,\"landing_protection_type\":0},"
                + "\"seq\":1004}";

        DjiMessage<DroneStatePushData> msg = MessageCodec.parse(payload, DroneStatePushData.class);

        DroneStatePushData data = msg.data();
        assertEquals(1, data.modeCode());
        assertEquals(0, data.stealthState());
        assertEquals(1, data.nightLightsState());
        assertEquals(0, data.landingType());
        assertEquals(0, data.landingProtectionType());
    }

    @Test
    @DisplayName("drc_psdk_floating_window_text — 2 字段平铺")
    void testParsePsdkFloatingWindowTextData() {
        String payload = "{\"method\":\"drc_psdk_floating_window_text\","
                + "\"data\":{\"psdk_index\":0,\"floating_window_text\":\"\"},"
                + "\"seq\":1005}";

        DjiMessage<PsdkFloatingWindowTextData> msg = MessageCodec.parse(payload, PsdkFloatingWindowTextData.class);

        PsdkFloatingWindowTextData data = msg.data();
        assertEquals(0, data.psdkIndex());
        assertEquals("", data.floatingWindowText());
    }

    @Test
    @DisplayName("drc_psdk_ui_resource — 3 字段平铺")
    void testParsePsdkUiResourceData() {
        String payload = "{\"method\":\"drc_psdk_ui_resource\","
                + "\"data\":{\"psdk_index\":0,\"psdk_ready\":1,"
                + "\"object_key\":\"psdk_config/0/ui_resource.tar.gz\"},"
                + "\"seq\":1006}";

        DjiMessage<PsdkUiResourceData> msg = MessageCodec.parse(payload, PsdkUiResourceData.class);

        PsdkUiResourceData data = msg.data();
        assertEquals(0, data.psdkIndex());
        assertEquals(1, data.psdkReady());
        assertEquals("psdk_config/0/ui_resource.tar.gz", data.objectKey());
    }

    @Test
    @DisplayName("drc_speaker_play_progress — 含嵌套 Progress 子结构")
    void testParseSpeakerPlayProgressData() {
        String payload = "{\"method\":\"drc_speaker_play_progress\","
                + "\"data\":{\"psdk_index\":2,\"result\":0,\"status\":\"success\","
                + "\"progress\":{\"step_key\":\"play\",\"percent\":100},"
                + "\"md5\":\"\"},"
                + "\"seq\":1007}";

        DjiMessage<SpeakerPlayProgressData> msg = MessageCodec.parse(payload, SpeakerPlayProgressData.class);

        SpeakerPlayProgressData data = msg.data();
        assertEquals(2, data.psdkIndex());
        assertEquals(0, data.result());
        assertEquals("success", data.status());
        assertEquals("play", data.progress().stepKey());
        assertEquals(100, data.progress().percent());
        assertEquals("", data.md5());
    }

    // ==================== 复杂嵌套结构解析 ====================

    @Test
    @DisplayName("drc_camera_state_push — payload_index + camera_state + media_storage 三层嵌套")
    void testParseCameraStatePushData() {
        String payload = "{\"method\":\"drc_camera_state_push\","
                + "\"data\":{\"payload_index\":\"165-0-7\","
                + "\"camera_state\":{\"camera_mode\":1,\"interval_photo_interval\":5,"
                + "\"video_resolution\":\"3840x2160\",\"linkage_zoom_state\":0,"
                + "\"photo_size\":\"4:3\",\"record_time\":120,\"recording_state\":1,"
                + "\"photo_state\":0,\"remain_photo_num\":100,\"remain_record_duration\":3600,"
                + "\"night_mode_settings\":{\"night_mode\":1,\"denoise_level\":3,"
                + "\"night_vision_enable\":true,\"infrared_fill_light_enable\":false,"
                + "\"night_scene_mode_suggestion\":1,\"is_working\":1}},"
                + "\"media_storage\":{\"photo_storage_settings\":[\"current\",\"ir\"],"
                + "\"video_storage_settings\":[\"current\",\"ir\"]}},"
                + "\"seq\":1008}";

        DjiMessage<CameraStatePushData> msg = MessageCodec.parse(payload, CameraStatePushData.class);

        CameraStatePushData data = msg.data();
        assertEquals("165-0-7", data.payloadIndex());
        assertEquals(1, data.cameraState().cameraMode());
        assertEquals(5, data.cameraState().intervalPhotoInterval());
        assertEquals("3840x2160", data.cameraState().videoResolution());
        assertEquals(120, data.cameraState().recordTime());
        assertEquals(1, data.cameraState().recordingState());
        assertEquals(100, data.cameraState().remainPhotoNum());
        assertEquals(1, data.cameraState().nightModeSettings().nightMode());
        assertEquals(3, data.cameraState().nightModeSettings().denoiseLevel());
        assertEquals(true, data.cameraState().nightModeSettings().nightVisionEnable());
        assertEquals(2, data.mediaStorage().photoStorageSettings().size());
        assertEquals("current", data.mediaStorage().photoStorageSettings().get(0));
    }

    @Test
    @DisplayName("drc_camera_osd_info_push — 6 子结构嵌套")
    void testParseCameraOsdInfoPushData() {
        String payload = "{\"method\":\"drc_camera_osd_info_push\","
                + "\"data\":{\"payload_index\":\"165-0-7\","
                + "\"wide_lense\":{\"wide_exposure_mode\":1,\"wide_iso\":8,"
                + "\"wide_shutter_speed\":45,\"wide_exposure_value\":16,\"wide_aperture_value\":10},"
                + "\"zoom_lense\":{\"zoom_exposure_mode\":1,\"zoom_iso\":8,\"zoom_shutter_speed\":45,"
                + "\"zoom_exposure_value\":16,\"zoom_focus_mode\":0,\"zoom_focus_value\":100,"
                + "\"zoom_max_focus_value\":200,\"zoom_min_focus_value\":0,"
                + "\"zoom_calibrate_farthest_focus_value\":34,"
                + "\"zoom_calibrate_nearest_focus_value\":64,\"zoom_focus_state\":0,"
                + "\"zoom_factor\":2.5,\"zoom_aperture_value\":10},"
                + "\"measure_target\":{\"measure_target_longitude\":113.9,"
                + "\"measure_target_latitude\":22.5,\"measure_target_altitude\":50.0,"
                + "\"measure_target_distance\":100.0,\"measure_target_error_state\":1},"
                + "\"ir_lense\":{\"screen_split_enable\":false,\"ir_zoom_factor\":2,"
                + "\"thermal_current_palette_style\":11,\"thermal_gain_mode\":2,"
                + "\"thermal_isotherm_state\":0,\"thermal_isotherm_upper_limit\":150,"
                + "\"thermal_isotherm_lower_limit\":-20,"
                + "\"thermal_global_temperature_min\":-10.5,"
                + "\"thermal_global_temperature_max\":80.5},"
                + "\"liveview\":{\"liveview_world_region\":{\"left\":0.4324,\"top\":0.4332,"
                + "\"right\":0.5639,\"bottom\":0.5609}}},"
                + "\"seq\":1009}";

        DjiMessage<CameraOsdInfoPushData> msg = MessageCodec.parse(payload, CameraOsdInfoPushData.class);

        CameraOsdInfoPushData data = msg.data();
        assertEquals("165-0-7", data.payloadIndex());
        assertEquals(1, data.wideLense().wideExposureMode());
        assertEquals(8, data.wideLense().wideIso());
        assertEquals(2.5, data.zoomLense().zoomFactor(), 0.001);
        assertEquals(100, data.zoomLense().zoomFocusValue());
        assertEquals(113.9, data.measureTarget().measureTargetLongitude(), 0.001);
        assertEquals(100.0, data.measureTarget().measureTargetDistance(), 0.001);
        assertEquals(false, data.irLense().screenSplitEnable());
        assertEquals(2, data.irLense().irZoomFactor());
        assertEquals(-10.5, data.irLense().thermalGlobalTemperatureMin(), 0.001);
        assertEquals(0.4324, data.liveview().liveviewWorldRegion().left(), 0.001);
        assertEquals(0.5609, data.liveview().liveviewWorldRegion().bottom(), 0.001);
    }

    @Test
    @DisplayName("drc_psdk_state_info — 探照灯（psdk_index=1，light 非空，speaker 为 null）")
    void testParsePsdkStateInfoDataLight() {
        String payload = "{\"method\":\"drc_psdk_state_info\","
                + "\"data\":{\"psdk_index\":1,\"psdk_type\":5,\"psdk_name\":\"Searchlight\","
                + "\"psdk_sn\":\"psdk_light_sn\",\"psdk_version\":\"1.0.0\",\"psdk_lib_version\":\"1.0.0\","
                + "\"light\":{\"work_mode\":1,\"brightness\":50,\"calibration_status\":0,"
                + "\"calibration_progress\":100,\"left_value\":30,\"right_value\":30,"
                + "\"wide_field_mode\":false,\"light_gimbal_control\":false}},"
                + "\"seq\":1010}";

        DjiMessage<PsdkStateInfoData> msg = MessageCodec.parse(payload, PsdkStateInfoData.class);

        PsdkStateInfoData data = msg.data();
        assertEquals(1, data.psdkIndex());
        assertEquals(5, data.psdkType());
        assertEquals("Searchlight", data.psdkName());
        assertEquals("psdk_light_sn", data.psdkSn());
        assertNotNull(data.light());
        assertEquals(1, data.light().workMode());
        assertEquals(50, data.light().brightness());
        assertEquals(100, data.light().calibrationProgress());
        assertEquals(false, data.light().wideFieldMode());
        assertNull(data.speaker());
    }

    @Test
    @DisplayName("drc_psdk_state_info — 喊话器（psdk_index=2，speaker 非空，light 为 null）")
    void testParsePsdkStateInfoDataSpeaker() {
        String payload = "{\"method\":\"drc_psdk_state_info\","
                + "\"data\":{\"psdk_index\":2,\"psdk_type\":5,\"psdk_name\":\"Speaker\","
                + "\"psdk_sn\":\"psdk_speaker_sn\",\"psdk_version\":\"1.0.0\",\"psdk_lib_version\":\"1.0.0\","
                + "\"speaker\":{\"work_mode\":0,\"play_mode\":0,\"system_state\":2,"
                + "\"play_volume\":80,\"play_file_name\":\"\",\"play_file_md5\":\"\","
                + "\"tts_volume\":80,\"tts_type\":0,\"tts_language\":0,\"tts_speed\":50}},"
                + "\"seq\":1011}";

        DjiMessage<PsdkStateInfoData> msg = MessageCodec.parse(payload, PsdkStateInfoData.class);

        PsdkStateInfoData data = msg.data();
        assertEquals(2, data.psdkIndex());
        assertEquals("Speaker", data.psdkName());
        assertNull(data.light());
        assertNotNull(data.speaker());
        assertEquals(0, data.speaker().workMode());
        assertEquals(2, data.speaker().systemState());
        assertEquals(80, data.speaker().playVolume());
        assertEquals(50, data.speaker().ttsSpeed());
    }

    @Test
    @DisplayName("drc_camera_photo_info_push — DJI 文档示例（countdown_time/result/status/progress/ext）")
    void testParseCameraPhotoInfoPushData() {
        String payload = "{\"method\":\"drc_camera_photo_info_push\","
                + "\"data\":{\"countdown_time\":4,\"result\":0,\"status\":\"in_progress\","
                + "\"progress\":{\"current_step\":0,\"percent\":100},"
                + "\"ext\":{\"camera_mode\":2}},"
                + "\"seq\":1}";

        DjiMessage<CameraPhotoInfoPushData> msg = MessageCodec.parse(payload, CameraPhotoInfoPushData.class);

        CameraPhotoInfoPushData data = msg.data();
        assertEquals(4, data.countdownTime());
        assertEquals(0, data.result());
        assertEquals("in_progress", data.status());
        assertEquals(0, data.progress().currentStep());
        assertEquals(100, data.progress().percent());
        assertEquals(ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode.HYPER_LIGHT, data.ext().cameraMode());
    }
}
