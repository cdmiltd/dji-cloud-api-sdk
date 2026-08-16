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
 * 执行高度模式（executeHeightMode），仅用于 waylines.wpml。
 *
 * <p>表示航点执行高度的参考基准，由 template.kml 的 {@link HeightMode}
 * 映射而来。映射规则：
 * <ul>
 *   <li>{@code relativeToStartPoint} → {@code relativeToStartPoint}</li>
 *   <li>{@code realTimeFollowSurface} → {@code realTimeFollowSurface}</li>
 *   <li>其他（EGM96 / aboveGroundLevel）→ {@code WGS84}</li>
 * </ul>
 *
 * @see HeightMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 executeHeightMode 枚举定义")
public enum ExecuteHeightMode implements WpmlEnum {

    /** WGS84 椭球高模式 */
    WGS84("WGS84", "WGS84椭球高"),

    /** 相对起飞点高度模式 */
    RELATIVE_TO_START_POINT("relativeToStartPoint", "相对起飞点高度"),

    /** 实时仿地模式 */
    REAL_TIME_FOLLOW_SURFACE("realTimeFollowSurface", "实时仿地模式");

    private static final Map<String, ExecuteHeightMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ExecuteHeightMode::code, Function.identity()));

    private final String code;
    private final String description;

    ExecuteHeightMode(String code, String description) {
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
     * 根据 heightMode 字符串码映射为 executeHeightMode。
     *
     * @param heightModeCode template.kml 的 heightMode 码值
     * @return 对应的 executeHeightMode（relativeToStartPoint 和 realTimeFollowSurface 保持原值，其他统一映射为 WGS84）
     */
    public static ExecuteHeightMode fromHeightMode(String heightModeCode) {
        if (heightModeCode == null) {
            return WGS84;
        }
        if (HeightMode.RELATIVE_TO_START_POINT.code().equals(heightModeCode)) {
            return RELATIVE_TO_START_POINT;
        }
        if (HeightMode.REAL_TIME_FOLLOW_SURFACE.code().equals(heightModeCode)) {
            return REAL_TIME_FOLLOW_SURFACE;
        }
        return WGS84;
    }
}
