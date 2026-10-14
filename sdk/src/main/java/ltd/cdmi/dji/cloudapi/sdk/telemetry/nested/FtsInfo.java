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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器快传（FTS，File Transfer System）信息（Drone OSD fts_info）。
 *
 * <p>DJI 文档字段（M4D/M4TD 设备属性页）：
 * <ul>
 *   <li>{@code fts_status} — 快传状态，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.FtsStatus}</li>
 *   <li>{@code fts_verification_code} — 验证码（取自飞行器 OSD，8 位数字+大写字母）</li>
 *   <li>{@code fts_valid_duration} — 验证码有效时长</li>
 *   <li>{@code fts_timestamp} — 时间戳（unix 秒）</li>
 *   <li>{@code fts_sn} — 快传设备序列号</li>
 *   <li>{@code fts_firmware_version} — 快传固件版本</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#ftsInfo()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI M4D/M4TD 设备属性页 fts_info struct：字段名与 fts_status 6 值已核实")
public record FtsInfo(
        /** 快传状态（0-4, 99），见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.FtsStatus} */
        Integer ftsStatus,

        /** 验证码（8 位数字+大写字母） */
        String ftsVerificationCode,

        /**
         * 验证码有效时长。
         * <p>{@code @Inferred}：DJI M4D/M4TD 页列出了该字段但未逐项标注精确类型，按“秒”语义推断为 Integer。
         */
        @Inferred(
                reason = "DJI M4D/M4TD 页 fts_info 子字段类型未逐项标注，按语义推断 fts_valid_duration 为秒级整数",
                verifyPoint = "待 M4D/M4TD 页原文逐字段核实 fts_valid_duration 的精确类型"
        )
        Integer ftsValidDuration,

        /**
         * 时间戳（unix 秒）。
         * <p>{@code @Inferred}：DJI M4D/M4TD 页列出了该字段但未逐项标注精确类型，按 unix 秒语义推断为 Integer。
         */
        @Inferred(
                reason = "DJI M4D/M4TD 页 fts_info 子字段类型未逐项标注，按 unix 秒语义推断 fts_timestamp 为 Integer",
                verifyPoint = "待 M4D/M4TD 页原文逐字段核实 fts_timestamp 的精确类型（int/long）"
        )
        Integer ftsTimestamp,

        /** 快传设备序列号 */
        String ftsSn,

        /** 快传固件版本 */
        String ftsFirmwareVersion
) {
}
