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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * airport_organization_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code airport_organization_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 表示绑定码与组织 ID 校验通过，绑定到的组织名称见 {@code output.organization_name}。
 *
 * <p>字段依据：DJI Cloud API 组织与机场绑定页 requests_reply data——
 * {@code {result, output{organization_name}}}（A4/A52）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "DJI Cloud API 组织与机场绑定（airport_organization_get）requests_reply：result + output{organization_name}（A4/A52）")
public record AirportOrganizationGetReply(
    Integer result,
    Output output
) {
    public AirportOrganizationGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * output 字段。
     *
     * @param organizationName 组织名称
     */
    public record Output(
        String organizationName
    ) {}
}
