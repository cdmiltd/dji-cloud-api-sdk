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
 * DJI 机场舱盖状态（cover_state）。
 *
 * <p>cover_state 表示机场舱盖的当前开合状态，出现在机场 OSD 中
 * （{@code thing/product/{device_sn}/osd}）。舱盖状态在变化时上报
 * （pushMode=1，Topic: {@code thing/product/{device_sn}/state}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 cover_state 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html cover_state 枚举定义（0-3）")
public enum CoverState {

    /** 关闭 */
    CLOSED(0, "关闭"),

    /** 打开 */
    OPENED(1, "打开"),

    /** 半开 */
    HALF_OPEN(2, "半开"),

    /** 舱盖状态异常 */
    ABNORMAL(3, "舱盖状态异常");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, CoverState> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(CoverState::code, Function.identity()));

    private final int code;
    private final String description;

    CoverState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回舱盖状态码数值。
     *
     * @return 状态码，如 {@code 1} 表示打开
     */
    public int code() {
        return code;
    }

    /**
     * 返回舱盖状态码的中文描述。
     *
     * @return 描述文本，如 {@code "打开"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据舱盖状态码数值查找对应的枚举值。
     *
     * @param code 状态码数值，如 {@code 1}
     * @return 对应的 {@link CoverState} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static CoverState fromCode(int code) {
        CoverState state = BY_CODE.get(code);
        if (state == null) {
            throw new IllegalArgumentException("未知的 cover_state: " + code);
        }
        return state;
    }
}
