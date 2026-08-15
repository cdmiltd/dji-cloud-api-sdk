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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 飞行器模式码（mode_code）。
 *
 * <p>mode_code 表示飞行器当前工作模式，出现在飞行器 OSD 中（机场上云和 Pilot 上云均适用）。
 * 与 {@link DockModeCode}（机场模式码）是完全不同的两套枚举，不可混用。
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">机场上云飞行器设备属性</a></li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/others/aircraft/properties.html">Pilot 上云飞行器设备属性</a>（多 18=空中 RTK 收敛模式）</li>
 * </ul>
 *
 * @see DockModeCode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#MODE_CODE
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#modeCode()
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.ControllerOsd#modeCode()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html")
@Verified(basis = "DJI Cloud API 官方文档飞行器 mode_code 枚举定义（0-20，机场上云 M30 文档完整定义）")
public enum DroneModeCode {

    /** 待机 */
    STANDBY(0, "待机"),

    /** 起飞准备 */
    TAKEOFF_PREPARATION(1, "起飞准备"),

    /** 起飞准备完毕 */
    TAKEOFF_READY(2, "起飞准备完毕"),

    /** 手动飞行 */
    MANUAL_FLIGHT(3, "手动飞行"),

    /** 自动起飞 */
    AUTO_TAKEOFF(4, "自动起飞"),

    /** 航线飞行 */
    WAYLINE_FLIGHT(5, "航线飞行"),

    /** 全景拍照 */
    PANORAMA(6, "全景拍照"),

    /** 智能跟随 */
    INTELLIGENT_TRACKING(7, "智能跟随"),

    /** ADS-B 躲避 */
    ADS_B_AVOIDANCE(8, "ADS-B 躲避"),

    /** 自动返航 */
    AUTO_RETURN_HOME(9, "自动返航"),

    /** 自动降落 */
    AUTO_LANDING(10, "自动降落"),

    /** 强制降落 */
    FORCED_LANDING(11, "强制降落"),

    /** 三桨叶降落 */
    THREE_BLADE_LANDING(12, "三桨叶降落"),

    /** 升级中 */
    UPGRADING(13, "升级中"),

    /** 未连接 */
    NOT_CONNECTED(14, "未连接"),

    /** APAS */
    APAS(15, "APAS"),

    /** 虚拟摇杆状态 */
    VIRTUAL_STICK(16, "虚拟摇杆状态"),

    /** 指令飞行 */
    LIVE_FLIGHT_CONTROLS(17, "指令飞行"),

    /** 空中 RTK 收敛模式 */
    AIRBORNE_RTK_FIXING(18, "空中RTK收敛模式"),

    /** 机场选址中（飞行器在空中悬停，为机场选址以及检查 RTK 信号质量） */
    DOCK_ADDRESS_SELECTING(19, "机场选址中"),

    /** POI 环绕 */
    POI_ORBIT(20, "POI环绕");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, DroneModeCode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(DroneModeCode::code, Function.identity()));

    private final int code;
    private final String description;

    DroneModeCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回模式码数值。
     *
     * @return 模式码，如 {@code 0} 表示待机
     */
    public int code() {
        return code;
    }

    /**
     * 返回模式码的中文描述。
     *
     * @return 描述文本，如 {@code "待机"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据模式码数值查找对应的枚举值。
     *
     * @param code 模式码数值，如 {@code 0}
     * @return 对应的 {@link DroneModeCode} 枚举值
     * @throws IllegalArgumentException 如果模式码不存在于已知枚举中
     */
    public static DroneModeCode fromCode(int code) {
        DroneModeCode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的飞行器模式码: " + code);
        }
        return mode;
    }
}
