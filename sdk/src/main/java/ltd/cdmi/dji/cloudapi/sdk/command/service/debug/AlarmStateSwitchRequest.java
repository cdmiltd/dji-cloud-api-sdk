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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * alarm_state_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code alarm_state_switch} 服务（services 通道，云→设备）的 data。
 * 用于切换报警状态，三 Dock 共有，Cmd 类指令（仅 services_reply，无进度事件）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#ALARM_STATE_SWITCH}
 *
 * <p>字段依据：DJI Dock3 cmd 文档 services 请求字段被截断，字段名来自
 * coverage-review.html §1.3 描述（action）。simulator 未解析请求参数。
 *
 * <p>标 @Inferred：字段名/类型待 DJI 文档确认，simulator 无验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Inferred(
    reason = "DJI Dock3 cmd 文档 services 请求字段被截断，字段名来自 coverage-review.html §1.3 描述；simulator 未解析请求参数",
    verifyPoint = "真机验证 action 字段名、类型和枚举值"
)
public record AlarmStateSwitchRequest(
    /** 报警动作（enum_int，0=关闭, 1=开启，待真机验证） */
    int action
) {}
