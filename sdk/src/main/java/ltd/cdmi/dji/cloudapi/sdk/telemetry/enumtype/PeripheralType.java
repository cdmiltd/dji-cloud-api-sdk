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
 * DJI O4 地面站外设类型（peripherals_info[].type）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 peripherals_info[].type 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 peripherals_info[].type 枚举定义（0-7）")
public enum PeripheralType {

    /** 其它 */
    OTHER(0, "其它"),

    /** 相机 */
    CAMERA(1, "相机"),

    /** DAA */
    DAA(2, "DAA"),

    /** 雷达 */
    RADAR(3, "雷达"),

    /** 电源 */
    POWER(4, "电源"),

    /** 第三方机库 */
    THIRD_PARTY_DOCK(5, "第三方机库"),

    /** 边缘计算 */
    EDGE_COMPUTING(6, "边缘计算"),

    /** RID */
    RID(7, "RID");

    private static final Map<Integer, PeripheralType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PeripheralType::code, Function.identity()));

    private final int code;
    private final String description;

    PeripheralType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按类型码反查枚举。
     *
     * @param code type（0-7）
     * @return 对应枚举值
     * @throws IllegalArgumentException 类型码未知
     */
    public static PeripheralType fromCode(int code) {
        PeripheralType t = BY_CODE.get(code);
        if (t == null) {
            throw new IllegalArgumentException("未知的外设类型: " + code);
        }
        return t;
    }
}
