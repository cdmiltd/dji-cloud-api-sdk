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
 * 高程模式（heightMode）。
 *
 * <p>heightMode 表示航点高度的参考基准，出现在 WPML
 * template.kml 文档的 {@code waylineCoordinateSysParam} 配置中。
 *
 * @see CoordinateMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 heightMode 枚举定义")
public enum HeightMode implements WpmlEnum {

    /** EGM96 高程模式：以 EGM96 大地水准面为高度参考 */
    EGM96("EGM96", "EGM96高程模式"),

    /** 相对起飞点高度模式 */
    RELATIVE_TO_START_POINT("relativeToStartPoint", "相对起飞点高度"),

    /** AGL 地面相对高度模式（仅支持司空2平台） */
    ABOVE_GROUND_LEVEL("aboveGroundLevel", "AGL相对地面高度"),

    /** 实时仿地模式（仅用于建图航拍模板，仅支持 M3E/M3T/M3M） */
    REAL_TIME_FOLLOW_SURFACE("realTimeFollowSurface", "实时仿地模式");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, HeightMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(HeightMode::code, Function.identity()));

    private final String code;
    private final String description;

    HeightMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "EGM96"}
     * @return 对应的 {@link HeightMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static HeightMode fromCode(String code) {
        HeightMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 heightMode: " + code);
        }
        return mode;
    }
}
