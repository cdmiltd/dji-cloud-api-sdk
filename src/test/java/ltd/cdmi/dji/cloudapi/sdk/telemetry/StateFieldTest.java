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
 * 验证 {@link StateField} 枚举的 fieldName 反查、往返一致性与唯一性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock/飞行器 state 文档定义的 34 个 State 字段名（snake_case，pushMode=1）
 *       能通过 {@link StateField#fromFieldName(String)} 反查到枚举常量</li>
 *   <li>未知 fieldName（含 null / 空串 / 空白串）均抛出 {@link IllegalArgumentException}</li>
 *   <li>{@link StateField#fieldName()} 与 {@link StateField#fromFieldName(String)} 形成双射（往返闭环）</li>
 *   <li>所有 fieldName 互不重复（保证查找表无歧义）</li>
 * </ol>
 */
class StateFieldTest {

    // ==================== fromFieldName 正向查找 ====================

    @Test
    @DisplayName("fromFieldName 反查固件/运行信息相关字段（Dock state）")
    void testFromFieldNameFirmwareAndRuntimeSpotCheck() {
        assertEquals(StateField.FIRMWARE_VERSION, StateField.fromFieldName("firmware_version"));
        assertEquals(StateField.FIRMWARE_UPGRADE_STATUS, StateField.fromFieldName("firmware_upgrade_status"));
        assertEquals(StateField.COMPATIBLE_STATUS, StateField.fromFieldName("compatible_status"));
        assertEquals(StateField.ACC_TIME, StateField.fromFieldName("acc_time"));
        assertEquals(StateField.LIVE_STATUS, StateField.fromFieldName("live_status"));
        assertEquals(StateField.LIVE_CAPACITY, StateField.fromFieldName("live_capacity"));
    }

    @Test
    @DisplayName("fromFieldName 反查用户配置/网络通信相关字段（Dock state）")
    void testFromFieldNameConfigAndNetworkSpotCheck() {
        assertEquals(StateField.AIR_TRANSFER_ENABLE, StateField.fromFieldName("air_transfer_enable"));
        assertEquals(StateField.USER_EXPERIENCE_IMPROVEMENT, StateField.fromFieldName("user_experience_improvement"));
        assertEquals(StateField.SILENT_MODE, StateField.fromFieldName("silent_mode"));
        assertEquals(StateField.NIGHT_LIGHTS_MODE, StateField.fromFieldName("night_lights_mode"));
        assertEquals(StateField.RTCM_INFO, StateField.fromFieldName("rtcm_info"));
        assertEquals(StateField.WIRELESS_LINK_TOPO, StateField.fromFieldName("wireless_link_topo"));
        assertEquals(StateField.DONGLE_INFOS, StateField.fromFieldName("dongle_infos"));
        assertEquals(StateField.OFFLINE_MAP_ENABLE, StateField.fromFieldName("offline_map_enable"));
    }

    @Test
    @DisplayName("fromFieldName 反查飞行器控制/指点飞行/返航相关字段（飞行器 state）")
    void testFromFieldNameAircraftControlSpotCheck() {
        assertEquals(StateField.DRONE_AUTHORITY_INFO, StateField.fromFieldName("drone_authority_info"));
        assertEquals(StateField.PAYLOADS, StateField.fromFieldName("payloads"));
        assertEquals(StateField.CONTROL_SOURCE, StateField.fromFieldName("control_source"));
        assertEquals(StateField.MODE_CODE_REASON, StateField.fromFieldName("mode_code_reason"));
        assertEquals(StateField.COMMANDER_MODE_LOST_ACTION, StateField.fromFieldName("commander_mode_lost_action"));
        assertEquals(StateField.COMMANDER_FLIGHT_MODE, StateField.fromFieldName("commander_flight_mode"));
        assertEquals(StateField.CURRENT_COMMANDER_FLIGHT_MODE, StateField.fromFieldName("current_commander_flight_mode"));
        assertEquals(StateField.COMMANDER_FLIGHT_HEIGHT, StateField.fromFieldName("commander_flight_height"));
        assertEquals(StateField.CURRENT_RTH_MODE, StateField.fromFieldName("current_rth_mode"));
        assertEquals(StateField.RTH_MODE, StateField.fromFieldName("rth_mode"));
    }

    @Test
    @DisplayName("fromFieldName 反查电池告警/Home点/航线/PSDK/热成像/水印/Pilot 字段")
    void testFromFieldNameMiscSpotCheck() {
        assertEquals(StateField.LOW_BATTERY_WARNING_THRESHOLD, StateField.fromFieldName("low_battery_warning_threshold"));
        assertEquals(StateField.SERIOUS_LOW_BATTERY_WARNING_THRESHOLD, StateField.fromFieldName("serious_low_battery_warning_threshold"));
        assertEquals(StateField.HOME_LONGITUDE, StateField.fromFieldName("home_longitude"));
        assertEquals(StateField.HOME_LATITUDE, StateField.fromFieldName("home_latitude"));
        assertEquals(StateField.WPMZ_VERSION, StateField.fromFieldName("wpmz_version"));
        assertEquals(StateField.PSDK_UI_RESOURCE, StateField.fromFieldName("psdk_ui_resource"));
        assertEquals(StateField.PSDK_WIDGET_VALUES, StateField.fromFieldName("psdk_widget_values"));
        assertEquals(StateField.TYPE_SUBTYPE_GIMBALINDEX, StateField.fromFieldName("type_subtype_gimbalindex"));
        assertEquals(StateField.CAMERA_WATERMARK_SETTINGS, StateField.fromFieldName("camera_watermark_settings"));
        assertEquals(StateField.CLOUD_CONTROL_AUTH, StateField.fromFieldName("cloud_control_auth"));
    }

    // ==================== fromFieldName 反向查找（未知 fieldName）====================

    @Test
    @DisplayName("fromFieldName 未知 fieldName 抛出 IllegalArgumentException")
    void testFromFieldNameUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("nonexistent_field"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("firmware-version"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("FIRMWARE_VERSION"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName(" firmware_version"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("firmware_version "));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("firmwareversion"));
        // OSD 字段（pushMode=0）不应在 State 枚举中找到
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("mode_code"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("cover_state"));
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("wireless_link"));
    }

    // ==================== fromFieldName 边界值 ====================

    @Test
    @DisplayName("fromFieldName(null) 抛出 IllegalArgumentException（与 Javadoc 声明一致）")
    void testFromFieldNameNullThrowsIae() {
        // Javadoc 声明 @throws IllegalArgumentException；源码在查找前对 null 做了显式校验，
        // 避免 Collectors.toUnmodifiableMap 底层 MapN.get(null) 触发 NPE。
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName(null));
    }

    @Test
    @DisplayName("fromFieldName 边界值：空串 / 空白串（空格/Tab/换行）均抛出 IllegalArgumentException")
    void testFromFieldNameBlankThrowsIae() {
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName(""),
                "空字符串应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName(" "),
                "单空格应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("   "),
                "多空格应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("\t"),
                "Tab 应抛 IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> StateField.fromFieldName("\n"),
                "换行符应抛 IllegalArgumentException");
    }

    // ==================== 往返一致性（round-trip）====================

    @Test
    @DisplayName("fieldName() 与 fromFieldName() 形成双射：所有 34 个枚举值往返闭环")
    void testFieldNameRoundTrip() {
        for (StateField field : StateField.values()) {
            String name = field.fieldName();
            StateField resolved = StateField.fromFieldName(name);
            assertEquals(field, resolved,
                    "Round-trip 失败: " + field + " → \"" + name + "\" → " + resolved);
        }
    }

    // ==================== 枚举值总数验证 ====================

    @Test
    @DisplayName("枚举总数应为 34（固件 3 + 运行 3 + 用户配置 4 + 网络 4 + 飞行器控制 4 + 指点 4 + 电池告警 2 + 返航 2 + Home 2 + 航线 1 + PSDK 2 + 热成像 1 + 水印 1 + Pilot 1）")
    void testTotalCount() {
        assertEquals(34, StateField.values().length);
    }

    // ==================== fieldName 唯一性验证 ====================

    @Test
    @DisplayName("所有 fieldName 互不重复（保证查找表无歧义）")
    void testFieldNameUniqueness() {
        Set<String> seen = new HashSet<>();
        for (StateField field : StateField.values()) {
            String name = field.fieldName();
            assertTrue(seen.add(name),
                    "发现重复 fieldName: \"" + name + "\" (来自 " + field + ")");
        }
        // 双重保险：distinct 计数应等于枚举总数
        long distinctCount = Arrays.stream(StateField.values())
                .map(StateField::fieldName)
                .distinct()
                .count();
        assertEquals(StateField.values().length, distinctCount,
                "distinct fieldName 数量应等于枚举总数");
    }

    // ==================== description 非空验证 ====================

    @Test
    @DisplayName("所有 description 非空且非空白")
    void testDescriptionNonBlank() {
        for (StateField field : StateField.values()) {
            assertTrue(!field.description().isBlank(),
                    "description 为空: " + field);
        }
    }
}
