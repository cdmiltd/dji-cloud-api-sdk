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
 * DJI 机场空调状态（air_conditioner_state）。
 *
 * <p>air_conditioner_state 表示机场空调当前的工作模式，出现在机场 OSD 的
 * {@code air_conditioner} 结构体中（{@code thing/product/{device_sn}/osd}）。
 * 空调同一时间仅存在一种工作模式。
 *
 * <p>状态序列：空闲 → 准备模式 → 工作模式 → 退出模式 → 空闲。
 * 例如开启制冷后：空闲(0) → 制冷准备(7) → 制冷(1) → 制冷退出(4) → 空闲(0)。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 air_conditioner_state 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html air_conditioner_state 枚举定义（0-15）")
public enum AirConditionerState {

    /** 空闲模式（无制冷、制热、除湿等） */
    IDLE(0, "空闲模式"),

    /** 制冷模式 */
    COOLING(1, "制冷模式"),

    /** 制热模式 */
    HEATING(2, "制热模式"),

    /** 除湿模式 */
    DEHUMIDIFICATION(3, "除湿模式"),

    /** 制冷退出模式 */
    COOLING_EXIT(4, "制冷退出模式"),

    /** 制热退出模式 */
    HEATING_EXIT(5, "制热退出模式"),

    /** 除湿退出模式 */
    DEHUMIDIFICATION_EXIT(6, "除湿退出模式"),

    /** 制冷准备模式 */
    COOLING_READY(7, "制冷准备模式"),

    /** 制热准备模式 */
    HEATING_READY(8, "制热准备模式"),

    /** 除湿准备模式 */
    DEHUMIDIFICATION_READY(9, "除湿准备模式"),

    /** 风冷准备中 */
    AIR_COOLING_PREPARING(10, "风冷准备中"),

    /** 风冷中 */
    AIR_COOLING_IN_PROGRESS(11, "风冷中"),

    /** 风冷退出中 */
    AIR_COOLING_EXITING(12, "风冷退出中"),

    /** 除雾准备中 */
    DEFOGGER_PREPARING(13, "除雾准备中"),

    /** 除雾中 */
    DEFOGGER_IN_PROGRESS(14, "除雾中"),

    /** 除雾退出中 */
    DEFOGGER_EXITING(15, "除雾退出中");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, AirConditionerState> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(AirConditionerState::code, Function.identity()));

    private final int code;
    private final String description;

    AirConditionerState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回空调状态码数值。
     *
     * @return 状态码，如 {@code 1} 表示制冷模式
     */
    public int code() {
        return code;
    }

    /**
     * 返回空调状态码的中文描述。
     *
     * @return 描述文本，如 {@code "制冷模式"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据空调状态码数值查找对应的枚举值。
     *
     * @param code 状态码数值，如 {@code 1}
     * @return 对应的 {@link AirConditionerState} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static AirConditionerState fromCode(int code) {
        AirConditionerState state = BY_CODE.get(code);
        if (state == null) {
            throw new IllegalArgumentException("未知的 air_conditioner_state: " + code);
        }
        return state;
    }
}
