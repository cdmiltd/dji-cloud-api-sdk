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
 * 航点转弯模式（waypointTurnMode）。
 *
 * <p>waypointTurnMode 表示飞行器到达航点时的转弯方式，出现在 WPML
 * common-element 文档的 {@code Placemark} 节点配置中。
 *
 * @see WaypointHeadingMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 waypointTurnMode 枚举定义")
public enum WaypointTurnMode implements WpmlEnum {

    /** 协调转弯，不过点，提前转弯 */
    COORDINATE_TURN("coordinateTurn", "协调转弯，不过点，提前转弯"),

    /** 直线飞行，飞行器到点停 */
    TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE("toPointAndStopWithDiscontinuityCurvature", "直线飞行，飞行器到点停"),

    /** 曲线飞行，飞行器到点停 */
    TO_POINT_AND_STOP_WITH_CONTINUITY_CURVATURE("toPointAndStopWithContinuityCurvature", "曲线飞行，飞行器到点停"),

    /** 曲线飞行，飞行器过点不停 */
    TO_POINT_AND_PASS_WITH_CONTINUITY_CURVATURE("toPointAndPassWithContinuityCurvature", "曲线飞行，飞行器过点不停");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, WaypointTurnMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(WaypointTurnMode::code, Function.identity()));

    private final String code;
    private final String description;

    WaypointTurnMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回航点转弯模式字符串码。
     *
     * @return 字符串码，如 {@code "coordinateTurn"} 表示协调转弯
     */
    public String code() {
        return code;
    }

    /**
     * 返回航点转弯模式的中文描述。
     *
     * @return 描述文本，如 {@code "协调转弯，不过点，提前转弯"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "coordinateTurn"}
     * @return 对应的 {@link WaypointTurnMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static WaypointTurnMode fromCode(String code) {
        WaypointTurnMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 waypointTurnMode: " + code);
        }
        return mode;
    }
}
