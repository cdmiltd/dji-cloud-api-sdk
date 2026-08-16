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
 * 拍照模式（shootType）。
 *
 * <p>建图航拍、倾斜摄影、航带飞行模板的拍照触发方式。
 * DJI 建议使用 {@code time} 等时间拍照。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.Mapping2dPlacemark
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 shootType 枚举定义")
public enum ShootType implements WpmlEnum {

    /** 等时间拍照（DJI 推荐） */
    TIME("time", "等时间拍照"),

    /** 等间隔拍照 */
    DISTANCE("distance", "等间隔拍照");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ShootType> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ShootType::code, Function.identity()));

    private final String code;
    private final String description;

    ShootType(String code, String description) {
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
     * @param code 字符串码，如 {@code "time"}
     * @return 对应的 {@link ShootType} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ShootType fromCode(String code) {
        ShootType type = BY_CODE.get(code);
        if (type == null) {
            throw new IllegalArgumentException("未知的 shootType: " + code);
        }
        return type;
    }
}
