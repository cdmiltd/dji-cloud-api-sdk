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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 动作触发类型（actionTriggerType）。
 *
 * <p>actionTriggerType 表示动作组内动作的触发条件，出现在 WPML
 * common-element 文档的 {@code ActionTrigger} 节点配置中。
 *
 * @see ActionActuatorFunc
 * @see ActionGroupMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 actionTriggerType 枚举定义")
public enum ActionTriggerType implements WpmlEnum {

    /** 到达航点时执行 */
    REACH_POINT("reachPoint", "到达航点时执行"),

    /** 航段触发，均匀转云台 */
    BETWEEN_ADJACENT_POINTS("betweenAdjacentPoints", "航段触发，均匀转云台"),

    /** 等时触发 */
    MULTIPLE_TIMING("multipleTiming", "等时触发"),

    /** 等距触发 */
    MULTIPLE_DISTANCE("multipleDistance", "等距触发");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ActionTriggerType> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ActionTriggerType::code, Function.identity()));

    private final String code;
    private final String description;

    ActionTriggerType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回动作触发类型字符串码。
     *
     * @return 字符串码，如 {@code "reachPoint"} 表示到达航点时执行
     */
    public String code() {
        return code;
    }

    /**
     * 返回动作触发类型的中文描述。
     *
     * @return 描述文本，如 {@code "到达航点时执行"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "reachPoint"}
     * @return 对应的 {@link ActionTriggerType} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ActionTriggerType fromCode(String code) {
        ActionTriggerType type = BY_CODE.get(code);
        if (type == null) {
            throw new IllegalArgumentException("未知的 actionTriggerType: " + code);
        }
        return type;
    }
}
