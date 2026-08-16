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

package ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 动作组执行模式（actionGroupMode）。
 *
 * <p>actionGroupMode 表示动作组内动作的执行方式，出现在 WPML
 * common-element 文档的 {@code ActionGroup} 节点配置中。
 *
 * @see ActionTriggerType
 * @see ActionActuatorFunc
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 actionGroupMode 枚举定义")
public enum ActionGroupMode implements WpmlEnum {

    /** 串行执行 */
    SEQUENCE("sequence", "串行执行"),

    /** 并行执行 */
    @Inferred(reason = "DJI文档仅展示sequence，parallel待确认",
              verifyPoint = "真机确认ActionGroupMode是否支持parallel")
    PARALLEL("parallel", "并行执行");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ActionGroupMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ActionGroupMode::code, Function.identity()));

    private final String code;
    private final String description;

    ActionGroupMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回动作组执行模式字符串码。
     *
     * @return 字符串码，如 {@code "sequence"} 表示串行执行
     */
    public String code() {
        return code;
    }

    /**
     * 返回动作组执行模式的中文描述。
     *
     * @return 描述文本，如 {@code "串行执行"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "sequence"}
     * @return 对应的 {@link ActionGroupMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ActionGroupMode fromCode(String code) {
        ActionGroupMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 actionGroupMode: " + code);
        }
        return mode;
    }
}
