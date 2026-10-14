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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 O4 地面站枚举族与新增飞行器枚举的 code 反查与值域。
 *
 * <p>覆盖：{@link GroundStationModeCode}（含不连续 9）、{@link SupplyPowerMode}、
 * {@link BasestationWorkMode}、{@link UnderWorkMode}、{@link LanStatus}、{@link PoeStatus}、
 * {@link RtcmDeviceType}、{@link RtcmSourceType}、{@link FixedState}、{@link CalibrationState}、
 * {@link PeripheralPort}、{@link PeripheralType}、{@link GroundStationHomePositionIsValid}、
 * {@link FtsStatus}、{@link IrMeteringMode}。
 */
class GroundStationEnumTest {

    @Test
    @DisplayName("GroundStationModeCode：值域不连续（0-6, 9）")
    void testGroundStationModeCode() {
        assertEquals(GroundStationModeCode.IDLE, GroundStationModeCode.fromCode(0));
        assertEquals(GroundStationModeCode.WORKING, GroundStationModeCode.fromCode(4));
        assertEquals(GroundStationModeCode.LOG_UPLOADING, GroundStationModeCode.fromCode(9));
        assertEquals(8, GroundStationModeCode.values().length);
        assertThrows(IllegalArgumentException.class, () -> GroundStationModeCode.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> GroundStationModeCode.fromCode(8));
    }

    @Test
    @DisplayName("SupplyPowerMode：0-2（电池/PD/POE）")
    void testSupplyPowerMode() {
        assertEquals(SupplyPowerMode.BATTERY, SupplyPowerMode.fromCode(0));
        assertEquals(SupplyPowerMode.PD, SupplyPowerMode.fromCode(1));
        assertEquals(SupplyPowerMode.POE, SupplyPowerMode.fromCode(2));
        assertEquals(3, SupplyPowerMode.values().length);
    }

    @Test
    @DisplayName("BasestationWorkMode / UnderWorkMode")
    void testWorkModes() {
        assertEquals(BasestationWorkMode.ROAMING_BASESTATION, BasestationWorkMode.fromCode(0));
        assertEquals(BasestationWorkMode.SIMPLE_DOCK, BasestationWorkMode.fromCode(2));
        assertEquals(3, BasestationWorkMode.values().length);
        assertEquals(UnderWorkMode.NORMAL, UnderWorkMode.fromCode(0));
        assertEquals(UnderWorkMode.UNDER_SHED, UnderWorkMode.fromCode(1));
        assertEquals(2, UnderWorkMode.values().length);
    }

    @Test
    @DisplayName("LanStatus / PoeStatus：均 5 值（0-4）")
    void testLanAndPoeStatus() {
        assertEquals(LanStatus.DISCONNECTED, LanStatus.fromCode(0));
        assertEquals(LanStatus.NOT_SUPPORTED, LanStatus.fromCode(4));
        assertEquals(5, LanStatus.values().length);
        assertEquals(PoeStatus.NONE, PoeStatus.fromCode(0));
        assertEquals(PoeStatus.W_71, PoeStatus.fromCode(4));
        assertEquals(5, PoeStatus.values().length);
    }

    @Test
    @DisplayName("RtcmDeviceType / RtcmSourceType")
    void testRtcmEnums() {
        assertEquals(RtcmDeviceType.REMOTE_CONTROLLER, RtcmDeviceType.fromCode(0));
        assertEquals(RtcmDeviceType.RELAY, RtcmDeviceType.fromCode(2));
        assertEquals(3, RtcmDeviceType.values().length);
        assertEquals(RtcmSourceType.NOT_CALIBRATED, RtcmSourceType.fromCode(0));
        assertEquals(RtcmSourceType.NETWORK_RTK, RtcmSourceType.fromCode(3));
        assertEquals(4, RtcmSourceType.values().length);
    }

    @Test
    @DisplayName("FixedState / CalibrationState")
    void testPositionFixedCalibration() {
        assertEquals(FixedState.CONVERGED, FixedState.fromCode(2));
        assertEquals(FixedState.FAILED, FixedState.fromCode(3));
        assertEquals(4, FixedState.values().length);
        assertEquals(CalibrationState.CALIBRATED, CalibrationState.fromCode(1));
        assertEquals(2, CalibrationState.values().length);
    }

    @Test
    @DisplayName("PeripheralPort / PeripheralType")
    void testPeripheralEnums() {
        assertEquals(PeripheralPort.LAN, PeripheralPort.fromCode(0));
        assertEquals(PeripheralPort.WAN, PeripheralPort.fromCode(1));
        assertEquals(2, PeripheralPort.values().length);
        assertEquals(PeripheralType.OTHER, PeripheralType.fromCode(0));
        assertEquals(PeripheralType.RID, PeripheralType.fromCode(7));
        assertEquals(8, PeripheralType.values().length);
    }

    @Test
    @DisplayName("GroundStationHomePositionIsValid：2 值独立枚举（不复用机场枚举）")
    void testGroundStationHomePositionIsValid() {
        assertEquals(GroundStationHomePositionIsValid.INVALID, GroundStationHomePositionIsValid.fromCode(0));
        assertEquals(GroundStationHomePositionIsValid.VALID, GroundStationHomePositionIsValid.fromCode(1));
        assertEquals(2, GroundStationHomePositionIsValid.values().length);
        assertThrows(IllegalArgumentException.class, () -> GroundStationHomePositionIsValid.fromCode(2));
    }

    @Test
    @DisplayName("FtsStatus：值域不连续（0-4, 99）")
    void testFtsStatus() {
        assertEquals(FtsStatus.DISCONNECTED, FtsStatus.fromCode(0));
        assertEquals(FtsStatus.PROPELLER_STOP_TRIGGERED, FtsStatus.fromCode(3));
        assertEquals(FtsStatus.UNKNOWN_ERROR, FtsStatus.fromCode(99));
        assertEquals(6, FtsStatus.values().length);
        assertThrows(IllegalArgumentException.class, () -> FtsStatus.fromCode(5));
    }

    @Test
    @DisplayName("IrMeteringMode：0-2（关闭/点/区域）")
    void testIrMeteringMode() {
        assertEquals(IrMeteringMode.CLOSE, IrMeteringMode.fromCode(0));
        assertEquals(IrMeteringMode.POINT, IrMeteringMode.fromCode(1));
        assertEquals(IrMeteringMode.AREA, IrMeteringMode.fromCode(2));
        assertEquals(3, IrMeteringMode.values().length);
        assertThrows(IllegalArgumentException.class, () -> IrMeteringMode.fromCode(3));
    }
}
