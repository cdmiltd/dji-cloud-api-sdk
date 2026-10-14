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
 * DJI O4 地面站供电模式（power_mode / electric_supply_mode）。
 *
 * <p>供电模式表示地面站电源来源，OSD 的 {@code power_mode} 与 state 的 {@code electric_supply_mode}
 * 共用本枚举。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 power_mode 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 power_mode / electric_supply_mode 枚举定义（0-2）")
public enum SupplyPowerMode {

    /** 电池 */
    BATTERY(0, "电池"),

    /** PD */
    PD(1, "PD"),

    /** POE */
    POE(2, "POE");

    private static final Map<Integer, SupplyPowerMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(SupplyPowerMode::code, Function.identity()));

    private final int code;
    private final String description;

    SupplyPowerMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按供电模式码反查枚举。
     *
     * @param code power_mode（0-2）
     * @return 对应枚举值
     * @throws IllegalArgumentException 模式码未知
     */
    public static SupplyPowerMode fromCode(int code) {
        SupplyPowerMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的供电模式: " + code);
        }
        return m;
    }
}
