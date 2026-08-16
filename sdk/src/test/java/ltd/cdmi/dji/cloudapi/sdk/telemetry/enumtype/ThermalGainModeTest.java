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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ThermalGainMode} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI M3D properties 文档定义的 3 个 thermal_gain_mode 数值
 *       （0=自动, 1=低增益, 2=高增益）能通过 {@link ThermalGainMode#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 3 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保测温范围语义不混入错误值</li>
 *   <li>description 含测温范围信息（低增益 0°C-500°C / 高增益 -20°C-150°C），帮助开发者选择</li>
 * </ol>
 */
class ThermalGainModeTest {

    @Test
    @DisplayName("fromCode 反查 DJI M3D 文档定义的 3 个数值（0, 1, 2）")
    void testFromCodeAllValues() {
        assertEquals(ThermalGainMode.AUTO, ThermalGainMode.fromCode(0));
        assertEquals(ThermalGainMode.LOW, ThermalGainMode.fromCode(1));
        assertEquals(ThermalGainMode.HIGH, ThermalGainMode.fromCode(2));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> ThermalGainMode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> ThermalGainMode.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> ThermalGainMode.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> ThermalGainMode.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 3（自动 + 低增益 + 高增益）")
    void testTotalCount() {
        assertEquals(3, ThermalGainMode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→AUTO, 1→LOW, 2→HIGH")
    void testCodeRoundTrip() {
        assertEquals(0, ThermalGainMode.AUTO.code());
        assertEquals(1, ThermalGainMode.LOW.code());
        assertEquals(2, ThermalGainMode.HIGH.code());
        // 双向闭环
        for (ThermalGainMode mode : ThermalGainMode.values()) {
            assertEquals(mode, ThermalGainMode.fromCode(mode.code()));
        }
    }

    @Test
    @DisplayName("description 非空且含测温范围信息（低/高增益的测温范围是开发者选择依据）")
    void testDescription() {
        assertEquals("自动", ThermalGainMode.AUTO.description());
        assertEquals("低增益, 测温范围0°C-500°C", ThermalGainMode.LOW.description());
        assertEquals("高增益, 测温范围-20°C-150°C", ThermalGainMode.HIGH.description());
        for (ThermalGainMode mode : ThermalGainMode.values()) {
            assertTrue(!mode.description().isBlank());
        }
    }
}
