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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI AI 跟随状态原因枚举（{@code ai_spotlight_zoom.state_reason} 字段）。
 *
 * <p>用于 {@code drc_ai_info_push} 推送中 {@code ai_spotlight_zoom.state_reason} 字段，
 * 标识当前跟随状态的原因或退出原因。
 *
 * <p><b>值域跨段</b>：
 * <ul>
 *   <li><b>正常原因（0–15）</b>：跟随过程中触发的状态变化原因，不会退出 AI 跟随</li>
 *   <li><b>退出原因（160–168）</b>：导致 AI 跟随退出的原因，设备会停止跟随</li>
 * </ul>
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue} 与 {@link JsonCreator} 实现 int 值与枚举双向绑定。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 状态上报</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_info_push Data 表 ai_spotlight_zoom.state_reason 枚举值 {0-15 正常原因, 160-168 退出原因}")
public enum AiTrackStateReason {

    // ===== 正常原因（0–15，不会退出 AI 跟随） =====

    NORMAL(0, "正常"),
    TOO_CLOSE(1, "距离目标过近"),
    TOO_FAR(2, "距离目标过远"),
    REACH_ALTITUDE_LOWER_LIMIT(3, "到达限低"),
    REACH_ALTITUDE_UPPER_LIMIT(4, "到达限高"),
    REACH_DISTANCE_LIMIT(5, "到达限远"),
    NEAR_NO_FLY_ZONE(6, "接近限飞区"),
    GIMBAL_LIMIT_REACHED(7, "云台到达限位"),
    YAW_REACH_ORBIT_LIMIT(8, "飞机偏航达到环绕模式最大限制"),
    OBSTACLE_TOO_CLOSE(9, "障碍物距离过近"),
    LOC_SOURCE_SWITCH_GPS_TO_RTK(10, "定位源切换(GPS→RTK)"),
    LOC_SOURCE_SWITCH_RTK_TO_GPS(11, "定位源切换(RTK→GPS)"),
    AVOIDANCE_DISABLED(12, "避障失效"),
    ZOOM_RANGE_LIMIT_REACHED(13, "到达变焦范围限制"),
    TARGET_LOST(14, "跟踪目标丢失"),
    GPS_SIGNAL_WEAK(15, "GPS信号弱"),

    // ===== 退出原因（160–168，会退出 AI 跟随） =====

    EXIT_NORMAL(160, "正常退出"),
    EXIT_PAYLOAD_NOT_SUPPORTED(161, "未适配负载"),
    EXIT_CAMERA_MODE_NOT_SUPPORTED(162, "不支持相机模式"),
    EXIT_INVALID_COMMAND(163, "非法命令"),
    EXIT_LOCATION_FAILED(164, "定位失败"),
    EXIT_AIRCRAFT_NOT_TAKEOFF(165, "飞机未起飞"),
    EXIT_FLIGHT_MODE_ERROR(166, "飞行模式错误"),
    EXIT_MODE_UNAVAILABLE(167, "当前模式不可用(返航/降落/姿态)"),
    EXIT_RC_SIGNAL_LOST(168, "丢失遥控/图传信号");

    private static final Map<Integer, AiTrackStateReason> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiTrackStateReason::code, Function.identity()));

    private final int code;
    private final String description;

    AiTrackStateReason(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiTrackStateReason fromCode(int code) {
        AiTrackStateReason r = BY_CODE.get(code);
        if (r == null) {
            throw new IllegalArgumentException("未知的 AI 跟随状态原因: " + code);
        }
        return r;
    }
}
