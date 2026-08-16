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
 * DJI 降雨量（rainfall）。
 *
 * <p>rainfall 表示机场所在位置的降雨量等级，出现在机场 OSD 中。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 rainfall 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html rainfall 枚举定义（0-3）")
public enum Rainfall {

    NO_RAIN(0, "无雨"),
    LIGHT(1, "小雨"),
    MODERATE(2, "中雨"),
    HEAVY(3, "大雨");

    private static final Map<Integer, Rainfall> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(Rainfall::code, Function.identity()));

    private final int code;
    private final String description;

    Rainfall(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static Rainfall fromCode(int code) {
        Rainfall r = BY_CODE.get(code);
        if (r == null) {
            throw new IllegalArgumentException("未知的 rainfall: " + code);
        }
        return r;
    }
}
