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
 * DJI O4 地面站模式码（mode_code）。
 *
 * <p>mode_code 表示地面站当前工作模式，出现在 O4 地面站 OSD 中。
 * 与 {@link DockModeCode}（机场模式码）、{@link DroneModeCode}（飞行器模式码）是完全不同的三套枚举，不可混用。
 * 注意值域不连续：0-6 为正常模式，9 为日志上报。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 mode_code 枚举定义</a>
 *
 * @see DockModeCode
 * @see DroneModeCode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.GroundStationOsd#modeCode()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 mode_code 枚举定义（0-6, 9）")
public enum GroundStationModeCode {

    /** 空闲 */
    IDLE(0, "空闲"),

    /** 现场调试 */
    LOCAL_DEBUG(1, "现场调试"),

    /** 远程调试 */
    REMOTE_DEBUG(2, "远程调试"),

    /** 固件升级 */
    FIRMWARE_UPGRADING(3, "固件升级"),

    /** 作业 */
    WORKING(4, "作业"),

    /** 待标定 */
    CALIBRATION_PENDING(5, "待标定"),

    /** 低功耗 */
    LOW_POWER(6, "低功耗"),

    /** 日志上报 */
    LOG_UPLOADING(9, "日志上报");

    private static final Map<Integer, GroundStationModeCode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(GroundStationModeCode::code, Function.identity()));

    private final int code;
    private final String description;

    GroundStationModeCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按模式码反查枚举。
     *
     * @param code mode_code（0-6, 9）
     * @return 对应枚举值
     * @throws IllegalArgumentException 模式码未知
     */
    public static GroundStationModeCode fromCode(int code) {
        GroundStationModeCode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的地面站模式码: " + code);
        }
        return m;
    }
}
