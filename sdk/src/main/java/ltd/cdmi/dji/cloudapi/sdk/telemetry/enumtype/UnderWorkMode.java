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
 * DJI O4 地面站工作环境模式（under_work_mode）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 under_work_mode 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 under_work_mode 枚举定义（0-1）")
public enum UnderWorkMode {

    /** 非棚下 */
    NORMAL(0, "非棚下"),

    /** 棚下 */
    UNDER_SHED(1, "棚下");

    private static final Map<Integer, UnderWorkMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(UnderWorkMode::code, Function.identity()));

    private final int code;
    private final String description;

    UnderWorkMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按模式码反查枚举。
     *
     * @param code under_work_mode（0-1）
     * @return 对应枚举值
     * @throws IllegalArgumentException 模式码未知
     */
    public static UnderWorkMode fromCode(int code) {
        UnderWorkMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的工作环境模式: " + code);
        }
        return m;
    }
}
