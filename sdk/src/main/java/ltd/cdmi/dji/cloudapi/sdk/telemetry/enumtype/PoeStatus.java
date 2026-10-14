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
 * DJI O4 地面站 POE 状态（poe_status）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 poe_status 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 poe_status 枚举定义（0-4）")
public enum PoeStatus {

    /** 无 PoE */
    NONE(0, "无 PoE"),

    /** 过低 */
    TOO_LOW(1, "过低"),

    /** 25.5W */
    W_25_5(2, "25.5W"),

    /** 51W */
    W_51(3, "51W"),

    /** 71W */
    W_71(4, "71W");

    private static final Map<Integer, PoeStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PoeStatus::code, Function.identity()));

    private final int code;
    private final String description;

    PoeStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按状态码反查枚举。
     *
     * @param code poe_status（0-4）
     * @return 对应枚举值
     * @throws IllegalArgumentException 状态码未知
     */
    public static PoeStatus fromCode(int code) {
        PoeStatus s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 POE 状态: " + code);
        }
        return s;
    }
}
