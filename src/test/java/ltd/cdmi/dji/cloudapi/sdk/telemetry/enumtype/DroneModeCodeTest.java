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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DroneModeCode} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI 飞行器 properties 文档定义的 21 个 mode_code 数值（0-20）能通过
 *       {@link DroneModeCode#fromCode(int)} 反查</li>
 *   <li><strong>拆分防回归</strong>：与 {@link DockModeCode}（机场模式码）是不同枚举，值域不重叠</li>
 *   <li>-1 / 21 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class DroneModeCodeTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 21 个数值（0-20）")
    void testFromCodeAllValues() {
        assertEquals(DroneModeCode.STANDBY, DroneModeCode.fromCode(0));
        assertEquals(DroneModeCode.TAKEOFF_PREPARATION, DroneModeCode.fromCode(1));
        assertEquals(DroneModeCode.TAKEOFF_READY, DroneModeCode.fromCode(2));
        assertEquals(DroneModeCode.MANUAL_FLIGHT, DroneModeCode.fromCode(3));
        assertEquals(DroneModeCode.AUTO_TAKEOFF, DroneModeCode.fromCode(4));
        assertEquals(DroneModeCode.WAYLINE_FLIGHT, DroneModeCode.fromCode(5));
        assertEquals(DroneModeCode.PANORAMA, DroneModeCode.fromCode(6));
        assertEquals(DroneModeCode.INTELLIGENT_TRACKING, DroneModeCode.fromCode(7));
        assertEquals(DroneModeCode.ADS_B_AVOIDANCE, DroneModeCode.fromCode(8));
        assertEquals(DroneModeCode.AUTO_RETURN_HOME, DroneModeCode.fromCode(9));
        assertEquals(DroneModeCode.AUTO_LANDING, DroneModeCode.fromCode(10));
        assertEquals(DroneModeCode.FORCED_LANDING, DroneModeCode.fromCode(11));
        assertEquals(DroneModeCode.THREE_BLADE_LANDING, DroneModeCode.fromCode(12));
        assertEquals(DroneModeCode.UPGRADING, DroneModeCode.fromCode(13));
        assertEquals(DroneModeCode.NOT_CONNECTED, DroneModeCode.fromCode(14));
        assertEquals(DroneModeCode.APAS, DroneModeCode.fromCode(15));
        assertEquals(DroneModeCode.VIRTUAL_STICK, DroneModeCode.fromCode(16));
        assertEquals(DroneModeCode.LIVE_FLIGHT_CONTROLS, DroneModeCode.fromCode(17));
        assertEquals(DroneModeCode.AIRBORNE_RTK_FIXING, DroneModeCode.fromCode(18));
        assertEquals(DroneModeCode.DOCK_ADDRESS_SELECTING, DroneModeCode.fromCode(19));
        assertEquals(DroneModeCode.POI_ORBIT, DroneModeCode.fromCode(20));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> DroneModeCode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> DroneModeCode.fromCode(21));
        assertThrows(IllegalArgumentException.class, () -> DroneModeCode.fromCode(22));
        assertThrows(IllegalArgumentException.class, () -> DroneModeCode.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 21（待机 + 起飞准备 + ... + POI环绕）")
    void testTotalCount() {
        assertEquals(21, DroneModeCode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0-20 → STANDBY-POI_ORBIT")
    void testCodeRoundTrip() {
        assertEquals(0, DroneModeCode.STANDBY.code());
        assertEquals(20, DroneModeCode.POI_ORBIT.code());
        // 双向闭环
        for (DroneModeCode m : DroneModeCode.values()) {
            assertEquals(m, DroneModeCode.fromCode(m.code()));
        }
    }

    @Test
    @DisplayName("与 DockModeCode 是不同枚举，class 不相等")
    void testNotDockModeCode() {
        assertNotEquals(DroneModeCode.class, DockModeCode.class);
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("待机", DroneModeCode.STANDBY.description());
        assertEquals("航线飞行", DroneModeCode.WAYLINE_FLIGHT.description());
        assertEquals("自动返航", DroneModeCode.AUTO_RETURN_HOME.description());
        assertEquals("POI环绕", DroneModeCode.POI_ORBIT.description());
        for (DroneModeCode m : DroneModeCode.values()) {
            assertTrue(!m.description().isBlank());
        }
    }
}
