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
 * DJI 遥控器失控行为（rc_lost_action）。
 *
 * <p>rc_lost_action 表示遥控器信号丢失时飞行器的执行动作，
 * 是飞行器可读写属性（accessMode=rw），出现在飞行器 OSD 中
 * （{@code thing/product/{device_sn}/osd}），可通过
 * {@code thing/product/{gateway_sn}/property/set} 设置。
 *
 * <p><b>注意区分</b>：rc_lost_action 与航线任务的 {@code out_of_control_action}
 * 是<span style="color:var(--muted)">不同的枚举</span>，值映射不同：
 * <ul>
 *   <li>{@code rc_lost_action}（本枚举）：0=悬停, 1=降落, 2=返航</li>
 *   <li>{@code out_of_control_action}（航线任务参数）：0=返航, 1=悬停, 2=降落</li>
 * </ul>
 * 两者不可混用。
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/en/api-reference/dock-to-cloud/mqtt/aircraft/m30-properties.html">
 *       DJI M30 Properties rc_lost_action 枚举定义</a>（英文版，constraint 字段）</li>
 *   <li><a href="https://developer.dji.com/doc/payload-sdk-api-reference/cn/module/flight-controller.html">
 *       DJI PSDK 飞行控制 E_DjiFlightControllerRCLostAction</a>（中文版，值定义一致）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/en/api-reference/dock-to-cloud/mqtt/aircraft/m30-properties.html")
@Verified(basis = "DJI M30 properties.html rc_lost_action 枚举定义（0-2）+ PSDK 飞行控制模块 E_DjiFlightControllerRCLostAction 值定义一致")
public enum RcLostAction {

    /** 悬停 */
    HOVERING(0, "悬停"),

    /** 降落 */
    LANDING(1, "降落"),

    /** 返航 */
    RETURN_TO_HOME(2, "返航");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, RcLostAction> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(RcLostAction::code, Function.identity()));

    private final int code;
    private final String description;

    RcLostAction(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回遥控器失控行为码数值。
     *
     * @return 行为码，如 {@code 2} 表示返航
     */
    public int code() {
        return code;
    }

    /**
     * 返回遥控器失控行为码的中文描述。
     *
     * @return 描述文本，如 {@code "返航"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据遥控器失控行为码数值查找对应的枚举值。
     *
     * @param code 行为码数值，如 {@code 2}
     * @return 对应的 {@link RcLostAction} 枚举值
     * @throws IllegalArgumentException 如果行为码不存在于已知枚举中
     */
    public static RcLostAction fromCode(int code) {
        RcLostAction action = BY_CODE.get(code);
        if (action == null) {
            throw new IllegalArgumentException("未知的 rc_lost_action: " + code);
        }
        return action;
    }
}
