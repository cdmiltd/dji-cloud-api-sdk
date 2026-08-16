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
 * 飞行器充电状态（drone_charge_state.state）。
 *
 * <p>表示飞行器在机场内的充电状态，出现在机场 OSD 的 {@code drone_charge_state} 子结构中。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">DJI 机场设备属性推送</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#DRONE_CHARGE_STATE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档 drone_charge_state.state 枚举定义")
public enum DroneChargeState {

    /** 空闲（未充电） */
    IDLE(0, "空闲"),

    /** 充电中 */
    CHARGING(1, "充电中");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, DroneChargeState> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(DroneChargeState::code, Function.identity()));

    private final int code;
    private final String description;

    DroneChargeState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回充电状态数值。
     *
     * @return 状态码，如 {@code 0} 表示空闲
     */
    public int code() {
        return code;
    }

    /**
     * 返回充电状态的中文描述。
     *
     * @return 描述文本，如 {@code "空闲"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据充电状态数值查找对应的枚举值。
     *
     * @param code 状态码，如 {@code 0}
     * @return 对应的 {@link DroneChargeState} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static DroneChargeState fromCode(int code) {
        DroneChargeState state = BY_CODE.get(code);
        if (state == null) {
            throw new IllegalArgumentException("未知的飞行器充电状态: " + code);
        }
        return state;
    }
}
