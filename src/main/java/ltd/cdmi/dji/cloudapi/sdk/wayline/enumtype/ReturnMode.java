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
 * 激光雷达回波模式（returnMode）。
 *
 * <p>仅用于 M300 RTK / M350 RTK 搭载激光雷达负载（如 Zenmuse L1/L2）的
 * 建图航拍、倾斜摄影、航带飞行模板，定义激光回波采集策略。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html#wpml-payloadparam")
@Verified(basis = "DJI WPML 共用元素文档 payloadParam returnMode 枚举定义")
public enum ReturnMode implements WpmlEnum {

    /** 单回波（取最强回波） */
    SINGLE_RETURN_STRONGEST("singleReturnStrongest", "单回波"),

    /** 双回波 */
    DUAL_RETURN("dualReturn", "双回波"),

    /** 三回波 */
    TRIPLE_RETURN("tripleReturn", "三回波");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ReturnMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ReturnMode::code, Function.identity()));

    private final String code;
    private final String description;

    ReturnMode(String code, String description) {
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
     * @param code 字符串码，如 {@code "dualReturn"}
     * @return 对应的 {@link ReturnMode} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ReturnMode fromCode(String code) {
        ReturnMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 returnMode: " + code);
        }
        return mode;
    }
}
