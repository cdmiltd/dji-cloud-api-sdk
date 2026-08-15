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
 * 遥控器失联时航线退出策略（exitOnRCLost）。
 *
 * <p>exitOnRCLost 表示航线执行过程中遥控器失联时飞行器的处理方式，出现在 WPML
 * template.kml 文档的 {@code Template} 节点配置中。
 *
 * @see ExecuteRCLostAction
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 exitOnRCLost 枚举定义")
public enum ExitOnRCLost implements WpmlEnum {

    /** 继续执行航线 */
    GO_CONTINUE("goContinue", "继续执行航线"),

    /** 退出航线执行失控动作 */
    EXECUTE_LOST_ACTION("executeLostAction", "退出航线执行失控动作");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ExitOnRCLost> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ExitOnRCLost::code, Function.identity()));

    private final String code;
    private final String description;

    ExitOnRCLost(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回遥控器失联时航线退出策略字符串码。
     *
     * @return 字符串码，如 {@code "goContinue"} 表示继续执行航线
     */
    public String code() {
        return code;
    }

    /**
     * 返回遥控器失联时航线退出策略的中文描述。
     *
     * @return 描述文本，如 {@code "继续执行航线"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "goContinue"}
     * @return 对应的 {@link ExitOnRCLost} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ExitOnRCLost fromCode(String code) {
        ExitOnRCLost action = BY_CODE.get(code);
        if (action == null) {
            throw new IllegalArgumentException("未知的 exitOnRCLost: " + code);
        }
        return action;
    }
}
