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

package ltd.cdmi.dji.cloudapi.sdk.command.drc;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DRC 通道无参数指令通用 Request。
 *
 * <p>DRC 通道（{@code drc/down}）中，部分指令的请求 data 为空对象 {@code {}}，不携带任何业务字段。
 * 本 record 作为这些指令的通用 Request，避免为每个无参数指令创建空 record 文件。
 *
 * <p>与 services 通道的 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest}
 * 分离，因为 DRC 通道与 services 通道的信封格式不同：
 * <ul>
 *   <li>DRC：{@code {method, data, seq}}</li>
 *   <li>services：{@code {tid, bid, method, timestamp, data}}</li>
 * </ul>
 *
 * <p><b>适用指令清单（3 个，均为飞行安全指令）</b>：
 * <ul>
 *   <li><b>drc_force_landing</b> — 强制降落（无视障碍物直接降落，Dock2 文档确认，
 *       Dock3 文档未找到待真机验证）</li>
 *   <li><b>drone_emergency_stop</b> — 飞行器紧急停止（取消降落/飞行，电机停止）</li>
 *   <li><b>drc_emergency_landing</b> — 紧急降落（受避障影响可能中止，Dock2 文档确认，
 *       Dock3 文档未找到待真机验证）</li>
 * </ul>
 *
 * <p>回复均使用 {@link DrcResultReply}（{@code {result:0}}）。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_FORCE_LANDING
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRONE_EMERGENCY_STOP
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_EMERGENCY_LANDING
 * @see DrcResultReply
 * @see ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/remote-control.html")
@Verified(basis = "DJI Dock2 remote-control 文档确认 drc_force_landing Data=null；simulator DrcCommandHandler 已对接 hivemind 验证 3 个安全指令 data 为空 + 回复 {result:0}")
public record NoParameterDrcRequest() {}
