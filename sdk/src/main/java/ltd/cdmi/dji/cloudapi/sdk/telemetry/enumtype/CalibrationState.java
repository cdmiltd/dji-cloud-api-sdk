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
 * DJI O4 地面站标定状态（position_state.is_calibration）。
 *
 * <p>与 {@link DockPositionState}（机场）共用 is_calibration 语义，本枚举固化其值域。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 position_state.is_calibration 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 position_state.is_calibration 枚举定义（0-1）")
public enum CalibrationState {

    /** 未标定 */
    NOT_CALIBRATED(0, "未标定"),

    /** 已标定 */
    CALIBRATED(1, "已标定");

    private static final Map<Integer, CalibrationState> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(CalibrationState::code, Function.identity()));

    private final int code;
    private final String description;

    CalibrationState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按状态码反查枚举。
     *
     * @param code is_calibration（0-1）
     * @return 对应枚举值
     * @throws IllegalArgumentException 状态码未知
     */
    public static CalibrationState fromCode(int code) {
        CalibrationState s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的标定状态: " + code);
        }
        return s;
    }
}
