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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器档位（gear）。
 *
 * <p>表示飞行器当前的飞行模式档位，出现在飞行器 OSD 中。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">DJI 飞行器设备属性推送</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#GEAR
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html")
@Verified(basis = "DJI Cloud API 官方文档 gear 枚举定义：{\"0\":\"A\",\"1\":\"P\",\"2\":\"NAV\",\"3\":\"FPV\",\"4\":\"FARM\",\"5\":\"S\",\"6\":\"F\",\"7\":\"M\",\"8\":\"G\",\"9\":\"T\"}")
public enum Gear {

    /** A 档（姿态模式） */
    A(0, "A档（姿态）"),

    /** P 档（定位模式） */
    P(1, "P档（定位）"),

    /** NAV 档（导航模式） */
    NAV(2, "NAV档（导航）"),

    /** FPV 档（第一人称视角模式） */
    FPV(3, "FPV档（第一人称视角）"),

    /** FARM 档（农业模式） */
    FARM(4, "FARM档（农业）"),

    /** S 档（运动模式） */
    S(5, "S档（运动）"),

    /** F 档 */
    F(6, "F档"),

    /** M 档 */
    M(7, "M档"),

    /** G 档 */
    G(8, "G档"),

    /** T 档 */
    T(9, "T档");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, Gear> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(Gear::code, Function.identity()));

    private final int code;
    private final String description;

    Gear(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回档位数值。
     *
     * @return 档位码，如 {@code 1} 表示 P 档
     */
    public int code() {
        return code;
    }

    /**
     * 返回档位的中文描述。
     *
     * @return 描述文本，如 {@code "P档（定位）"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据档位数值查找对应的枚举值。
     *
     * @param code 档位数值，如 {@code 1}
     * @return 对应的 {@link Gear} 枚举值
     * @throws IllegalArgumentException 如果档位不存在于已知枚举中
     */
    public static Gear fromCode(int code) {
        Gear gear = BY_CODE.get(code);
        if (gear == null) {
            throw new IllegalArgumentException("未知的飞行器档位: " + code);
        }
        return gear;
    }
}
