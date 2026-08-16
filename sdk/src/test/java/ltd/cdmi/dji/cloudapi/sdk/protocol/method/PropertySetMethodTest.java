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

package ltd.cdmi.dji.cloudapi.sdk.protocol.method;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PropertySetMethod} 枚举的属性名映射与反查。
 *
 * <p><b>核心证明</b>：property/set 通道可设置的 18 个属性名（snake_case 字符串）
 * 与枚举常量一一对应，{@link PropertySetMethod#fromPropertyName(String)} 反查覆盖全部属性。
 */
class PropertySetMethodTest {

    @Test
    @DisplayName("枚举总数应为 18（Dock3 4 + M3D 9 + M3D 红外 5）")
    void testTotalCount() {
        assertEquals(18, PropertySetMethod.values().length);
    }

    @Test
    @DisplayName("propertyName 与已知属性名字符串一一对应")
    void testPropertyNameMapping() {
        assertEquals("air_transfer_enable", PropertySetMethod.AIR_TRANSFER_ENABLE.propertyName());
        assertEquals("silent_mode", PropertySetMethod.SILENT_MODE.propertyName());
        assertEquals("user_experience_improvement", PropertySetMethod.USER_EXPERIENCE_IMPROVEMENT.propertyName());
        assertEquals("obstacle_avoidance", PropertySetMethod.OBSTACLE_AVOIDANCE.propertyName());
        assertEquals("height_limit", PropertySetMethod.HEIGHT_LIMIT.propertyName());
        assertEquals("distance_limit_status", PropertySetMethod.DISTANCE_LIMIT_STATUS.propertyName());
        assertEquals("rth_altitude", PropertySetMethod.RTH_ALTITUDE.propertyName());
        assertEquals("remaining_power_for_return_home", PropertySetMethod.REMAINING_POWER_FOR_RETURN_HOME.propertyName());
        assertEquals("night_lights_state", PropertySetMethod.NIGHT_LIGHTS_STATE.propertyName());
        assertEquals("night_lights_mode", PropertySetMethod.NIGHT_LIGHTS_MODE.propertyName());
        assertEquals("rc_lost_action", PropertySetMethod.RC_LOST_ACTION.propertyName());
        assertEquals("commander_flight_mode", PropertySetMethod.COMMANDER_FLIGHT_MODE.propertyName());
        assertEquals("camera_watermark_settings", PropertySetMethod.CAMERA_WATERMARK_SETTINGS.propertyName());
        assertEquals("thermal_current_palette_style", PropertySetMethod.THERMAL_CURRENT_PALETTE_STYLE.propertyName());
        assertEquals("thermal_gain_mode", PropertySetMethod.THERMAL_GAIN_MODE.propertyName());
        assertEquals("thermal_isotherm_state", PropertySetMethod.THERMAL_ISOTHERM_STATE.propertyName());
        assertEquals("thermal_isotherm_upper_limit", PropertySetMethod.THERMAL_ISOTHERM_UPPER_LIMIT.propertyName());
        assertEquals("thermal_isotherm_lower_limit", PropertySetMethod.THERMAL_ISOTHERM_LOWER_LIMIT.propertyName());
    }

    @Test
    @DisplayName("fromPropertyName 反查：覆盖全部 18 个属性")
    void testFromPropertyName() {
        for (PropertySetMethod expected : PropertySetMethod.values()) {
            assertEquals(expected, PropertySetMethod.fromPropertyName(expected.propertyName()),
                    "propertyName=" + expected.propertyName() + " 反查失败");
        }
    }

    @Test
    @DisplayName("fromPropertyName 未知属性名抛 IllegalArgumentException")
    void testFromPropertyNameUnknown() {
        assertThrows(IllegalArgumentException.class, () -> PropertySetMethod.fromPropertyName("non_existent_property"));
    }

    @Test
    @DisplayName("propertyName 非空")
    void testPropertyNameNonBlank() {
        for (PropertySetMethod m : PropertySetMethod.values()) {
            assertTrue(!m.propertyName().isBlank(),
                    "枚举 " + m.name() + " 的 propertyName 不应为空");
        }
    }

    @Test
    @DisplayName("propertyName 全局唯一（无重复）")
    void testPropertyNameUnique() {
        Map<String, PropertySetMethod> seen = new HashMap<>();
        for (PropertySetMethod m : PropertySetMethod.values()) {
            PropertySetMethod prev = seen.put(m.propertyName(), m);
            assertEquals(null, prev,
                    "propertyName=" + m.propertyName() + " 在 " + m + " 与 " + prev + " 间重复");
        }
    }
}
