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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器控制权状态（{@code drone_authority_info} struct，Dock1 OSD 专属，pushMode=0）。
 *
 * <p>结构：{@code {control_source, locked, payloads[]{control_source, payload_index, sn}}}。
 * 挂载于 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#droneAuthorityInfo()}。
 *
 * <p><b>{@code control_source}</b>：{@code enum_string}，{@code ""}=未获取、{@code A}=A 控、{@code B}=B 控。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/properties.html">
 * DJI Dock1 设备属性推送</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/properties.html")
@Verified(basis = "DJI Dock1 properties.html drone_authority_info struct（pushMode=0）：control_source(enum_string \"\"/A/B)、locked(bool)、payloads[]{control_source,payload_index,sn}")
public record DroneAuthorityInfo(
    /** 控制源（enum_string："" 未获取、A=A 控、B=B 控） */
    String controlSource,
    /** 是否锁定 */
    Boolean locked,
    /** 各负载控制权（子项 pushMode=1，随负载变化推送） */
    List<PayloadAuthority> payloads
) {
    /**
     * drone_authority_info.payloads[] 单个负载控制权。
     *
     * @param controlSource 控制源（enum_string："" 未获取、A=A 控、B=B 控）
     * @param payloadIndex  负载索引
     * @param sn            负载序列号
     */
    public record PayloadAuthority(
        String controlSource,
        String payloadIndex,
        String sn
    ) {}
}
