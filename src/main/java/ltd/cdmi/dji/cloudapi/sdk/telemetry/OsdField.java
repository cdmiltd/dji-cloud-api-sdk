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
 * DJI Cloud API OSD（遥测）字段名定义。
 *
 * <p>OSD 字段对应 pushMode=0 的设备属性，按固定周期推送，反映设备实时状态。
 * 机场 OSD 字段来源于 DJI Dock properties 文档，飞行器 OSD 字段来源于
 * M30/M3D/M4D properties 文档。
 *
 * <p>每个枚举值包含两个字段：
 * <ul>
 *   <li>{@link #fieldName()} — DJI 协议中的字段名字符串（snake_case），用于 MQTT 消息序列化</li>
 *   <li>{@link #description()} — 中文描述，便于开发者理解字段含义</li>
 * </ul>
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">机场设备属性推送</a></li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">飞行器设备属性推送</a></li>
 * </ul>
 *
 * @see StateField
 * @see DockOsd
 * @see DroneOsd
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档机场/飞行器设备属性推送属性列表，字段名与 pushMode 核实一致")
public enum OsdField {

    // ==================== 机场 OSD 字段（DockOsd） ====================

    /** 机场累计作业次数 */
    JOB_NUMBER("job_number", "机场累计作业次数"),

    /** 机场激活时间（unix 秒） */
    ACTIVATION_TIME("activation_time", "激活时间"),

    /** 工作电流（mA） */
    WORKING_CURRENT("working_current", "工作电流(mA)"),

    /** 工作电压（mV） */
    WORKING_VOLTAGE("working_voltage", "工作电压(mV)"),

    /** 市电电压（V） */
    ELECTRIC_SUPPLY_VOLTAGE("electric_supply_voltage", "市电电压(V)"),

    /** 备用电池信息（struct：switch/voltage/temperature） */
    BACKUP_BATTERY("backup_battery", "备用电池"),

    /** 飞行器电池保养信息（struct：maintenance_state/maintenance_time_left/heat_state/batteries） */
    DRONE_BATTERY_MAINTENANCE_INFO("drone_battery_maintenance_info", "飞行器电池保养信息"),

    /** 保养状态（struct：maintain_status_array） */
    MAINTAIN_STATUS("maintain_status", "保养状态"),

    /** 任务步骤码 */
    FLIGHTTASK_STEP_CODE("flighttask_step_code", "任务步骤码"),

    /** 媒体文件详情（struct：remain_upload） */
    MEDIA_FILE_DETAIL("media_file_detail", "媒体文件详情"),

    /** 无线链路（struct：dongle_number/4g_link_state/sdr_link_state/link_workmode 等） */
    WIRELESS_LINK("wireless_link", "无线链路"),

    /** DRC 链路状态 */
    DRC_STATE("drc_state", "DRC链路状态"),

    /** 模式码（机场见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DockModeCode}，飞行器见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode}） */
    MODE_CODE("mode_code", "模式码"),

    /** 纬度 */
    LATITUDE("latitude", "纬度"),

    /** 经度 */
    LONGITUDE("longitude", "经度"),

    /** 高度 */
    HEIGHT("height", "高度"),

    /** 网络状态（struct：type/quality/rate） */
    NETWORK_STATE("network_state", "网络状态"),

    /** 存储（struct：total/used） */
    STORAGE("storage", "存储"),

    /** 子设备信息（struct：device_sn/device_model_key/device_online_status/device_paired） */
    SUB_DEVICE("sub_device", "子设备"),

    /** 舱盖状态（0=关闭，1=打开） */
    COVER_STATE("cover_state", "舱盖状态"),

    /** 飞行器在舱（0=不在舱，1=在舱） */
    DRONE_IN_DOCK("drone_in_dock", "飞行器在舱"),

    /** Home 点有效性（0=无效，1=有效，Dock2/Dock3 only） */
    HOME_POSITION_IS_VALID("home_position_is_valid", "Home点有效性"),

    /** 机场朝向角（0-359，Dock2/Dock3 only） */
    HEADING("heading", "机场朝向角"),

    /** 飞行器充电状态（struct：capacity_percent/state） */
    DRONE_CHARGE_STATE("drone_charge_state", "飞行器充电状态"),

    /** 机场温度（°C） */
    TEMPERATURE("temperature", "温度"),

    /** 湿度（%） */
    HUMIDITY("humidity", "湿度"),

    /** 风速（0.1 m/s，上报值需除以 10 得到实际 m/s） */
    WIND_SPEED("wind_speed", "风速"),

    /** 降雨量（mm） */
    RAINFALL("rainfall", "降雨量"),

    /** 环境温度（°C） */
    ENVIRONMENT_TEMPERATURE("environment_temperature", "环境温度"),

    /** 补光灯状态（0=关闭，1=打开） */
    SUPPLEMENT_LIGHT_STATE("supplement_light_state", "补光灯状态"),

    /** 机场空调（struct：air_conditioner_state/switch_time） */
    AIR_CONDITIONER("air_conditioner", "机场空调"),

    /** 紧急停止按钮状态（0=未按下，1=按下） */
    EMERGENCY_STOP_STATE("emergency_stop_state", "紧急停止按钮状态"),

    /** 声光报警状态（0=关闭，1=开启） */
    ALARM_STATE("alarm_state", "声光报警状态"),

    /** 推杆状态（0=收回，1=展开） */
    PUTTER_STATE("putter_state", "推杆状态"),

    /** 电池存储模式 */
    BATTERY_STORE_MODE("battery_store_mode", "电池存储模式"),

    /** 备用降落点（struct：longitude/latitude/safe_land_height/is_configured/height） */
    ALTERNATE_LAND_POINT("alternate_land_point", "备用降落点"),

    /** 首次上电时间（unix 毫秒） */
    FIRST_POWER_ON("first_power_on", "首次上电时间"),

    /** 定位状态（struct：is_calibration/is_fixed/quality/gps_number/rtk_number） */
    POSITION_STATE("position_state", "定位状态"),

    /** 自收敛坐标（struct，pushMode=0 周期推送） */
    SELF_CONVERGE_COORDINATE("self_converge_coordinate", "自收敛坐标"),

    // ==================== 飞行器 OSD 字段（DroneOsd） ====================

    /** 俯仰角（°） */
    ATTITUDE_PITCH("attitude_pitch", "俯仰角"),

    /** 横滚角（°） */
    ATTITUDE_ROLL("attitude_roll", "横滚角"),

    /** 航向角（°） */
    ATTITUDE_HEAD("attitude_head", "航向角"),

    /** 水平速度（m/s） */
    HORIZONTAL_SPEED("horizontal_speed", "水平速度"),

    /** 垂直速度（m/s） */
    VERTICAL_SPEED("vertical_speed", "垂直速度"),

    /** 风向（枚举 1-8，正北到西北） */
    WIND_DIRECTION("wind_direction", "风向"),

    /** 国家区域码（Pilot to Cloud 遥控器 OSD，如 "CN"，仅 RC Pro 上报） */
    COUNTRY("country", "国家区域码"),

    /** RID 工作状态（true=正常，飞行器 OSD） */
    RID_STATE("rid_state", "RID工作状态"),

    /** 遥控器失控动作（0=悬停,1=降落,2=返航,3=上升，rw，飞行器 OSD） */
    RC_LOST_ACTION("rc_lost_action", "遥控器失控动作"),

    /** 飞行器相机信息（array of struct，飞行器 OSD） */
    CAMERAS("cameras", "相机信息"),

    /** 电池信息（struct：capacity_percent/remain_flight_time/return_home_power/landing_power/batteries） */
    BATTERY("battery", "电池"),

    /** 累计飞行时间（秒） */
    TOTAL_FLIGHT_TIME("total_flight_time", "累计飞行时间"),

    /** 固件版本 */
    FIRMWARE_VERSION("firmware_version", "固件版本"),

    /** 档位（见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.Gear}） */
    GEAR("gear", "档位"),

    /** 限高（米） */
    HEIGHT_LIMIT("height_limit", "限高"),

    /** 距 Home 点距离（米） */
    HOME_DISTANCE("home_distance", "距Home距离"),

    /** 限远状态（struct：state/distance_limit/is_near_distance_limit） */
    DISTANCE_LIMIT_STATUS("distance_limit_status", "限远状态"),

    /** 返航高度（米） */
    RTH_ALTITUDE("rth_altitude", "返航高度"),

    /** 接近限飞区（0=未达到，1=接近） */
    IS_NEAR_AREA_LIMIT("is_near_area_limit", "接近限飞区"),

    /** 接近限高（0=未达到，1=接近） */
    IS_NEAR_HEIGHT_LIMIT("is_near_height_limit", "接近限高"),

    /** 夜航灯状态（0=关闭，1=打开） */
    NIGHT_LIGHTS_STATE("night_lights_state", "夜航灯状态"),

    /** 避障状态（struct：horizon/upside/downside） */
    OBSTACLE_AVOIDANCE("obstacle_avoidance", "避障状态"),

    /** 累计飞行里程（米） */
    TOTAL_FLIGHT_DISTANCE("total_flight_distance", "累计飞行里程"),

    /** 累计飞行架次 */
    TOTAL_FLIGHT_SORTIES("total_flight_sorties", "累计飞行架次"),

    /** 轨迹 ID */
    TRACK_ID("track_id", "轨迹ID"),

    /** 相对起飞点高度（米） */
    ELEVATION("elevation", "相对起飞点高度");

    /** fieldName → 枚举值 的不可变查找表 */
    private static final Map<String, OsdField> BY_FIELD_NAME =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(OsdField::fieldName, Function.identity()));

    private final String fieldName;
    private final String description;

    OsdField(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 返回 DJI 协议中的字段名字符串（snake_case）。
     *
     * @return 字段名，如 {@code "mode_code"}
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * 返回字段的中文描述。
     *
     * @return 描述文本，如 {@code "模式码"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据 DJI 协议字段名查找对应的枚举值。
     *
     * @param fieldName DJI 协议字段名（snake_case），如 {@code "mode_code"}
     * @return 对应的 {@link OsdField} 枚举值
     * @throws IllegalArgumentException 如果字段名不存在于已知 OSD 字段中
     */
    public static OsdField fromFieldName(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("未知的 OSD 字段名: null");
        }
        OsdField field = BY_FIELD_NAME.get(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("未知的 OSD 字段名: " + fieldName);
        }
        return field;
    }
}
