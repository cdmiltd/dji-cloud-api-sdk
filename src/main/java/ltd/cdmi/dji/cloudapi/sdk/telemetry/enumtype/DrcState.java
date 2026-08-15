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
 * DJI DRC 链路状态（drc_state）。
 *
 * <p>drc_state 表示 DRC（Drone Remote Control）链路的连接状态，
 * 出现在机场 OSD 中（{@code thing/product/{device_sn}/osd}）。
 * DRC 链路用于云端对飞行器的实时控制（fly_to_point、poi_mode 等指令飞行）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 drc_state 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html drc_state 枚举定义（0-2）")
public enum DrcState {

    /** 未连接 */
    DISCONNECTED(0, "未连接"),

    /** 连接中 */
    CONNECTING(1, "连接中"),

    /** 已连接 */
    CONNECTED(2, "已连接");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, DrcState> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(DrcState::code, Function.identity()));

    private final int code;
    private final String description;

    DrcState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回 DRC 链路状态码数值。
     *
     * @return 状态码，如 {@code 2} 表示已连接
     */
    public int code() {
        return code;
    }

    /**
     * 返回 DRC 链路状态码的中文描述。
     *
     * @return 描述文本，如 {@code "已连接"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据 DRC 链路状态码数值查找对应的枚举值。
     *
     * @param code 状态码数值，如 {@code 2}
     * @return 对应的 {@link DrcState} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static DrcState fromCode(int code) {
        DrcState state = BY_CODE.get(code);
        if (state == null) {
            throw new IllegalArgumentException("未知的 drc_state: " + code);
        }
        return state;
    }
}
