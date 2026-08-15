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
 * 航点偏航角旋转路径模式（waypointHeadingPathMode）。
 *
 * <p>waypointHeadingPathMode 表示飞行器偏航角过渡到下一航点时的旋转方向，出现在 WPML
 * common-element 文档的 {@code Placemark} 节点配置中，仅在
 * {@link WaypointHeadingMode#SMOOTH_TRANSITION} 时生效。
 *
 * @see WaypointHeadingMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 waypointHeadingPathMode 枚举定义")
public enum WaypointHeadingPathMode implements WpmlEnum {

    /** 顺时针旋转 */
    CLOCKWISE("clockwise", "顺时针旋转"),

    /** 逆时针旋转 */
    COUNTER_CLOCKWISE("counterClockwise", "逆时针旋转"),

    /** 沿最短路径旋转 */
    FOLLOW_BAD_ARC("followBadArc", "沿最短路径旋转");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, WaypointHeadingPathMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(WaypointHeadingPathMode::code, Function.identity()));

    private final String code;
    private final String description;

    WaypointHeadingPathMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回航点偏航角旋转路径模式字符串码。
     *
     * @return 字符串码，如 {@code "clockwise"} 表示顺时针旋转
     */
    public String code() {
        return code;
    }

    /**
     * 返回航点偏航角旋转路径模式的中文描述。
     *
     * @return 描述文本，如 {@code "顺时针旋转"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "clockwise"}
     * @return 对应的 {@link WaypointHeadingPathMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static WaypointHeadingPathMode fromCode(String code) {
        WaypointHeadingPathMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 waypointHeadingPathMode: " + code);
        }
        return mode;
    }
}
