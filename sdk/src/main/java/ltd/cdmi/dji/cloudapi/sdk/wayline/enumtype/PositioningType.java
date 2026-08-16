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
 * 定位类型（positioningType）。
 *
 * <p>positioningType 表示航线任务使用的定位方式，出现在 WPML
 * template.kml 文档的 {@code waylineCoordinateSysParam} 配置中。
 * 该元素仅用于标记位置数据来源，不影响实际航线执行。
 *
 * @see CoordinateMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 positioningType 枚举定义")
public enum PositioningType implements WpmlEnum {

    /** GPS 定位 */
    GPS("GPS", "GPS定位"),

    /** RTK 基站差分定位 */
    RTK_BASE_STATION("RTKBaseStation", "RTK基站定位"),

    /** 千寻网络 RTK 差分定位 */
    QIANXUN("QianXun", "千寻网络RTK定位"),

    /** 自定义网络 RTK 差分定位 */
    CUSTOM("Custom", "自定义网络RTK定位");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, PositioningType> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(PositioningType::code, Function.identity()));

    private final String code;
    private final String description;

    PositioningType(String code, String description) {
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
     * @param code 字符串码，如 {@code "GPS"}
     * @return 对应的 {@link PositioningType} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static PositioningType fromCode(String code) {
        PositioningType type = BY_CODE.get(code);
        if (type == null) {
            throw new IllegalArgumentException("未知的 positioningType: " + code);
        }
        return type;
    }
}
