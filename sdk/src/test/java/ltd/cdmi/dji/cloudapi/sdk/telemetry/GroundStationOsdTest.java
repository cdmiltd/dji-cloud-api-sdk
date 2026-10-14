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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link GroundStationOsd} record 解析与 NON_NULL 序列化测试。
 *
 * <p>验证 O4 地面站 OSD 的 snake_case ↔ camelCase 映射，以及可选字段缺席时为 null。
 */
class GroundStationOsdTest {

    private static final String SAMPLE = "{"
            + "\"mode_code\":4,\"low_power_switch_state\":1,\"electric_supply_power\":120,"
            + "\"power_mode\":2,\"battery_voltage\":24000,"
            + "\"tilt_angle\":{\"value\":1.5,\"valid\":1},"
            + "\"basestation_work_mode\":0,\"under_work_mode\":1,"
            + "\"lan_status\":3,\"poe_status\":4,"
            + "\"battery\":{\"capacity_percent\":80,\"voltage\":24000,\"current\":1.2,\"temperature\":30.5},"
            + "\"self_converge_coordinate\":{\"longitude\":113.9,\"latitude\":22.5,\"height\":50.0},"
            + "\"electric_supply_voltage\":220.0,\"acc_time\":3600,"
            + "\"longitude\":113.9,\"latitude\":22.5,\"height\":50.0,"
            + "\"home_position_is_valid\":1}";

    @Test
    @DisplayName("从 JSON 解析 O4 地面站 OSD 顶层字段")
    void testParseTopLevelFields() {
        GroundStationOsd osd = MessageCodec.fromJson(SAMPLE, GroundStationOsd.class);

        assertEquals(4, osd.modeCode());
        assertEquals(1, osd.lowPowerSwitchState());
        assertEquals(120, osd.electricSupplyPower());
        assertEquals(2, osd.powerMode());
        assertEquals(24000, osd.batteryVoltage());
        assertEquals(0, osd.basestationWorkMode());
        assertEquals(1, osd.underWorkMode());
        assertEquals(3, osd.lanStatus());
        assertEquals(4, osd.poeStatus());
        assertEquals(220.0, osd.electricSupplyVoltage(), 0.001);
        assertEquals(3600, osd.accTime());
        assertEquals(113.9, osd.longitude(), 0.001);
        assertEquals(1, osd.homePositionIsValid());
    }

    @Test
    @DisplayName("解析嵌套 tilt_angle / battery / self_converge_coordinate")
    void testParseNestedStructs() {
        GroundStationOsd osd = MessageCodec.fromJson(SAMPLE, GroundStationOsd.class);

        assertEquals(1.5, osd.tiltAngle().value(), 0.001);
        assertEquals(1, osd.tiltAngle().valid());
        assertEquals(80, osd.battery().capacityPercent());
        assertEquals(24000, osd.battery().voltage());
        assertEquals(1.2, osd.battery().current(), 0.001);
        assertEquals(30.5, osd.battery().temperature(), 0.001);
        assertEquals(113.9, osd.selfConvergeCoordinate().longitude(), 0.001);
        assertEquals(50.0, osd.selfConvergeCoordinate().height(), 0.001);
    }

    @Test
    @DisplayName("未推送的可选字段反序列化为 null")
    void testUnsetFieldsAreNull() {
        GroundStationOsd osd = MessageCodec.fromJson(SAMPLE, GroundStationOsd.class);

        assertNull(osd.positionState());
        assertNull(osd.alternateLandPoint());
        assertNull(osd.networkState());
        assertNull(osd.storage());
    }

    @Test
    @DisplayName("NON_NULL：null 字段不序列化")
    void testNonNullSerialization() {
        GroundStationOsd osd = new GroundStationOsd(
                0, null, null, null, null, null, null, null, null, null,
                null, null, null, null, 113.9, 22.5, null, null, null, null, null, null);

        String json = MessageCodec.toJson(osd);

        assertTrue(json.contains("\"mode_code\":0"));
        assertTrue(json.contains("\"longitude\":113.9"));
        assertFalse(json.contains("\"electric_supply_power\""));
        assertFalse(json.contains("\"battery\":null"));
    }
}
