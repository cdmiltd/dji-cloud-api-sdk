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

/**
 * services 通道 eSIM 管理类请求 POJO。
 *
 * <p>本包含 3 个 eSIM 相关服务的 data record（Dock2/Dock3 共有）：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.esim.EsimActivateRequest EsimActivateRequest}
 *       — esim_activate eSIM 激活（@Inferred 字段待 DJI 文档确认）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.esim.EsimOperatorSwitchRequest EsimOperatorSwitchRequest}
 *       — esim_operator_switch eSIM 运营商切换（@Verified DJI Dock3 cmd 文档确认字段）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.esim.SimSlotSwitchRequest SimSlotSwitchRequest}
 *       — sim_slot_switch eSIM/SIM 卡切换（@Inferred 字段待 DJI 文档确认）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.esim;
