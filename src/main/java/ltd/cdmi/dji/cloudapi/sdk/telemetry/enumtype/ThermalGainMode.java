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
 * DJI 红外相机增益模式（thermal_gain_mode）。
 *
 * <p>thermal_gain_mode 标识红外相机测温的增益模式，影响测温范围和精度：
 * <ul>
 *   <li>低增益：提供更大的测温范围（0°C~500°C）</li>
 *   <li>高增益：拥有更高的测温精度（-20°C~150°C）</li>
 * </ul>
 *
 * <p>对应可设置属性：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.PropertySetMethod#THERMAL_GAIN_MODE}
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html">
 * DJI M3D/M3TD 设备属性 thermal_gain_mode 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html")
@Verified(basis = "DJI M3D properties.html thermal_gain_mode 枚举定义（0-2）")
public enum ThermalGainMode {

    AUTO(0, "自动"),
    LOW(1, "低增益, 测温范围0°C-500°C"),
    HIGH(2, "高增益, 测温范围-20°C-150°C");

    private static final Map<Integer, ThermalGainMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(ThermalGainMode::code, Function.identity()));

    private final int code;
    private final String description;

    ThermalGainMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static ThermalGainMode fromCode(int code) {
        ThermalGainMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 thermal_gain_mode: " + code);
        }
        return m;
    }
}
