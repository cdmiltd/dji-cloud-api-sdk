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
 * DJI 机场模式码（mode_code）。
 *
 * <p>mode_code 表示机场当前工作模式，仅出现在机场 OSD 中。
 * 与 {@link DroneModeCode}（飞行器模式码）是完全不同的两套枚举，不可混用。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">DJI 机场设备属性推送</a>
 *
 * @see DroneModeCode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#MODE_CODE
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#modeCode()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档机场 mode_code 枚举定义（Dock2 属性列表）")
public enum DockModeCode {

    /** 空闲中 */
    IDLE(0, "空闲中"),

    /** 现场调试 */
    LOCAL_DEBUG(1, "现场调试"),

    /** 远程调试 */
    REMOTE_DEBUG(2, "远程调试"),

    /** 固件升级中 */
    FIRMWARE_UPGRADING(3, "固件升级中"),

    /** 作业中 */
    WORKING(4, "作业中"),

    /** 待标定 */
    CALIBRATION_PENDING(5, "待标定");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, DockModeCode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(DockModeCode::code, Function.identity()));

    private final int code;
    private final String description;

    DockModeCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回模式码数值。
     *
     * @return 模式码，如 {@code 0} 表示空闲中
     */
    public int code() {
        return code;
    }

    /**
     * 返回模式码的中文描述。
     *
     * @return 描述文本，如 {@code "空闲中"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据模式码数值查找对应的枚举值。
     *
     * @param code 模式码数值，如 {@code 0}
     * @return 对应的 {@link DockModeCode} 枚举值
     * @throws IllegalArgumentException 如果模式码不存在于已知枚举中
     */
    public static DockModeCode fromCode(int code) {
        DockModeCode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的机场模式码: " + code);
        }
        return mode;
    }
}
