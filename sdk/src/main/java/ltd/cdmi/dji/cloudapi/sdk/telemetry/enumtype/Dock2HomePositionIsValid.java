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
 * DJI Dock2 机场 home 点有效性（home_position_is_valid）。
 *
 * <p>Dock2 机场 home_position_is_valid 为 2 值枚举：{@code 0=无效}、{@code 1=有效}，
 * 与 Dock3 的 4 值枚举（见 {@link Dock3HomePositionIsValid}）值域不同，
 * 各设备侧 {@code fromCode} 值域精确、无误判（AGENTS §6.1「避免歧义命名」）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">
 * DJI Dock2 设备属性 home_position_is_valid 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Dock2 properties.html home_position_is_valid 枚举定义（0-1）")
public enum Dock2HomePositionIsValid {

    INVALID(0, "无效"),
    VALID(1, "有效");

    private static final Map<Integer, Dock2HomePositionIsValid> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(Dock2HomePositionIsValid::code, Function.identity()));

    private final int code;
    private final String description;

    Dock2HomePositionIsValid(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static Dock2HomePositionIsValid fromCode(int code) {
        Dock2HomePositionIsValid v = BY_CODE.get(code);
        if (v == null) {
            throw new IllegalArgumentException("未知的 home_position_is_valid (Dock2): " + code);
        }
        return v;
    }
}
