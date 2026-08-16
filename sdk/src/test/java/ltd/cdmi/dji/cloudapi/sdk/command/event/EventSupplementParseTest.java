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

package ltd.cdmi.dji.cloudapi.sdk.command.event;

import ltd.cdmi.dji.cloudapi.sdk.command.event.esdk.CustomDataFromEsdkData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.flight.JoystickInvalidNotifyData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.flight.ObstacleAvoidanceNotifyData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.CustomDataFromPsdkData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.PsdkFloatingWindowTextData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.PsdkUiResourceUploadResultData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.DeviceExitHomingNotifyData;
import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;
import ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DjiMessage#parse(String, Class)} 对补全的 7 个 events 通道 POJO 的类型安全解析。
 *
 * <p><b>核心证明</b>：events 通道消息信封为 {@code {method, data, need_reply, tid, bid, timestamp}},
 * {@link DjiMessage#parse} 解析出 {@link DjiMessage}<T>, {@code data()} 是调用方指定的 POJO 类型。
 *
 * <p>注：第 8 个缺失 event {@code drc_status_notify} 已废弃（DJI 文档保留但 simulator 无实现），
 * 仅在 {@link EventMethod} 枚举中标注 @Inferred，无 POJO，不在本测试覆盖。
 */
class EventSupplementParseTest {

    // ==================== wayline ====================

    @Test
    @DisplayName("device_exit_homing_notify — sn/action/reason 字段")
    void testParseDeviceExitHomingNotify() {
        String payload = "{\"method\":\"device_exit_homing_notify\","
                + "\"data\":{\"sn\":\"DOCK3-001\",\"action\":1,\"reason\":0},"
                + "\"need_reply\":1,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<DeviceExitHomingNotifyData> msg =
                DjiMessage.parse(payload, DeviceExitHomingNotifyData.class);
        assertEquals("DOCK3-001", msg.data().sn());
        assertEquals(1, msg.data().action());
        assertEquals(0, msg.data().reason());
    }

    // ==================== flight ====================

    @Test
    @DisplayName("obstacle_avoidance_notify — 嵌套 List<ObstacleInfo>")
    void testParseObstacleAvoidanceNotify() {
        String payload = "{\"method\":\"obstacle_avoidance_notify\","
                + "\"data\":{\"wayline_uuid\":\"wu-1\",\"flight_id\":\"fl-1\","
                + "\"obstacles\":[{\"id\":\"o1\",\"type\":0,\"timestamp\":1700000000000,"
                + "\"latitude\":30.67,\"longitude\":104.07,\"height\":100,"
                + "\"wayline_id\":\"w1\",\"waypoint_index\":0}],"
                + "\"is_final_report\":true},"
                + "\"need_reply\":1,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<ObstacleAvoidanceNotifyData> msg =
                DjiMessage.parse(payload, ObstacleAvoidanceNotifyData.class);
        assertEquals("fl-1", msg.data().flightId());
        assertEquals(1, msg.data().obstacles().size());
        ObstacleAvoidanceNotifyData.ObstacleInfo obs = msg.data().obstacles().get(0);
        assertEquals("o1", obs.id());
        assertEquals(0, obs.type());
        assertEquals(1700000000000L, obs.timestamp());
        assertEquals(30.67, obs.latitude(), 0.001);
        assertEquals(104.07, obs.longitude(), 0.001);
        assertEquals(100, obs.height(), 0.001);
        assertEquals("w1", obs.waylineId());
        assertEquals(0, obs.waypointIndex());
        assertTrue(msg.data().isFinalReport());
    }

    @Test
    @DisplayName("joystick_invalid_notify — reason 枚举")
    void testParseJoystickInvalidNotify() {
        String payload = "{\"method\":\"joystick_invalid_notify\","
                + "\"data\":{\"reason\":0},"
                + "\"need_reply\":1,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<JoystickInvalidNotifyData> msg =
                DjiMessage.parse(payload, JoystickInvalidNotifyData.class);
        assertEquals(0, msg.data().reason());
    }

    // ==================== psdk ====================

    @Test
    @DisplayName("psdk_floating_window_text — psdk_index + value 平铺")
    void testParsePsdkFloatingWindowText() {
        String payload = "{\"method\":\"psdk_floating_window_text\","
                + "\"data\":{\"psdk_index\":1,\"value\":\"hello world\"},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<PsdkFloatingWindowTextData> msg =
                DjiMessage.parse(payload, PsdkFloatingWindowTextData.class);
        assertEquals(1, msg.data().psdkIndex());
        assertEquals("hello world", msg.data().value());
    }

    @Test
    @DisplayName("psdk_ui_resource_upload_result — 平铺 psdk_index/object_key/size/result")
    void testParsePsdkUiResourceUploadResult() {
        String payload = "{\"method\":\"psdk_ui_resource_upload_result\","
                + "\"data\":{\"psdk_index\":1,\"object_key\":\"res/ui.zip\","
                + "\"size\":1024,\"result\":0},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<PsdkUiResourceUploadResultData> msg =
                DjiMessage.parse(payload, PsdkUiResourceUploadResultData.class);
        assertEquals(1, msg.data().psdkIndex());
        assertEquals("res/ui.zip", msg.data().objectKey());
        assertEquals(1024, msg.data().size());
        assertEquals(0, msg.data().result());
    }

    @Test
    @DisplayName("custom_data_transmission_from_psdk — value 字段")
    void testParseCustomDataFromPsdk() {
        String payload = "{\"method\":\"custom_data_transmission_from_psdk\","
                + "\"data\":{\"value\":\"psdk-msg\"},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<CustomDataFromPsdkData> msg =
                DjiMessage.parse(payload, CustomDataFromPsdkData.class);
        assertEquals("psdk-msg", msg.data().value());
    }

    // ==================== esdk ====================

    @Test
    @DisplayName("custom_data_transmission_from_esdk — value 字段")
    void testParseCustomDataFromEsdk() {
        String payload = "{\"method\":\"custom_data_transmission_from_esdk\","
                + "\"data\":{\"value\":\"esdk-msg\"},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<CustomDataFromEsdkData> msg =
                DjiMessage.parse(payload, CustomDataFromEsdkData.class);
        assertEquals("esdk-msg", msg.data().value());
    }

    // ==================== 枚举反查 ====================

    @Test
    @DisplayName("EventMethod.fromMethodName — 7 个新常量可反查")
    void testEventMethodReverseLookup() {
        assertEquals(EventMethod.DEVICE_EXIT_HOMING_NOTIFY,
                EventMethod.fromMethodName("device_exit_homing_notify").orElseThrow());
        assertEquals(EventMethod.OBSTACLE_AVOIDANCE_NOTIFY,
                EventMethod.fromMethodName("obstacle_avoidance_notify").orElseThrow());
        assertEquals(EventMethod.JOYSTICK_INVALID_NOTIFY,
                EventMethod.fromMethodName("joystick_invalid_notify").orElseThrow());
        assertEquals(EventMethod.PSDK_FLOATING_WINDOW_TEXT,
                EventMethod.fromMethodName("psdk_floating_window_text").orElseThrow());
        assertEquals(EventMethod.PSDK_UI_RESOURCE_UPLOAD_RESULT,
                EventMethod.fromMethodName("psdk_ui_resource_upload_result").orElseThrow());
        assertEquals(EventMethod.CUSTOM_DATA_TRANSMISSION_FROM_PSDK,
                EventMethod.fromMethodName("custom_data_transmission_from_psdk").orElseThrow());
        assertEquals(EventMethod.CUSTOM_DATA_TRANSMISSION_FROM_ESDK,
                EventMethod.fromMethodName("custom_data_transmission_from_esdk").orElseThrow());
        // drc_status_notify（已废弃）
        assertEquals(EventMethod.DRC_STATUS_NOTIFY,
                EventMethod.fromMethodName("drc_status_notify").orElseThrow());
    }
}
