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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * airport_bind_status 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code airport_bind_status} 指令（requests 通道）的回复 data。
 * {@code result} 为<b>返回码</b>（{@code 0}=成功、非 {@code 0}=错误）；各机场的绑定状态见
 * {@code output.bind_status[]} 元素的 {@code is_device_bind_organization}。
 *
 * <p>字段依据：DJI Cloud API 组织与机场绑定页 requests_reply data——
 * {@code {result, output{bind_status[]{sn, is_device_bind_organization, organization_id,
 * organization_name, device_callsign}}}}（A4/A52）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "DJI Cloud API 组织与机场绑定（airport_bind_status）requests_reply：result(返回码) + output{bind_status[]}（A4/A52）")
public record AirportBindStatusReply(
    Integer result,
    Output output
) {
    public AirportBindStatusReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * output 字段。
     *
     * @param bindStatus 各机场绑定状态数组
     */
    public record Output(
        List<BindStatus> bindStatus
    ) {}

    /**
     * output.bind_status 数组元素，单个机场绑定状态。
     *
     * @param sn                       设备序列号
     * @param isDeviceBindOrganization 是否已绑定组织
     * @param organizationId           组织 ID
     * @param organizationName         组织名称
     * @param deviceCallsign           设备呼号
     */
    public record BindStatus(
        String sn,
        Boolean isDeviceBindOrganization,
        String organizationId,
        String organizationName,
        String deviceCallsign
    ) {}
}
