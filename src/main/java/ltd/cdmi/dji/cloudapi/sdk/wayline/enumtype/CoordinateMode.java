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
 * 坐标系模式（coordinateMode）。
 *
 * <p>coordinateMode 表示航点使用的地理坐标系，出现在 WPML
 * template.kml 文档的 {@code Template} 节点配置中。
 *
 * @see HeightMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 coordinateMode 枚举定义")
public enum CoordinateMode implements WpmlEnum {

    /** WGS84 坐标系 */
    WGS84("WGS84", "WGS84坐标系");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, CoordinateMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(CoordinateMode::code, Function.identity()));

    private final String code;
    private final String description;

    CoordinateMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回坐标系模式字符串码。
     *
     * @return 字符串码，如 {@code "WGS84"} 表示 WGS84 坐标系
     */
    public String code() {
        return code;
    }

    /**
     * 返回坐标系模式的中文描述。
     *
     * @return 描述文本，如 {@code "WGS84坐标系"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "WGS84"}
     * @return 对应的 {@link CoordinateMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static CoordinateMode fromCode(String code) {
        CoordinateMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 coordinateMode: " + code);
        }
        return mode;
    }
}
