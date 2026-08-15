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
 * 航点偏航角模式（waypointHeadingMode）。
 *
 * <p>waypointHeadingMode 表示飞行器到达航点时的偏航角控制方式，出现在 WPML
 * common-element 文档的 {@code Placemark} 节点配置中。
 *
 * @see WaypointHeadingPathMode
 * @see WaypointTurnMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 waypointHeadingMode 枚举定义")
public enum WaypointHeadingMode implements WpmlEnum {

    /** 沿航线方向 */
    FOLLOW_WAYLINE("followWayline", "沿航线方向"),

    /** 手动控制 */
    MANUALLY("manually", "手动控制"),

    /** 锁定当前偏航角 */
    FIXED("fixed", "锁定当前偏航角"),

    /** 自定义均匀过渡 */
    SMOOTH_TRANSITION("smoothTransition", "自定义均匀过渡"),

    /** 朝向兴趣点 */
    TOWARD_POI("towardPOI", "朝向兴趣点");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, WaypointHeadingMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(WaypointHeadingMode::code, Function.identity()));

    private final String code;
    private final String description;

    WaypointHeadingMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回航点偏航角模式字符串码。
     *
     * @return 字符串码，如 {@code "followWayline"} 表示沿航线方向
     */
    public String code() {
        return code;
    }

    /**
     * 返回航点偏航角模式的中文描述。
     *
     * @return 描述文本，如 {@code "沿航线方向"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "followWayline"}
     * @return 对应的 {@link WaypointHeadingMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static WaypointHeadingMode fromCode(String code) {
        WaypointHeadingMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 waypointHeadingMode: " + code);
        }
        return mode;
    }
}
