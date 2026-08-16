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
 * 云台旋转模式（gimbalRotateMode）。
 *
 * <p>gimbalRotateMode 表示 {@link ActionActuatorFunc#GIMBAL_ROTATE} 动作中云台旋转角度的参考基准，
 * 出现在 WPML common-element 文档的 {@code Action} 节点的 {@code actionActuatorFuncParam} 配置中。
 *
 * @see ActionActuatorFunc
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 gimbalRotateMode 枚举定义")
@Inferred(reason = "DJI文档示例仅展示absoluteAngle，完整枚举值待确认",
          verifyPoint = "真机确认GimbalRotateMode完整枚举值")
public enum GimbalRotateMode implements WpmlEnum {

    /** 绝对角度模式 */
    ABSOLUTE_ANGLE("absoluteAngle", "绝对角度模式");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, GimbalRotateMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(GimbalRotateMode::code, Function.identity()));

    private final String code;
    private final String description;

    GimbalRotateMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回云台旋转模式字符串码。
     *
     * @return 字符串码，如 {@code "absoluteAngle"} 表示绝对角度模式
     */
    public String code() {
        return code;
    }

    /**
     * 返回云台旋转模式的中文描述。
     *
     * @return 描述文本，如 {@code "绝对角度模式"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "absoluteAngle"}
     * @return 对应的 {@link GimbalRotateMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static GimbalRotateMode fromCode(String code) {
        GimbalRotateMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 gimbalRotateMode: " + code);
        }
        return mode;
    }
}
