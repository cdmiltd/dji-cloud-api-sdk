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
 * DJI 飞行器模式码触发原因（mode_code_reason）。
 *
 * <p>mode_code_reason 表示飞行器进入当前 {@link DroneModeCode}（mode_code）状态的原因，
 * 与 mode_code 一同出现在飞行器 OSD 中（机场上云和 Pilot 上云均适用）。
 * 例如 mode_code=10（自动降落）时，mode_code_reason 解释触发降落的来源
 * （低电量、低电压、近距返航、大风等）。
 *
 * <p>注意与 {@link DroneModeCode} 的语义区分：
 * <ul>
 *   <li>{@code mode_code} —— 当前是什么状态</li>
 *   <li>{@code mode_code_reason} —— 为什么进入该状态</li>
 * </ul>
 * 两者是独立的两套枚举，不可混用。mode_code_reason 与 mode_code 一同出现在
 * 飞行器 OSD 中（{@link ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#MODE_CODE}）。
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html">机场上云 M3D 设备属性</a>（mode_code_reason 枚举 0-23 完整定义）</li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">机场上云 M30 设备属性</a>（同枚举定义）</li>
 * </ul>
 *
 * @see DroneModeCode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#MODE_CODE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html")
@Verified(basis = "DJI Cloud API 官方文档飞行器 mode_code_reason 枚举定义（0-23，M3D/M30 设备属性文档完整定义）")
public enum ModeCodeReason {

    /** 无意义（mode_code 本身已说明原因，或该状态下无需原因） */
    NO_MEANING(0, "无意义"),

    /** 电池电量不足触发的返航或降落 */
    LOW_BATTERY(1, "电池电量不足（返航、降落）"),

    /** 电池电压不足触发的返航或降落 */
    LOW_VOLTAGE(2, "电池电压不足（返航、降落）"),

    /** 电压严重过低触发的返航或降落 */
    CRITICAL_VOLTAGE(3, "电压严重过低（返航、降落）"),

    /** 遥控器按键触发的起飞、返航或降落 */
    RC_BUTTON_REQUEST(4, "遥控器按键请求（起飞、返航、降落）"),

    /** App 端触发的起飞、返航或降落 */
    APP_REQUEST(5, "App请求（起飞、返航、降落）"),

    /** 遥控信号丢失触发的返航、降落或悬停 */
    RC_SIGNAL_LOSS(6, "遥控信号丢失（返航、降落、悬停）"),

    /** 导航、SDK 等外部设备触发的起飞、返航或降落 */
    EXTERNAL_TRIGGER(7, "导航、SDK等外部设备触发（起飞、返航、降落）"),

    /** 进入机场限飞区触发的降落 */
    ENTER_GEO_ZONE(8, "进入机场限飞区（降落）"),

    /** 距离 Home 点太近触发的降落 */
    RETURN_TOO_CLOSE(9, "距离Home点太近（降落）"),

    /** 距离 Home 点太远触发的降落 */
    RETURN_TOO_FAR(10, "距离Home点太远（降落）"),

    /** 执行航点任务时触发的起飞 */
    WAYPOINT_TAKEOFF(11, "执行航点任务时请求（起飞）"),

    /** 返航阶段到达 Home 点上方后触发的降落 */
    RETURN_ABOVE_HOME(12, "返航阶段到达Home点上方后请求（降落）"),

    /** 距地面 0.7m 时继续下降的降落 */
    SECOND_STAGE_DESCENT(13, "距地面0.7m继续下降（降落）"),

    /** 强制突破限低保护触发的降落 */
    LOW_ALTITUDE_OVERRIDE(14, "强制突破限低保护（降落）"),

    /** 周围有航班经过触发的返航或降落 */
    NEARBY_FLIGHTS(15, "周围有航班经过（返航、降落）"),

    /** 高度控制失败触发的返航或降落 */
    ALTITUDE_CONTROL_FAILURE(16, "高度控制失败（返航、降落）"),

    /** 智能低电量返航后进入的降落 */
    INTELLIGENT_LOW_BATTERY(17, "智能低电量返航后进入（降落）"),

    /** AP 控制飞行模式下的手动飞行 */
    AP_CONTROL(18, "AP控制飞行模式（手动飞行）"),

    /** 硬件异常触发的返航或降落 */
    HARDWARE_ABNORMAL(19, "硬件异常（返航、降落）"),

    /** 防触地保护结束后进入的降落 */
    ANTI_COLLISION_END(20, "防触地保护结束（降落）"),

    /** 返航被取消后进入的悬停 */
    RETURN_CANCELED(21, "返航取消（悬停）"),

    /** 返航时遇到障碍物触发的降落 */
    RETURN_OBSTACLE(22, "返航时遇到障碍物（降落）"),

    /** 机场场景下大风触发的返航 */
    HIGH_WIND(23, "机场场景下大风触发（返航）");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, ModeCodeReason> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ModeCodeReason::code, Function.identity()));

    private final int code;
    private final String description;

    ModeCodeReason(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回原因码数值。
     *
     * @return 原因码，如 {@code 1} 表示电池电量不足
     */
    public int code() {
        return code;
    }

    /**
     * 返回原因码的中文描述。
     *
     * @return 描述文本，如 {@code "电池电量不足（返航、降落）"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据原因码数值查找对应的枚举值。
     *
     * @param code 原因码数值，如 {@code 1}
     * @return 对应的 {@link ModeCodeReason} 枚举值
     * @throws IllegalArgumentException 如果原因码不存在于已知枚举中
     */
    public static ModeCodeReason fromCode(int code) {
        ModeCodeReason reason = BY_CODE.get(code);
        if (reason == null) {
            throw new IllegalArgumentException("未知的 mode_code_reason: " + code);
        }
        return reason;
    }
}
