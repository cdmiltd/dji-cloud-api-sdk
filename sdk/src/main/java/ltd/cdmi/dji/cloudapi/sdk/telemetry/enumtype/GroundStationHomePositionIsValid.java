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
 * DJI O4 地面站 home 点有效性（home_position_is_valid）。
 *
 * <p>O4 地面站 home_position_is_valid 为 2 值枚举：{@code 0=无效}、{@code 1=有效}。
 * 与机场侧 {@link Dock2HomePositionIsValid}/{@link Dock3HomePositionIsValid} 是不同设备的枚举，
 * 不可混用（§5-31/§5-38 决策）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 home_position_is_valid 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 home_position_is_valid 枚举定义（0-1）")
public enum GroundStationHomePositionIsValid {

    INVALID(0, "无效"),
    VALID(1, "有效");

    private static final Map<Integer, GroundStationHomePositionIsValid> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(GroundStationHomePositionIsValid::code, Function.identity()));

    private final int code;
    private final String description;

    GroundStationHomePositionIsValid(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按状态码反查枚举。
     *
     * @param code home_position_is_valid（0-1）
     * @return 对应枚举值
     * @throws IllegalArgumentException 状态码未知
     */
    public static GroundStationHomePositionIsValid fromCode(int code) {
        GroundStationHomePositionIsValid v = BY_CODE.get(code);
        if (v == null) {
            throw new IllegalArgumentException("未知的 home_position_is_valid (地面站): " + code);
        }
        return v;
    }
}
