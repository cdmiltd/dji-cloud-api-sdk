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
 * DJI 搜星档位（position_state.quality）。
 *
 * <p>position_state.quality 表示机场 RTK 搜星质量档位，出现在机场 OSD 的 position_state 结构体中。
 * 注意值域不连续：1-5 为正常档位，10 为 RTK fixed（收敛成功）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 position_state.quality 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html position_state.quality 枚举定义（1-5, 10）")
public enum PositionQuality {

    LEVEL_1(1, "1档"),
    LEVEL_2(2, "2档"),
    LEVEL_3(3, "3档"),
    LEVEL_4(4, "4档"),
    LEVEL_5(5, "5档"),
    RTK_FIXED(10, "RTK fixed");

    private static final Map<Integer, PositionQuality> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PositionQuality::code, Function.identity()));

    private final int code;
    private final String description;

    PositionQuality(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static PositionQuality fromCode(int code) {
        PositionQuality q = BY_CODE.get(code);
        if (q == null) {
            throw new IllegalArgumentException("未知的 position_state.quality: " + code);
        }
        return q;
    }
}
