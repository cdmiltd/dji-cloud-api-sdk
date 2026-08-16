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

package ltd.cdmi.dji.cloudapi.sdk.command.service;

import ltd.cdmi.dji.cloudapi.sdk.command.service.esdk.CustomDataTransmissionToEsdkRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.firmware.OtaCreateRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.log.FileUploadListReply;
import ltd.cdmi.dji.cloudapi.sdk.command.service.log.FileUploadStartRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.log.FileUploadUpdateRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.PsdkInputBoxTextSetRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.PsdkWidgetValueSetRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerAudioPlayStartRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerPlayModeSetRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerPlayStopRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerPlayVolumeSetRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerReplayRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.SpeakerTtsPlayStartRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.CustomDataTransmissionToPsdkRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.InFlightWaylineDeliverRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.InFlightWaylineRecoverRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.InFlightWaylineStopRequest;
import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;

import ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证 {@link DjiMessage#parse(String, Class)} 对补全的 17 个 services 指令 POJO 的类型安全解析。
 *
 * <p><b>核心证明</b>：services 通道消息信封为 {@code {method, data, tid, bid, timestamp}},
 * {@link DjiMessage#parse} 解析出 {@link DjiMessage}<T>, {@code data()} 是调用方指定的 POJO 类型。
 *
 * <p>注：{@code in_flight_wayline_cancel} 复用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest},
 * 不在本测试覆盖；fileupload_list 的回复结构用 {@link FileUploadListReply} 单独测试。
 */
class ServiceSupplementParseTest {

    // ==================== firmware + log ====================

    @Test
    @DisplayName("ota_create — List<OtaDevice> 嵌套结构")
    void testParseOtaCreateRequest() {
        String payload = "{\"method\":\"ota_create\","
                + "\"data\":{\"devices\":[{\"sn\":\"SN001\",\"product_version\":\"1.2.3\","
                + "\"firmware_upgrade_type\":2,\"file_url\":\"https://...\",\"md5\":\"abc\","
                + "\"file_size\":123456789,\"file_name\":\"fw.bin\"}]},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";

        DjiMessage<OtaCreateRequest> msg = DjiMessage.parse(payload, OtaCreateRequest.class);

        assertEquals("ota_create", msg.method());
        assertEquals(1, msg.data().devices().size());
        OtaCreateRequest.OtaDevice d = msg.data().devices().get(0);
        assertEquals("SN001", d.sn());
        assertEquals("1.2.3", d.productVersion());
        assertEquals(2, d.firmwareUpgradeType());
        assertEquals("https://...", d.fileUrl());
        assertEquals("abc", d.md5());
        assertEquals(123456789L, d.fileSize());
        assertEquals("fw.bin", d.fileName());
    }

    @Test
    @DisplayName("fileupload_start — params.files[].list[].boot_index 三层嵌套（@Inferred 包装层）")
    void testParseFileUploadStartRequest() {
        String payload = "{\"method\":\"fileupload_start\","
                + "\"data\":{\"params\":{\"files\":[{\"module\":\"0\","
                + "\"object_key\":\"logs/drone\","
                + "\"list\":[{\"boot_index\":1},{\"boot_index\":2}]}]}},"
                + "\"tid\":\"t2\",\"bid\":\"b2\"}";

        DjiMessage<FileUploadStartRequest> msg = DjiMessage.parse(payload, FileUploadStartRequest.class);

        FileUploadStartRequest data = msg.data();
        assertEquals(1, data.params().files().size());
        FileUploadStartRequest.FileUploadFile f = data.params().files().get(0);
        assertEquals("0", f.module());
        assertEquals("logs/drone", f.objectKey());
        assertEquals(2, f.list().size());
        assertEquals(1, f.list().get(0).bootIndex());
        assertEquals(2, f.list().get(1).bootIndex());
    }

    @Test
    @DisplayName("fileupload_update — 单字段 status")
    void testParseFileUploadUpdateRequest() {
        String payload = "{\"method\":\"fileupload_update\","
                + "\"data\":{\"status\":\"cancel\"},"
                + "\"tid\":\"t3\",\"bid\":\"b3\"}";

        DjiMessage<FileUploadUpdateRequest> msg = DjiMessage.parse(payload, FileUploadUpdateRequest.class);

        assertEquals("cancel", msg.data().status());
    }

    @Test
    @DisplayName("fileupload_list Reply — files[].list[] 嵌套（@Inferred 时间单位）")
    void testParseFileUploadListReply() {
        String replyPayload = "{\"method\":\"fileupload_list\","
                + "\"data\":{\"result\":0,\"files\":[{\"device_sn\":\"SN001\","
                + "\"result\":0,\"module\":\"0\","
                + "\"list\":[{\"boot_index\":1,\"start_time\":1700000000000,"
                + "\"end_time\":1700000036000,\"size\":155232}]}]},"
                + "\"tid\":\"t4\",\"bid\":\"b4\"}";

        DjiMessage<FileUploadListReply> msg = DjiMessage.parse(replyPayload, FileUploadListReply.class);

        FileUploadListReply data = msg.data();
        assertEquals(0, data.result());
        assertEquals(1, data.files().size());
        FileUploadListReply.FileGroup g = data.files().get(0);
        assertEquals("SN001", g.deviceSn());
        assertEquals("0", g.module());
        assertEquals(1, g.list().size());
        FileUploadListReply.LogFile lf = g.list().get(0);
        assertEquals(1, lf.bootIndex());
        assertEquals(1700000000000L, lf.startTime());
        assertEquals(1700000036000L, lf.endTime());
        assertEquals(155232L, lf.size());
    }

    // ==================== wayline ====================

    @Test
    @DisplayName("in_flight_wayline_deliver — file 子结构 + 5 个可选 Integer 字段")
    void testParseInFlightWaylineDeliverRequest() {
        String payload = "{\"method\":\"in_flight_wayline_deliver\","
                + "\"data\":{\"in_flight_wayline_id\":\"WL001\","
                + "\"file\":{\"url\":\"https://...\",\"fingerprint\":\"md5hash\"},"
                + "\"out_of_control_action\":1,\"exit_wayline_when_rc_lost\":0,"
                + "\"rth_altitude\":50,\"rth_mode\":1,\"wayline_precision_type\":2},"
                + "\"tid\":\"t5\",\"bid\":\"b5\"}";

        DjiMessage<InFlightWaylineDeliverRequest> msg = DjiMessage.parse(payload, InFlightWaylineDeliverRequest.class);

        InFlightWaylineDeliverRequest data = msg.data();
        assertEquals("WL001", data.inFlightWaylineId());
        assertEquals("https://...", data.file().url());
        assertEquals("md5hash", data.file().fingerprint());
        assertEquals(1, data.outOfControlAction());
        assertEquals(0, data.exitWaylineWhenRcLost());
        assertEquals(50, data.rthAltitude());
        assertEquals(1, data.rthMode());
        assertEquals(2, data.waylinePrecisionType());
    }

    @Test
    @DisplayName("in_flight_wayline_deliver — 可选字段未提供时为 null")
    void testParseInFlightWaylineDeliverRequestOptionalNull() {
        String payload = "{\"method\":\"in_flight_wayline_deliver\","
                + "\"data\":{\"in_flight_wayline_id\":\"WL002\","
                + "\"file\":{\"url\":\"u\",\"fingerprint\":\"f\"}},"
                + "\"tid\":\"t6\",\"bid\":\"b6\"}";

        DjiMessage<InFlightWaylineDeliverRequest> msg = DjiMessage.parse(payload, InFlightWaylineDeliverRequest.class);

        InFlightWaylineDeliverRequest data = msg.data();
        assertEquals("WL002", data.inFlightWaylineId());
        assertNull(data.outOfControlAction());
        assertNull(data.exitWaylineWhenRcLost());
        assertNull(data.rthAltitude());
        assertNull(data.rthMode());
        assertNull(data.waylinePrecisionType());
    }

    @Test
    @DisplayName("in_flight_wayline_stop — 单字段 in_flight_wayline_id")
    void testParseInFlightWaylineStopRequest() {
        String payload = "{\"method\":\"in_flight_wayline_stop\","
                + "\"data\":{\"in_flight_wayline_id\":\"WL001\"},"
                + "\"tid\":\"t7\",\"bid\":\"b7\"}";

        DjiMessage<InFlightWaylineStopRequest> msg = DjiMessage.parse(payload, InFlightWaylineStopRequest.class);

        assertEquals("WL001", msg.data().inFlightWaylineId());
    }

    @Test
    @DisplayName("in_flight_wayline_recover — 单字段 in_flight_wayline_id")
    void testParseInFlightWaylineRecoverRequest() {
        String payload = "{\"method\":\"in_flight_wayline_recover\","
                + "\"data\":{\"in_flight_wayline_id\":\"WL001\"},"
                + "\"tid\":\"t8\",\"bid\":\"b8\"}";

        DjiMessage<InFlightWaylineRecoverRequest> msg = DjiMessage.parse(payload, InFlightWaylineRecoverRequest.class);

        assertEquals("WL001", msg.data().inFlightWaylineId());
    }

    // ==================== PSDK ====================

    @Test
    @DisplayName("speaker_play_volume_set — psdk_index + play_volume")
    void testParseSpeakerPlayVolumeSetRequest() {
        String payload = "{\"method\":\"speaker_play_volume_set\","
                + "\"data\":{\"psdk_index\":2,\"play_volume\":80},"
                + "\"tid\":\"t9\",\"bid\":\"b9\"}";

        DjiMessage<SpeakerPlayVolumeSetRequest> msg = DjiMessage.parse(payload, SpeakerPlayVolumeSetRequest.class);

        assertEquals(2, msg.data().psdkIndex());
        assertEquals(80, msg.data().playVolume());
    }

    @Test
    @DisplayName("speaker_play_mode_set — psdk_index + play_mode")
    void testParseSpeakerPlayModeSetRequest() {
        String payload = "{\"method\":\"speaker_play_mode_set\","
                + "\"data\":{\"psdk_index\":2,\"play_mode\":1},"
                + "\"tid\":\"t10\",\"bid\":\"b10\"}";

        DjiMessage<SpeakerPlayModeSetRequest> msg = DjiMessage.parse(payload, SpeakerPlayModeSetRequest.class);

        assertEquals(2, msg.data().psdkIndex());
        assertEquals(1, msg.data().playMode());
    }

    @Test
    @DisplayName("speaker_play_stop — 单字段 psdk_index")
    void testParseSpeakerPlayStopRequest() {
        String payload = "{\"method\":\"speaker_play_stop\","
                + "\"data\":{\"psdk_index\":2},"
                + "\"tid\":\"t11\",\"bid\":\"b11\"}";

        DjiMessage<SpeakerPlayStopRequest> msg = DjiMessage.parse(payload, SpeakerPlayStopRequest.class);

        assertEquals(2, msg.data().psdkIndex());
    }

    @Test
    @DisplayName("speaker_replay — 单字段 psdk_index")
    void testParseSpeakerReplayRequest() {
        String payload = "{\"method\":\"speaker_replay\","
                + "\"data\":{\"psdk_index\":2},"
                + "\"tid\":\"t12\",\"bid\":\"b12\"}";

        DjiMessage<SpeakerReplayRequest> msg = DjiMessage.parse(payload, SpeakerReplayRequest.class);

        assertEquals(2, msg.data().psdkIndex());
    }

    @Test
    @DisplayName("speaker_tts_play_start — tts 子结构 {name, text, md5}")
    void testParseSpeakerTtsPlayStartRequest() {
        String payload = "{\"method\":\"speaker_tts_play_start\","
                + "\"data\":{\"psdk_index\":2,"
                + "\"tts\":{\"name\":\"notice\",\"text\":\"Hello world\",\"md5\":\"md5hash\"}},"
                + "\"tid\":\"t13\",\"bid\":\"b13\"}";

        DjiMessage<SpeakerTtsPlayStartRequest> msg = DjiMessage.parse(payload, SpeakerTtsPlayStartRequest.class);

        SpeakerTtsPlayStartRequest.Tts tts = msg.data().tts();
        assertEquals(2, msg.data().psdkIndex());
        assertEquals("notice", tts.name());
        assertEquals("Hello world", tts.text());
        assertEquals("md5hash", tts.md5());
    }

    @Test
    @DisplayName("speaker_audio_play_start — file 子结构 {name, url, md5, format}")
    void testParseSpeakerAudioPlayStartRequest() {
        String payload = "{\"method\":\"speaker_audio_play_start\","
                + "\"data\":{\"psdk_index\":2,"
                + "\"file\":{\"name\":\"audio.mp3\",\"url\":\"https://...\","
                + "\"md5\":\"md5hash\",\"format\":\"mp3\"}},"
                + "\"tid\":\"t14\",\"bid\":\"b14\"}";

        DjiMessage<SpeakerAudioPlayStartRequest> msg = DjiMessage.parse(payload, SpeakerAudioPlayStartRequest.class);

        SpeakerAudioPlayStartRequest.AudioFile f = msg.data().file();
        assertEquals(2, msg.data().psdkIndex());
        assertEquals("audio.mp3", f.name());
        assertEquals("https://...", f.url());
        assertEquals("md5hash", f.md5());
        assertEquals("mp3", f.format());
    }

    @Test
    @DisplayName("psdk_input_box_text_set — psdk_index + value")
    void testParsePsdkInputBoxTextSetRequest() {
        String payload = "{\"method\":\"psdk_input_box_text_set\","
                + "\"data\":{\"psdk_index\":0,\"value\":\"Hello\"},"
                + "\"tid\":\"t15\",\"bid\":\"b15\"}";

        DjiMessage<PsdkInputBoxTextSetRequest> msg = DjiMessage.parse(payload, PsdkInputBoxTextSetRequest.class);

        assertEquals(0, msg.data().psdkIndex());
        assertEquals("Hello", msg.data().value());
    }

    @Test
    @DisplayName("psdk_widget_value_set — psdk_index + index + value")
    void testParsePsdkWidgetValueSetRequest() {
        String payload = "{\"method\":\"psdk_widget_value_set\","
                + "\"data\":{\"psdk_index\":0,\"index\":1,\"value\":42},"
                + "\"tid\":\"t16\",\"bid\":\"b16\"}";

        DjiMessage<PsdkWidgetValueSetRequest> msg = DjiMessage.parse(payload, PsdkWidgetValueSetRequest.class);

        assertEquals(0, msg.data().psdkIndex());
        assertEquals(1, msg.data().index());
        assertEquals(42, msg.data().value());
    }

    @Test
    @DisplayName("custom_data_transmission_to_psdk — 单字段 value")
    void testParseCustomDataTransmissionToPsdkRequest() {
        String payload = "{\"method\":\"custom_data_transmission_to_psdk\","
                + "\"data\":{\"value\":\"payload123\"},"
                + "\"tid\":\"t17\",\"bid\":\"b17\"}";

        DjiMessage<CustomDataTransmissionToPsdkRequest> msg = DjiMessage.parse(payload, CustomDataTransmissionToPsdkRequest.class);

        assertEquals("payload123", msg.data().value());
    }

    // ==================== ESDK ====================

    @Test
    @DisplayName("custom_data_transmission_to_esdk — 单字段 value")
    void testParseCustomDataTransmissionToEsdkRequest() {
        String payload = "{\"method\":\"custom_data_transmission_to_esdk\","
                + "\"data\":{\"value\":\"esdk-payload\"},"
                + "\"tid\":\"t18\",\"bid\":\"b18\"}";

        DjiMessage<CustomDataTransmissionToEsdkRequest> msg = DjiMessage.parse(payload, CustomDataTransmissionToEsdkRequest.class);

        assertEquals("esdk-payload", msg.data().value());
    }

    @Test
    @DisplayName("ServiceMethod.fromMethodName 反查 18 个补全 method")
    void testFromMethodNameSupplementMethods() {
        // 验证 18 个补全 method 都能通过反查找到枚举
        assertEquals(ServiceMethod.OTA_CREATE, ServiceMethod.fromMethodName("ota_create").orElseThrow());
        assertEquals(ServiceMethod.FILEUPLOAD_START, ServiceMethod.fromMethodName("fileupload_start").orElseThrow());
        assertEquals(ServiceMethod.FILEUPLOAD_LIST, ServiceMethod.fromMethodName("fileupload_list").orElseThrow());
        assertEquals(ServiceMethod.FILEUPLOAD_UPDATE, ServiceMethod.fromMethodName("fileupload_update").orElseThrow());
        assertEquals(ServiceMethod.IN_FLIGHT_WAYLINE_DELIVER, ServiceMethod.fromMethodName("in_flight_wayline_deliver").orElseThrow());
        assertEquals(ServiceMethod.IN_FLIGHT_WAYLINE_STOP, ServiceMethod.fromMethodName("in_flight_wayline_stop").orElseThrow());
        assertEquals(ServiceMethod.IN_FLIGHT_WAYLINE_RECOVER, ServiceMethod.fromMethodName("in_flight_wayline_recover").orElseThrow());
        assertEquals(ServiceMethod.IN_FLIGHT_WAYLINE_CANCEL, ServiceMethod.fromMethodName("in_flight_wayline_cancel").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_PLAY_VOLUME_SET, ServiceMethod.fromMethodName("speaker_play_volume_set").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_PLAY_MODE_SET, ServiceMethod.fromMethodName("speaker_play_mode_set").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_PLAY_STOP, ServiceMethod.fromMethodName("speaker_play_stop").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_REPLAY, ServiceMethod.fromMethodName("speaker_replay").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_TTS_PLAY_START, ServiceMethod.fromMethodName("speaker_tts_play_start").orElseThrow());
        assertEquals(ServiceMethod.SPEAKER_AUDIO_PLAY_START, ServiceMethod.fromMethodName("speaker_audio_play_start").orElseThrow());
        assertEquals(ServiceMethod.PSDK_INPUT_BOX_TEXT_SET, ServiceMethod.fromMethodName("psdk_input_box_text_set").orElseThrow());
        assertEquals(ServiceMethod.PSDK_WIDGET_VALUE_SET, ServiceMethod.fromMethodName("psdk_widget_value_set").orElseThrow());
        assertEquals(ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_PSDK, ServiceMethod.fromMethodName("custom_data_transmission_to_psdk").orElseThrow());
        assertEquals(ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_ESDK, ServiceMethod.fromMethodName("custom_data_transmission_to_esdk").orElseThrow());
    }

    @Test
    @DisplayName("ServiceMethod 枚举总数应为 97（71 原有 + 18 补全 + 8 回归补全）")
    void testTotalCount() {
        assertEquals(97, ServiceMethod.values().length);
    }
}
