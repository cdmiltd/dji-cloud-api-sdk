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
 * DJI 网络质量（network_state.quality）。
 *
 * <p>network_state.quality 表示机场的网络信号质量等级，出现在机场 OSD 的 network_state 结构体中。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 network_state.quality 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html network_state.quality 枚举定义（0-5）")
public enum NetworkQuality {

    NO_SIGNAL(0, "无信号"),
    POOR(1, "差"),
    FAIR(2, "较差"),
    MODERATE(3, "一般"),
    GOOD(4, "较好"),
    EXCELLENT(5, "好");

    private static final Map<Integer, NetworkQuality> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(NetworkQuality::code, Function.identity()));

    private final int code;
    private final String description;

    NetworkQuality(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static NetworkQuality fromCode(int code) {
        NetworkQuality q = BY_CODE.get(code);
        if (q == null) {
            throw new IllegalArgumentException("未知的 network_state.quality: " + code);
        }
        return q;
    }
}
