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

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API property/set 通道可设置属性枚举。
 *
 * <p>DJI Cloud API 的 property/set 通道（cloud-to-device）用于设置设备属性，
 * 与 services 通道不同：不使用 method 字段，直接在 data 中放置属性名→值的映射。
 * property/set_reply 返回每个属性的设置结果（code: 0=成功）。
 *
 * <p>本枚举列出 DJI Dock3 + M3D/M3TD 文档中 accessMode=rw 的 18 个属性：
 *
 * <table>
 * <caption>可设置属性</caption>
 * <tr><th>枚举</th><th>属性名</th><th>类型</th><th>适用设备</th></tr>
 * <tr><td>AIR_TRANSFER_ENABLE</td><td>air_transfer_enable</td><td>bool</td><td>Dock3</td></tr>
 * <tr><td>SILENT_MODE</td><td>silent_mode</td><td>enum_int(0-1)</td><td>Dock3</td></tr>
 * <tr><td>USER_EXPERIENCE_IMPROVEMENT</td><td>user_experience_improvement</td><td>enum_int(0-2)</td><td>Dock3</td></tr>
 * <tr><td>OBSTACLE_AVOIDANCE</td><td>obstacle_avoidance</td><td>struct</td><td>M3D</td></tr>
 * <tr><td>HEIGHT_LIMIT</td><td>height_limit</td><td>int(20-1500)</td><td>M3D</td></tr>
 * <tr><td>DISTANCE_LIMIT_STATUS</td><td>distance_limit_status</td><td>struct</td><td>M3D</td></tr>
 * <tr><td>RTH_ALTITUDE</td><td>rth_altitude</td><td>int</td><td>M3D</td></tr>
 * <tr><td>REMAINING_POWER_FOR_RETURN_HOME</td><td>remaining_power_for_return_home</td><td>int(0-100)</td><td>M3D/M4D（v1.16.1 新增）</td></tr>
 * <tr><td>NIGHT_LIGHTS_STATE</td><td>night_lights_state</td><td>enum_int(0-1)</td><td>M3D</td></tr>
 * <tr><td>NIGHT_LIGHTS_MODE</td><td>night_lights_mode</td><td>enum_int</td><td>Dock3</td></tr>
 * <tr><td>RC_LOST_ACTION</td><td>rc_lost_action</td><td>enum_int(0-2)</td><td>M3D/M30</td></tr>
 * <tr><td>COMMANDER_FLIGHT_MODE</td><td>commander_flight_mode</td><td>enum_int(0-1)</td><td>M3D</td></tr>
 * <tr><td>CAMERA_WATERMARK_SETTINGS</td><td>camera_watermark_settings</td><td>struct</td><td>M3D</td></tr>
 * <tr><td>THERMAL_CURRENT_PALETTE_STYLE</td><td>thermal_current_palette_style</td><td>enum_int</td><td>M3D thermal</td></tr>
 * <tr><td>THERMAL_GAIN_MODE</td><td>thermal_gain_mode</td><td>enum_int(0-2)</td><td>M3D thermal</td></tr>
 * <tr><td>THERMAL_ISOTHERM_STATE</td><td>thermal_isotherm_state</td><td>enum_int(0-1)</td><td>M3D thermal</td></tr>
 * <tr><td>THERMAL_ISOTHERM_UPPER_LIMIT</td><td>thermal_isotherm_upper_limit</td><td>int(°C)</td><td>M3D thermal</td></tr>
 * <tr><td>THERMAL_ISOTHERM_LOWER_LIMIT</td><td>thermal_isotherm_lower_limit</td><td>int(°C)</td><td>M3D thermal</td></tr>
 * </table>
 *
 * <p>参考：
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html">
 * DJI M3D 设备属性</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 + M3D properties.html accessMode=rw 属性列表（18 个，含 v1.16.1 新增 remaining_power_for_return_home）")
public enum PropertySetMethod {

    // --- Dock3 机场属性 ---
    /** 空中回传开关（bool） */
    AIR_TRANSFER_ENABLE("air_transfer_enable"),
    /** 机场静音模式（0=非静音, 1=静音） */
    SILENT_MODE("silent_mode"),
    /** 用户体验改善计划（0=初始, 1=拒绝, 2=同意） */
    USER_EXPERIENCE_IMPROVEMENT("user_experience_improvement"),

    // --- M3D 飞行器属性 ---
    /** 飞行器避障状态（struct） */
    OBSTACLE_AVOIDANCE("obstacle_avoidance"),
    /** 飞行器限高（20-1500 米） */
    HEIGHT_LIMIT("height_limit"),
    /** 飞行器限远（struct） */
    DISTANCE_LIMIT_STATUS("distance_limit_status"),
    /** 返航高度（米，相对起飞点） */
    RTH_ALTITUDE("rth_altitude"),
    /** 返航预留电量（百分比 0-100，v1.16.1 新增，M3D/M4D 飞行器属性） */
    REMAINING_POWER_FOR_RETURN_HOME("remaining_power_for_return_home"),
    /** 飞行器夜航灯状态（0=关闭, 1=打开） */
    NIGHT_LIGHTS_STATE("night_lights_state"),
    /** 机场夜航灯模式 */
    NIGHT_LIGHTS_MODE("night_lights_mode"),
    /** 遥控器失控动作（0=悬停, 1=降落, 2=返航） */
    RC_LOST_ACTION("rc_lost_action"),

    // --- M3D 指点飞行与相机水印 ---
    /** 指点飞行模式设置值（0=智能高度飞行, 1=设定高度飞行） */
    COMMANDER_FLIGHT_MODE("commander_flight_mode"),
    /** 相机水印设置（struct，用户对相机拍摄的照片和录像文件进行水印配置） */
    CAMERA_WATERMARK_SETTINGS("camera_watermark_settings"),

    // --- M3D 红外相机子属性（嵌套在 cameras 结构中） ---
    /** 调色盘样式 */
    THERMAL_CURRENT_PALETTE_STYLE("thermal_current_palette_style"),
    /** 增益模式（0=自动, 1=低增益, 2=高增益） */
    THERMAL_GAIN_MODE("thermal_gain_mode"),
    /** 等温线开关（0=关闭, 1=开启） */
    THERMAL_ISOTHERM_STATE("thermal_isotherm_state"),
    /** 测温区间上限（°C） */
    THERMAL_ISOTHERM_UPPER_LIMIT("thermal_isotherm_upper_limit"),
    /** 测温区间下限（°C） */
    THERMAL_ISOTHERM_LOWER_LIMIT("thermal_isotherm_lower_limit");

    private final String propertyName;

    /** propertyName → 枚举 查找表 */
    private static final Map<String, PropertySetMethod> BY_NAME =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(
                    PropertySetMethod::propertyName, Function.identity()));

    PropertySetMethod(String propertyName) {
        this.propertyName = propertyName;
    }

    public String propertyName() { return propertyName; }

    /**
     * 按 property 名称反查枚举。
     *
     * @param propertyName DJI 协议属性名（如 "height_limit"）
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException propertyName 不匹配任何已知属性
     */
    public static PropertySetMethod fromPropertyName(String propertyName) {
        PropertySetMethod method = BY_NAME.get(propertyName);
        if (method == null) {
            throw new IllegalArgumentException("未知的属性名: " + propertyName);
        }
        return method;
    }
}
