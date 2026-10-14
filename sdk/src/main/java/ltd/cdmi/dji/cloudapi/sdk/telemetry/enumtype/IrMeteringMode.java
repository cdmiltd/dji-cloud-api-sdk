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
 * DJI 红外测温模式（ir_metering_mode）。
 *
 * <p>ir_metering_mode 表示红外相机当前测温模式，出现在飞行器 OSD 的 cameras[] 元素中
 * （M4D/M4TD，pushMode=0）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html">
 * DJI M4D/M4TD 设备属性 cameras[].ir_metering_mode 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI M4D/M4TD 设备属性页 cameras[].ir_metering_mode 枚举定义（0-2）")
public enum IrMeteringMode {

    /** 关闭测温 */
    CLOSE(0, "关闭测温"),

    /** 点测温 */
    POINT(1, "点测温"),

    /** 区域测温 */
    AREA(2, "区域测温");

    private static final Map<Integer, IrMeteringMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(IrMeteringMode::code, Function.identity()));

    private final int code;
    private final String description;

    IrMeteringMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按模式码反查枚举。
     *
     * @param code ir_metering_mode 模式码（0-2）
     * @return 对应枚举值
     * @throws IllegalArgumentException 模式码未知
     */
    public static IrMeteringMode fromCode(int code) {
        IrMeteringMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 ir_metering_mode: " + code);
        }
        return m;
    }
}
