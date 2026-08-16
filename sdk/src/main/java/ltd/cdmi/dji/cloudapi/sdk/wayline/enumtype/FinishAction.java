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
 * 航线结束动作（finishAction）。
 *
 * <p>finishAction 表示航线任务执行完毕后飞行器的动作，出现在 WPML
 * template.kml 文档的 {@code Template} 节点配置中。
 *
 * @see FlyToWaylineMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 finishAction 枚举定义")
public enum FinishAction implements WpmlEnum {

    /** 返航 */
    GO_HOME("goHome", "返航"),

    /** 无动作 */
    NO_ACTION("noAction", "无动作"),

    /** 原地降落 */
    AUTO_LAND("autoLand", "原地降落"),

    /** 飞向航线起始点 */
    GO_TO_FIRST_WAYPOINT("gotoFirstWaypoint", "飞向航线起始点");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, FinishAction> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(FinishAction::code, Function.identity()));

    private final String code;
    private final String description;

    FinishAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回航线结束动作字符串码。
     *
     * @return 字符串码，如 {@code "goHome"} 表示返航
     */
    public String code() {
        return code;
    }

    /**
     * 返回航线结束动作的中文描述。
     *
     * @return 描述文本，如 {@code "返航"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "goHome"}
     * @return 对应的 {@link FinishAction} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static FinishAction fromCode(String code) {
        FinishAction action = BY_CODE.get(code);
        if (action == null) {
            throw new IllegalArgumentException("未知的 finishAction: " + code);
        }
        return action;
    }
}
