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
 * DJI home 点有效性（home_position_is_valid）。
 *
 * <p>home_position_is_valid 表示机场经纬度和航向属性的有效性组合，
 * 出现在机场 OSD 中。当机场未标定时，经纬度坐标无效；
 * 航向不需要标定也可能有效。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 home_position_is_valid 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html home_position_is_valid 枚举定义（0-3）")
public enum HomePositionIsValid {

    BOTH_INVALID(0, "航向和经纬度坐标都无效"),
    BOTH_VALID(1, "航向和经纬度坐标都有效"),
    HEADING_VALID_ONLY(2, "航向有效，经纬度无效"),
    COORDINATE_VALID_ONLY(3, "经纬度有效，航向无效");

    private static final Map<Integer, HomePositionIsValid> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(HomePositionIsValid::code, Function.identity()));

    private final int code;
    private final String description;

    HomePositionIsValid(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static HomePositionIsValid fromCode(int code) {
        HomePositionIsValid v = BY_CODE.get(code);
        if (v == null) {
            throw new IllegalArgumentException("未知的 home_position_is_valid: " + code);
        }
        return v;
    }
}
