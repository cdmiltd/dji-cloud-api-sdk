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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 固件升级类型（firmware_upgrade_type）。
 *
 * <p>出现在 {@code ota_create} 服务请求 {@code devices[].firmware_upgrade_type} 中
 * （见 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.firmware.OtaCreateRequest.OtaDevice}）。
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue}（序列化：枚举 → int code）与
 * {@link JsonCreator}（反序列化：int code → 枚举）实现 DJI 协议 int 值与枚举类型的双向绑定，
 * 可直接作为 record 字段类型使用，序列化输出仍为 int，向后兼容。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/firmware.html">
 * DJI O4 地面站 固件升级</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/firmware.html")
@Verified(basis = "DJI O4 地面站 firmware.html firmware_upgrade_type enum_int{2,3,4}（Dock1 为子集 {2,3}，O4 含 PSDK 4）")
public enum FirmwareUpgradeType {

    /** 一致性升级 */
    CONSISTENCY(2, "一致性升级"),

    /** 普通升级 */
    NORMAL(3, "普通升级"),

    /** PSDK 升级 */
    PSDK(4, "PSDK升级");

    private static final Map<Integer, FirmwareUpgradeType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(FirmwareUpgradeType::code, Function.identity()));

    private final int code;
    private final String description;

    FirmwareUpgradeType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FirmwareUpgradeType fromCode(int code) {
        FirmwareUpgradeType type = BY_CODE.get(code);
        if (type == null) {
            throw new IllegalArgumentException("未知的 firmware_upgrade_type: " + code);
        }
        return type;
    }
}
