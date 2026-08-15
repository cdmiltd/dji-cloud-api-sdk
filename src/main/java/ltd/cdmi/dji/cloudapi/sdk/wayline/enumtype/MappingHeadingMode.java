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
 * 建图航拍飞行器偏航角模式（mappingHeadingMode）。
 *
 * <p>仅用于建图航拍（mapping2d）模板，支持机型 M3E/M3T/M3M、M3D/M3TD。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.MappingHeadingParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 mappingHeadingMode 枚举定义")
public enum MappingHeadingMode implements WpmlEnum {

    /** 固定为用户设置的偏航角 */
    FIXED("fixed", "固定偏航角"),

    /** 偏航角跟随航线 */
    FOLLOW_WAYLINE("followWayline", "跟随航线");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, MappingHeadingMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(MappingHeadingMode::code, Function.identity()));

    private final String code;
    private final String description;

    MappingHeadingMode(String code, String description) {
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
     * @param code 字符串码，如 {@code "fixed"}
     * @return 对应的 {@link MappingHeadingMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static MappingHeadingMode fromCode(String code) {
        MappingHeadingMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 mappingHeadingMode: " + code);
        }
        return mode;
    }
}
