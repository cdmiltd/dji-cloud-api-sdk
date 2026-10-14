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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * update_topo 状态回复 data。
 *
 * <p>对应 DJI Cloud API {@code update_topo}（status_reply 通道）的回复 data，
 * 文档输出示例仅含 {@code result}（{@code result=0} 成功）。
 *
 * <p>字段依据：DJI 文档 status_reply 输出示例仅含 result；
 * simulator {@code DockOnlineService.sendUpdateTopo} 读取 status_reply data.result 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "DJI 文档 status_reply update_topo 输出示例仅含 result；simulator DockOnlineService.sendUpdateTopo 读取 status_reply data.result 已对接 hivemind 验证")
public record UpdateTopoReplyData(
    Integer result
) {
    public UpdateTopoReplyData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
