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
 * airport_organization_bind 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code airport_organization_bind} 指令（requests 通道）的回复 data。
 * {@code result=0} 但 {@code output.err_infos} 非空表示设备级失败（部分设备绑定失败）。
 *
 * <p>字段依据：simulator {@code DockOnlineService.checkOrgBindResult} L331-L346 读取
 * data.result + data.output.err_infos 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService.checkOrgBindResult L331-L346 读取 data.result + data.output.err_infos 已对接 hivemind 验证")
public record AirportOrganizationBindReply(
    Integer result,
    Output output
) {
    public AirportOrganizationBindReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** output 字段，含设备级错误信息。 */
    public record Output(
        List<ErrorInfo> errInfos
    ) {}

    /** output.err_infos 数组元素，单个设备绑定错误。 */
    public record ErrorInfo(
        Integer errCode,
        String errSn
    ) {}
}
