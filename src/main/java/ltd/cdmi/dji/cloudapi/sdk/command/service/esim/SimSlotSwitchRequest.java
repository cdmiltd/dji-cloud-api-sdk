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

package ltd.cdmi.dji.cloudapi.sdk.command.service.esim;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * sim_slot_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code sim_slot_switch} 服务（services 通道，云→设备）的 data。
 * 用于 eSIM/SIM 卡切换，Dock2/Dock3 共有，Cmd 类指令（仅 services_reply，无进度事件）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#SIM_SLOT_SWITCH}
 *
 * <p>字段依据：DJI Dock3 cmd 文档 services 请求部分被截断（L559-L564），字段名来自
 * coverage-review.html §1.3 描述（slot_type）。simulator 未解析请求参数。
 *
 * <p>标 @Inferred：字段名/类型待 DJI 文档确认，simulator 无验证。
 * services_reply 仅有 result（无 output，DJI 文档明确）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Inferred(
    reason = "DJI Dock3 cmd 文档 services 请求字段被截断，字段名来自 coverage-review.html §1.3 描述；simulator 未解析请求参数",
    verifyPoint = "真机验证 slot_type 字段名、类型和枚举值"
)
public record SimSlotSwitchRequest(
    /** SIM 卡槽类型（enum_int，待真机验证枚举值） */
    int slotType
) {}
