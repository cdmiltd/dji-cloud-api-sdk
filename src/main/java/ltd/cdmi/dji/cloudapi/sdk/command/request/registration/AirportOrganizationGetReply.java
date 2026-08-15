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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * airport_organization_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code airport_organization_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 表示绑定码与组织 ID 校验通过。
 *
 * <p>字段依据：simulator {@code DockOnlineService.checkOrgGetResult} L308 读取 data.result
 * 已对接 hivemind 验证。
 *
 * <p>注意：simulator 仅读取 {@code data.result}，未解析 {@code output.airports}（机场列表），
 * {@code output} 结构待真机验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService.checkOrgGetResult L308 读取 data.result 已对接 hivemind 验证")
@Inferred(
    reason = "simulator 仅读取 data.result，未解析 output.airports（机场列表），output 结构待真机验证",
    verifyPoint = "真机验证 airport_organization_get 回复 output 字段"
)
public record AirportOrganizationGetReply(
    Integer result
) {
    public AirportOrganizationGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
