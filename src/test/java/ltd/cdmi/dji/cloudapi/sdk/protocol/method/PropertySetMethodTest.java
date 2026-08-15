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
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PropertySetMethod} 枚举的属性名映射。
 *
 * <p><b>核心证明</b>：property/set 通道可设置的 17 个属性名（snake_case 字符串）
 * 与枚举常量一一对应。注：本枚举仅提供 {@link PropertySetMethod#propertyName()}
 * 正向映射，无 {@code fromMethodName} 反查方法，故反查通过测试内建 Map 实现。
 */
class PropertySetMethodTest {

    @Test
    @DisplayName("枚举总数应为 17（Dock3 4 + M3D 8 + M3D 红外 5）")
    void testTotalCount() {
        assertEquals(17, PropertySetMethod.values().length);
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
    @DisplayName("propertyName 双向映射：字符串 → 枚举（自建反查表，覆盖全部 17 个）")
    void testReverseLookup() {
        Map<String, PropertySetMethod> reverse = new HashMap<>();
        for (PropertySetMethod m : PropertySetMethod.values()) {
            reverse.put(m.propertyName(), m);
        }
        // 全部 17 个 propertyName 应能反查到枚举
        for (PropertySetMethod expected : PropertySetMethod.values()) {
            assertEquals(Optional.of(expected), Optional.ofNullable(reverse.get(expected.propertyName())),
                    "propertyName=" + expected.propertyName() + " 反查失败");
        }
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
