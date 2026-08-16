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
 * DJI Dongle 类型（dongle_infos.dongle_type）。
 *
 * <p>dongle_type 标识 4G Dongle 硬件型号，出现在机场 state 的 dongle_infos 数组中。
 * <strong>注意值域不连续</strong>：仅 6 和 10 两个值，<strong>没有 0/1</strong>。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 dongle_infos.dongle_type 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html dongle_infos.dongle_type 枚举定义（6/10）")
public enum DongleType {

    LEGACY(6, "旧 Dongle"),
    ESIM_CAPABLE(10, "支持 eSIM 的新 Dongle");

    private static final Map<Integer, DongleType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(DongleType::code, Function.identity()));

    private final int code;
    private final String description;

    DongleType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static DongleType fromCode(int code) {
        DongleType t = BY_CODE.get(code);
        if (t == null) {
            throw new IllegalArgumentException("未知的 dongle_type: " + code);
        }
        return t;
    }
}
