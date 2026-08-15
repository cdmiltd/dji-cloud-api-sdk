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
 * RTK 定位收敛状态（position_state.is_fixed）。
 *
 * <p>表示 RTK 定位的收敛阶段，出现在机场 OSD 和飞行器 OSD 的 {@code position_state}
 * 子结构中（字段名 {@code is_fixed}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">DJI 机场设备属性推送</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#POSITION_STATE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档 position_state.is_fixed 枚举定义")
public enum PositionState {

    /** 未开始 */
    NOT_STARTED(0, "未开始"),

    /** 收敛中 */
    CONVERGING(1, "收敛中"),

    /** 收敛成功 */
    CONVERGED(2, "收敛成功"),

    /** 收敛失败 */
    CONVERGE_FAILED(3, "收敛失败");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, PositionState> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(PositionState::code, Function.identity()));

    private final int code;
    private final String description;

    PositionState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回定位状态数值。
     *
     * @return 状态码，如 {@code 2} 表示收敛成功
     */
    public int code() {
        return code;
    }

    /**
     * 返回定位状态的中文描述。
     *
     * @return 描述文本，如 {@code "收敛成功"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据定位状态数值查找对应的枚举值。
     *
     * @param code 状态码，如 {@code 2}
     * @return 对应的 {@link PositionState} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static PositionState fromCode(int code) {
        PositionState state = BY_CODE.get(code);
        if (state == null) {
            throw new IllegalArgumentException("未知的 RTK 定位状态: " + code);
        }
        return state;
    }
}
