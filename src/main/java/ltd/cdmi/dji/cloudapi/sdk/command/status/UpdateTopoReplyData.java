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

package ltd.cdmi.dji.cloudapi.sdk.command.status;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * update_topo 状态回复 data。
 *
 * <p>对应 DJI Cloud API {@code update_topo}（status_reply 通道）的回复 data。
 * {@code result=0} 成功；{@code sub_type} 取值：{@code 0=上线}、{@code 1=下线}
 * （DJI 文档规定，待真机验证）。
 *
 * <p>字段依据：simulator {@code DockOnlineService.sendUpdateTopo} 读取 status_reply data.result
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService.sendUpdateTopo 读取 status_reply data.result 已对接 hivemind 验证")
@Inferred(
    reason = "DJI 文档中 status_reply data 含 sub_type（0=上线/1=下线），simulator 仅读取 result 未读取 sub_type",
    verifyPoint = "真机验证 status_reply 是否同时含 result 和 sub_type"
)
public record UpdateTopoReplyData(
    Integer result,
    Integer subType
) {
    public UpdateTopoReplyData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
