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
 * DJI O4 地面站外设接入端口（peripherals_info[].port）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 peripherals_info[].port 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 peripherals_info[].port 枚举定义（0-1）")
public enum PeripheralPort {

    /** LAN */
    LAN(0, "LAN"),

    /** WAN */
    WAN(1, "WAN");

    private static final Map<Integer, PeripheralPort> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PeripheralPort::code, Function.identity()));

    private final int code;
    private final String description;

    PeripheralPort(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按端口码反查枚举。
     *
     * @param code port（0-1）
     * @return 对应枚举值
     * @throws IllegalArgumentException 端口码未知
     */
    public static PeripheralPort fromCode(int code) {
        PeripheralPort p = BY_CODE.get(code);
        if (p == null) {
            throw new IllegalArgumentException("未知的外设端口: " + code);
        }
        return p;
    }
}
