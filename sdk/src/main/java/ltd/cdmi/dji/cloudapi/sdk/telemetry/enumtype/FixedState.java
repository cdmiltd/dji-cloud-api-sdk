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
 * DJI O4 地面站收敛状态（position_state.is_fixed）。
 *
 * <p>与 {@link DockPositionState}（机场）共用 is_fixed 语义，本枚举固化其值域。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 position_state.is_fixed 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 position_state.is_fixed 枚举定义（0-3）")
public enum FixedState {

    /** 未开始 */
    NOT_STARTED(0, "未开始"),

    /** 收敛中 */
    CONVERGING(1, "收敛中"),

    /** 收敛成功 */
    CONVERGED(2, "收敛成功"),

    /** 收敛失败 */
    FAILED(3, "收敛失败");

    private static final Map<Integer, FixedState> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(FixedState::code, Function.identity()));

    private final int code;
    private final String description;

    FixedState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按状态码反查枚举。
     *
     * @param code is_fixed（0-3）
     * @return 对应枚举值
     * @throws IllegalArgumentException 状态码未知
     */
    public static FixedState fromCode(int code) {
        FixedState s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的收敛状态: " + code);
        }
        return s;
    }
}
