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
 * DJI O4 地面站 RTCM 源类型（rtcm_info.source_type）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 rtcm_info.source_type 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 rtcm_info.source_type 枚举定义（0-3）")
public enum RtcmSourceType {

    /** 未标定 */
    NOT_CALIBRATED(0, "未标定"),

    /** 自收敛 */
    SELF_CONVERGENCE(1, "自收敛"),

    /** 手动 */
    MANUAL(2, "手动"),

    /** 网络 RTK */
    NETWORK_RTK(3, "网络RTK");

    private static final Map<Integer, RtcmSourceType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(RtcmSourceType::code, Function.identity()));

    private final int code;
    private final String description;

    RtcmSourceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按源类型码反查枚举。
     *
     * @param code source_type（0-3）
     * @return 对应枚举值
     * @throws IllegalArgumentException 源类型码未知
     */
    public static RtcmSourceType fromCode(int code) {
        RtcmSourceType t = BY_CODE.get(code);
        if (t == null) {
            throw new IllegalArgumentException("未知的 RTCM 源类型: " + code);
        }
        return t;
    }
}
