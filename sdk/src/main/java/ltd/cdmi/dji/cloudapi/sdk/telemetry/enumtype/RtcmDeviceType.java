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
 * DJI O4 地面站 RTCM 设备类型（rtcm_info.rtcm_device_type）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性 rtcm_info.rtcm_device_type 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 rtcm_device_type 枚举定义（0-2）")
public enum RtcmDeviceType {

    /** 遥控器 */
    REMOTE_CONTROLLER(0, "遥控器"),

    /** 机场 */
    DOCK(1, "机场"),

    /** 中继 */
    RELAY(2, "中继");

    private static final Map<Integer, RtcmDeviceType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(RtcmDeviceType::code, Function.identity()));

    private final int code;
    private final String description;

    RtcmDeviceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按设备类型码反查枚举。
     *
     * @param code rtcm_device_type（0-2）
     * @return 对应枚举值
     * @throws IllegalArgumentException 设备类型码未知
     */
    public static RtcmDeviceType fromCode(int code) {
        RtcmDeviceType t = BY_CODE.get(code);
        if (t == null) {
            throw new IllegalArgumentException("未知的 RTCM 设备类型: " + code);
        }
        return t;
    }
}
