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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API State 字段名定义（pushMode=1，状态变化时上报）。
 *
 * <p>与 {@link OsdField}（pushMode=0，周期推送）不同，State 字段仅在设备状态
 * 发生变化时通过 state topic 推送，用于事件驱动的状态同步。
 *
 * <p>收录范围：
 * <ul>
 *   <li>Dock state 字段（{@code thing/product/{dockSn}/state}）</li>
 *   <li>飞行器 state 字段（{@code thing/product/{droneSn}/state}）</li>
 *   <li>Pilot 特有 state 字段</li>
 * </ul>
 *
 * <p>部分字段名在 Dock state 和飞行器 state 中均出现（如 {@code firmware_version}、
 * {@code wireless_link_topo}），枚举中只定义一次，具体归属见各字段注释。
 *
 * <p>注意：以下字段在 DJI 文档中是 pushMode=0（OSD 字段），已在 {@link OsdField} 中定义，
 * 不在本枚举中重复：wireless_link、maintain_status、network_state、drc_state、
 * drone_charge_state、battery_store_mode、cover_state、putter_state、drone_in_dock、mode_code。
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">机场设备属性推送</a></li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">飞行器设备属性推送</a></li>
 * </ul>
 *
 * @see OsdField
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档设备属性推送属性列表中 pushMode=1 的字段")
public enum StateField {

    // ==================== 固件相关（pushMode=1, r）====================

    /** 固件版本（Dock state + 飞行器 state，M400 飞行器 state 除外） */
    FIRMWARE_VERSION("firmware_version", "固件版本"),

    /** 固件升级状态（Dock state + 飞行器 state） */
    FIRMWARE_UPGRADE_STATUS("firmware_upgrade_status", "固件升级状态"),

    /** 固件一致性（Dock state + 飞行器 state） */
    COMPATIBLE_STATUS("compatible_status", "固件一致性"),

    // ==================== 运行信息（pushMode=1, r）====================

    /** 机场累计运行时长，单位秒（Dock state） */
    ACC_TIME("acc_time", "机场累计运行时长"),

    /** 直播状态推送，无在推视频流时为空数组（Dock state） */
    LIVE_STATUS("live_status", "直播状态推送"),

    /** 网关设备直播能力（struct，Dock state，pushMode=1 变化推送） */
    LIVE_CAPACITY("live_capacity", "网关设备直播能力"),

    // ==================== 用户配置（pushMode=1, rw）====================

    /** 一键起飞开关（Dock state，Dock2/Dock3 only） */
    AIR_TRANSFER_ENABLE("air_transfer_enable", "一键起飞开关"),

    /** 用户体验改进计划（Dock state） */
    USER_EXPERIENCE_IMPROVEMENT("user_experience_improvement", "用户体验改进计划"),

    /** 静音模式（Dock state） */
    SILENT_MODE("silent_mode", "静音模式"),

    /** 夜航灯模式（Dock state, rw，机场夜航灯模式设置） */
    NIGHT_LIGHTS_MODE("night_lights_mode", "夜航灯模式"),

    // ==================== 网络通信（pushMode=1, r）====================

    /** RTK 标定源（Dock state, Dock2/Dock3 only） */
    RTCM_INFO("rtcm_info", "RTK标定源"),

    /** 图传连接拓扑（Dock state: Dock2/Dock3 only; 飞行器 state: M3D/M4D only） */
    WIRELESS_LINK_TOPO("wireless_link_topo", "图传连接拓扑"),

    /** 4G Dongle 信息（Dock state: Dock2/Dock3 only; 飞行器 state: M400 only） */
    DONGLE_INFOS("dongle_infos", "4G Dongle信息"),

    /** 离线地图开关（飞行器 state, M400 only） */
    OFFLINE_MAP_ENABLE("offline_map_enable", "离线地图开关"),

    // ==================== 飞行器控制（pushMode=1）====================

    /** 飞行器控制权状态（Dock state, Dock1 only, payloads 部分） */
    DRONE_AUTHORITY_INFO("drone_authority_info", "飞行器控制权状态"),

    /** 负载状态（飞行器 state） */
    PAYLOADS("payloads", "负载状态"),

    /** 当前控制源（飞行器 state） */
    CONTROL_SOURCE("control_source", "当前控制源"),

    /** 飞行器进入当前状态的原因（飞行器 state） */
    MODE_CODE_REASON("mode_code_reason", "飞行器状态原因"),

    // ==================== 指点飞行（pushMode=1）====================

    /** 指点飞行失控动作（飞行器 state） */
    COMMANDER_MODE_LOST_ACTION("commander_mode_lost_action", "指点飞行失控动作"),

    /** 指点飞行模式设置值（飞行器 state, rw — M3D 文档确认 accessMode=rw） */
    COMMANDER_FLIGHT_MODE("commander_flight_mode", "指点飞行模式设置值"),

    /** 指点飞行模式当前值（飞行器 state） */
    CURRENT_COMMANDER_FLIGHT_MODE("current_commander_flight_mode", "指点飞行模式当前值"),

    /** 指点飞行高度（飞行器 state） */
    COMMANDER_FLIGHT_HEIGHT("commander_flight_height", "指点飞行高度"),

    // ==================== 电池告警（pushMode=1, accessMode=r）====================

    /** 低电量告警阈值（飞行器 state, r — M30 文档确认 accessMode=r） */
    LOW_BATTERY_WARNING_THRESHOLD("low_battery_warning_threshold", "低电量告警阈值"),

    /** 严重低电量告警阈值（飞行器 state, r — M30 文档确认 accessMode=r） */
    SERIOUS_LOW_BATTERY_WARNING_THRESHOLD("serious_low_battery_warning_threshold", "严重低电量告警阈值"),

    // ==================== 返航（pushMode=1）====================

    /** 返航高度模式当前值（飞行器 state） */
    CURRENT_RTH_MODE("current_rth_mode", "返航高度模式当前值"),

    /** 返航高度模式设置值（飞行器 state, r — M3D/M4D 文档确认 accessMode=r，大疆机场不支持设置返航高度模式） */
    RTH_MODE("rth_mode", "返航高度模式设置值"),

    // ==================== Home 点（pushMode=1）====================

    /** Home 点经度（飞行器 state） */
    HOME_LONGITUDE("home_longitude", "Home点经度"),

    /** Home 点纬度（飞行器 state） */
    HOME_LATITUDE("home_latitude", "Home点纬度"),

    // ==================== 航线（pushMode=1）====================

    /** 航线解析库版本号（飞行器 state） */
    WPMZ_VERSION("wpmz_version", "航线解析库版本号"),

    // ==================== PSDK（pushMode=1）====================

    /** PSDK UI 资源包（飞行器 state） */
    PSDK_UI_RESOURCE("psdk_ui_resource", "PSDK UI资源包"),

    /** PSDK 负载设备属性值（飞行器 state） */
    PSDK_WIDGET_VALUES("psdk_widget_values", "PSDK负载设备属性值"),

    // ==================== 热成像（pushMode=1）====================

    /** 云台热成像结构，含 thermal_supported_palette_styles（飞行器 state, M3D/M4D thermal only） */
    TYPE_SUBTYPE_GIMBALINDEX("type_subtype_gimbalindex", "云台热成像结构"),

    // ==================== 相机水印（pushMode=1, rw）====================

    /** 相机水印设置（飞行器 state, rw — M3D 文档确认 accessMode=rw） */
    CAMERA_WATERMARK_SETTINGS("camera_watermark_settings", "相机水印设置"),

    // ==================== Pilot 特有（pushMode=1）====================

    /** 云控授权状态（Pilot 特有，通过 state topic 上报） */
    CLOUD_CONTROL_AUTH("cloud_control_auth", "云控授权状态");

    /** fieldName → 枚举值 的不可变查找表 */
    private static final Map<String, StateField> BY_FIELD_NAME =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(StateField::fieldName, Function.identity()));

    private final String fieldName;
    private final String description;

    StateField(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 返回 DJI 协议中的字段名字符串（snake_case）。
     *
     * @return 字段名，如 {@code "night_lights_mode"}
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * 返回字段的中文描述。
     *
     * @return 描述文本，如 {@code "夜航灯模式"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据 DJI 协议字段名查找对应的枚举值。
     *
     * @param fieldName DJI 协议字段名（snake_case），如 {@code "night_lights_mode"}
     * @return 对应的 {@link StateField} 枚举值
     * @throws IllegalArgumentException 如果字段名不存在于已知 State 字段中
     */
    public static StateField fromFieldName(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("未知的 State 字段名: null");
        }
        StateField field = BY_FIELD_NAME.get(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("未知的 State 字段名: " + fieldName);
        }
        return field;
    }
}
