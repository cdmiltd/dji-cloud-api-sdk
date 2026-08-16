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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link OsdField} 枚举的 fieldName 反查、往返一致性与唯一性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock/飞行器 properties 文档定义的 65 个 OSD 字段名（snake_case）
 *       能通过 {@link OsdField#fromFieldName(String)} 反查到枚举常量</li>
 *   <li>未知 fieldName（含 null / 空串 / 空白串）均抛出 {@link IllegalArgumentException}</li>
 *   <li>{@link OsdField#fieldName()} 与 {@link OsdField#fromFieldName(String)} 形成双射（往返闭环）</li>
 *   <li>所有 fieldName 互不重复（保证查找表无歧义）</li>
 * </ol>
 */
class OsdFieldTest {

    // ==================== fromFieldName 正向查找 ====================

    @Test
    @DisplayName("fromFieldName 反查机场 OSD 代表字段（Dock2/Dock3 properties）")
    void testFromFieldNameDockOsdSpotCheck() {
        assertEquals(OsdField.JOB_NUMBER, OsdField.fromFieldName("job_number"));
        assertEquals(OsdField.MODE_CODE, OsdField.fromFieldName("mode_code"));
        assertEquals(OsdField.LATITUDE, OsdField.fromFieldName("latitude"));
        assertEquals(OsdField.LONGITUDE, OsdField.fromFieldName("longitude"));
        assertEquals(OsdField.HEIGHT, OsdField.fromFieldName("height"));
        assertEquals(OsdField.COVER_STATE, OsdField.fromFieldName("cover_state"));
        assertEquals(OsdField.DRONE_IN_DOCK, OsdField.fromFieldName("drone_in_dock"));
        assertEquals(OsdField.HOME_POSITION_IS_VALID, OsdField.fromFieldName("home_position_is_valid"));
        assertEquals(OsdField.HEADING, OsdField.fromFieldName("heading"));
        assertEquals(OsdField.DRONE_CHARGE_STATE, OsdField.fromFieldName("drone_charge_state"));
        assertEquals(OsdField.TEMPERATURE, OsdField.fromFieldName("temperature"));
        assertEquals(OsdField.WIND_SPEED, OsdField.fromFieldName("wind_speed"));
        assertEquals(OsdField.RAINFALL, OsdField.fromFieldName("rainfall"));
        assertEquals(OsdField.AIR_CONDITIONER, OsdField.fromFieldName("air_conditioner"));
        assertEquals(OsdField.EMERGENCY_STOP_STATE, OsdField.fromFieldName("emergency_stop_state"));
        assertEquals(OsdField.ALARM_STATE, OsdField.fromFieldName("alarm_state"));
        assertEquals(OsdField.PUTTER_STATE, OsdField.fromFieldName("putter_state"));
        assertEquals(OsdField.BATTERY_STORE_MODE, OsdField.fromFieldName("battery_store_mode"));
        assertEquals(OsdField.WIRELESS_LINK, OsdField.fromFieldName("wireless_link"));
        assertEquals(OsdField.DRC_STATE, OsdField.fromFieldName("drc_state"));
    }

    @Test
    @DisplayName("fromFieldName 反查飞行器 OSD 代表字段（M30/M3D/M4D properties）")
    void testFromFieldNameDroneOsdSpotCheck() {
        assertEquals(OsdField.ATTITUDE_PITCH, OsdField.fromFieldName("attitude_pitch"));
        assertEquals(OsdField.ATTITUDE_ROLL, OsdField.fromFieldName("attitude_roll"));
        assertEquals(OsdField.ATTITUDE_HEAD, OsdField.fromFieldName("attitude_head"));
        assertEquals(OsdField.HORIZONTAL_SPEED, OsdField.fromFieldName("horizontal_speed"));
        assertEquals(OsdField.VERTICAL_SPEED, OsdField.fromFieldName("vertical_speed"));
        assertEquals(OsdField.WIND_DIRECTION, OsdField.fromFieldName("wind_direction"));
        assertEquals(OsdField.BATTERY, OsdField.fromFieldName("battery"));
        assertEquals(OsdField.CAMERAS, OsdField.fromFieldName("cameras"));
        assertEquals(OsdField.TOTAL_FLIGHT_TIME, OsdField.fromFieldName("total_flight_time"));
        assertEquals(OsdField.FIRMWARE_VERSION, OsdField.fromFieldName("firmware_version"));
        assertEquals(OsdField.GEAR, OsdField.fromFieldName("gear"));
        assertEquals(OsdField.HEIGHT_LIMIT, OsdField.fromFieldName("height_limit"));
        assertEquals(OsdField.HOME_DISTANCE, OsdField.fromFieldName("home_distance"));
        assertEquals(OsdField.RTH_ALTITUDE, OsdField.fromFieldName("rth_altitude"));
        assertEquals(OsdField.NIGHT_LIGHTS_STATE, OsdField.fromFieldName("night_lights_state"));
        assertEquals(OsdField.OBSTACLE_AVOIDANCE, OsdField.fromFieldName("obstacle_avoidance"));
        assertEquals(OsdField.TOTAL_FLIGHT_DISTANCE, OsdField.fromFieldName("total_flight_distance"));
        assertEquals(OsdField.TOTAL_FLIGHT_SORTIES, OsdField.fromFieldName("total_flight_sorties"));
        assertEquals(OsdField.TRACK_ID, OsdField.fromFieldName("track_id"));
        assertEquals(OsdField.ELEVATION, OsdField.fromFieldName("elevation"));
    }

    // ==================== fromFieldName 反向查找（未知 fieldName）====================

    @Test
    @DisplayName("fromFieldName 未知 fieldName 抛出 IllegalArgumentException")
    void testFromFieldNameUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("nonexistent_field"));
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("mode-code"));
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("MODE_CODE"));
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName(" mode_code"));
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("mode_code "));
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("modecode"));
    }

    // ==================== fromFieldName 边界值 ====================

    @Test
    @DisplayName("fromFieldName(null) 抛出 IllegalArgumentException（与 Javadoc 声明一致）")
    void testFromFieldNameNullThrowsIae() {
        // Javadoc 声明 @throws IllegalArgumentException；源码在查找前对 null 做了显式校验，
        // 避免 Collectors.toUnmodifiableMap 底层 MapN.get(null) 触发 NPE。
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName(null));
    }

    @Test
    @DisplayName("fromFieldName 边界值：空串 / 空白串（空格/Tab/换行）均抛出 IllegalArgumentException")
    void testFromFieldNameBlankThrowsIae() {
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName(""),
                "空字符串应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName(" "),
                "单空格应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("   "),
                "多空格应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("\t"),
                "Tab 应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> OsdField.fromFieldName("\n"),
                "换行符应抛 IllegalArgumentException");
    }

    // ==================== 往返一致性（round-trip）====================

    @Test
    @DisplayName("fieldName() 与 fromFieldName() 形成双射：所有 65 个枚举值往返闭环")
    void testFieldNameRoundTrip() {
        for (OsdField field : OsdField.values()) {
            String name = field.fieldName();
            OsdField resolved = OsdField.fromFieldName(name);
            assertEquals(field, resolved,
                    "Round-trip 失败: " + field + " → \"" + name + "\" → " + resolved);
        }
    }

    // ==================== 枚举值总数验证 ====================

    @Test
    @DisplayName("枚举总数应为 68（机场 OSD 40 + 飞行器 OSD 28）")
    void testTotalCount() {
        assertEquals(68, OsdField.values().length);
    }

    // ==================== fieldName 唯一性验证 ====================

    @Test
    @DisplayName("所有 fieldName 互不重复（保证查找表无歧义）")
    void testFieldNameUniqueness() {
        Set<String> seen = new HashSet<>();
        for (OsdField field : OsdField.values()) {
            String name = field.fieldName();
            assertTrue(seen.add(name),
                    "发现重复 fieldName: \"" + name + "\" (来自 " + field + ")");
        }
        // 双重保险：distinct 计数应等于枚举总数
        long distinctCount = Arrays.stream(OsdField.values())
                .map(OsdField::fieldName)
                .distinct()
                .count();
        assertEquals(OsdField.values().length, distinctCount,
                "distinct fieldName 数量应等于枚举总数");
    }

    // ==================== description 非空验证 ====================

    @Test
    @DisplayName("所有 description 非空且非空白")
    void testDescriptionNonBlank() {
        for (OsdField field : OsdField.values()) {
            assertTrue(!field.description().isBlank(),
                    "description 为空: " + field);
        }
    }
}
