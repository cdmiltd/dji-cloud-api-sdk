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

import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.AirConditionerModeSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.AlarmStateSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryStoreModeSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.RtkCalibrationRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.SdrWorkmodeSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.esim.EsimActivateRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.esim.EsimOperatorSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.esim.SimSlotSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.BatteryStoreMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 {@link MessageCodec#parse(String, Class)} 对补全的 8 个 services 带参 POJO 的类型安全解析。
 *
 * <p><b>核心证明</b>：services 请求消息信封为 {@code {method, data, tid, bid, timestamp}},
 * {@link MessageCodec#parse} 解析出 {@link DjiMessage}<T>, {@code data()} 是调用方指定的 POJO 类型。
 *
 * <p>其中 {@code EsimOperatorSwitchRequest} 字段依据 DJI Dock3 cmd 文档 services 请求确认（@Verified），
 * 其余 7 个因 DJI 文档 services 请求部分被截断，字段名来自 coverage-review.html §1.3 描述（@Inferred）。
 */
class ServiceParamParseTest {

    // ==================== esim 包（3 个）====================

    @Test
    @DisplayName("esim_activate — eid + List<EsimInfo>（@Inferred）")
    void testParseEsimActivate() {
        String payload = "{\"method\":\"esim_activate\","
                + "\"data\":{\"eid\":\"eid-001\",\"esim_infos\":["
                + "{\"operator\":\"移动\",\"iccid\":\"iccid1\",\"imsi\":\"imsi1\"}]},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<EsimActivateRequest> msg =
                MessageCodec.parse(payload, EsimActivateRequest.class);
        assertEquals("eid-001", msg.data().eid());
        assertEquals(1, msg.data().esimInfos().size());
        assertEquals("移动", msg.data().esimInfos().get(0).operator());
        assertEquals("iccid1", msg.data().esimInfos().get(0).iccid());
        assertEquals("imsi1", msg.data().esimInfos().get(0).imsi());
    }

    @Test
    @DisplayName("esim_operator_switch — imei/device_type/esim_operator（@Verified DJI 文档确认）")
    void testParseEsimOperatorSwitch() {
        String payload = "{\"method\":\"esim_operator_switch\","
                + "\"data\":{\"imei\":\"123456789012345\",\"device_type\":\"dock\",\"esim_operator\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<EsimOperatorSwitchRequest> msg =
                MessageCodec.parse(payload, EsimOperatorSwitchRequest.class);
        assertEquals("123456789012345", msg.data().imei());
        assertEquals("dock", msg.data().deviceType());
        assertEquals(1, msg.data().esimOperator());
    }

    @Test
    @DisplayName("sim_slot_switch — slot_type（@Inferred）")
    void testParseSimSlotSwitch() {
        String payload = "{\"method\":\"sim_slot_switch\","
                + "\"data\":{\"slot_type\":0},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<SimSlotSwitchRequest> msg =
                MessageCodec.parse(payload, SimSlotSwitchRequest.class);
        assertEquals(0, msg.data().slotType());
    }

    // ==================== debug 包（5 个）====================

    @Test
    @DisplayName("rtk_calibration — cali_type（@Inferred）")
    void testParseRtkCalibration() {
        String payload = "{\"method\":\"rtk_calibration\","
                + "\"data\":{\"cali_type\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<RtkCalibrationRequest> msg =
                MessageCodec.parse(payload, RtkCalibrationRequest.class);
        assertEquals(1, msg.data().caliType());
    }

    @Test
    @DisplayName("battery_store_mode_switch — mode（类型化枚举 BatteryStoreMode，@Inferred 字段名）")
    void testParseBatteryStoreModeSwitch() {
        String payload = "{\"method\":\"battery_store_mode_switch\","
                + "\"data\":{\"mode\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<BatteryStoreModeSwitchRequest> msg =
                MessageCodec.parse(payload, BatteryStoreModeSwitchRequest.class);
        // mode 字段类型化为 BatteryStoreMode 枚举，Jackson 通过 @JsonCreator 将 int 1 绑定为 PLANNING
        assertEquals(BatteryStoreMode.PLANNING, msg.data().mode());
        assertEquals(1, msg.data().mode().code());
    }

    @Test
    @DisplayName("alarm_state_switch — action（@Inferred）")
    void testParseAlarmStateSwitch() {
        String payload = "{\"method\":\"alarm_state_switch\","
                + "\"data\":{\"action\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<AlarmStateSwitchRequest> msg =
                MessageCodec.parse(payload, AlarmStateSwitchRequest.class);
        assertEquals(1, msg.data().action());
    }

    @Test
    @DisplayName("air_conditioner_mode_switch — mode（@Inferred）")
    void testParseAirConditionerModeSwitch() {
        String payload = "{\"method\":\"air_conditioner_mode_switch\","
                + "\"data\":{\"mode\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<AirConditionerModeSwitchRequest> msg =
                MessageCodec.parse(payload, AirConditionerModeSwitchRequest.class);
        assertEquals(1, msg.data().mode());
    }

    @Test
    @DisplayName("sdr_workmode_switch — link_workmode（@Inferred）")
    void testParseSdrWorkmodeSwitch() {
        String payload = "{\"method\":\"sdr_workmode_switch\","
                + "\"data\":{\"link_workmode\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<SdrWorkmodeSwitchRequest> msg =
                MessageCodec.parse(payload, SdrWorkmodeSwitchRequest.class);
        assertEquals(1, msg.data().linkWorkmode());
    }
}
