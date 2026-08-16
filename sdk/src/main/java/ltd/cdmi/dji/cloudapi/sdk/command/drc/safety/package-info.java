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
 * DRC 飞行安全指令 POJO。
 *
 * <p>三个指令的请求 data 均为空 {@code {}}，回复均使用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}（{@code {result: 0}}）。
 *
 * <h3>指令清单（3 个，三 Dock 共有，全部 @Verified）</h3>
 * <ul>
 *   <li><b>drc_force_landing</b> — DRC 强制降落。
 *       调用后无论是否有障碍飞行器都会直接降到地面，
 *       可用 {@code drone_emergency_stop} 取消。
 *       降落完成后只能人工拾取飞行器，慎用！
 * DJI Dock2 remote-control 文档确认。</li>
 *   <li><b>drone_emergency_stop</b> — 飞行器紧急停止。
 *       取消降落/飞行，电机停止。
 * DJI Dock3 DRC 文档确认。</li>
 *   <li><b>drc_emergency_landing</b> — DRC 紧急降落。
 *       受避障影响可能中止（与 drc_force_landing 的区别）。
 * DJI Dock2 remote-control 文档确认。</li>
 * </ul>
 *
 * <h3>DJI 文档核实</h3>
 * <p>DJI Cloud API 文档确认：
 * <pre>{@code
 * // drc_force_landing 请求（Dock2 remote-control）
 * Topic: thing/product/{gateway_sn}/drc/down
 * Method: drc_force_landing
 * Data: null   // 请求 data 为空
 *
 * // drc_force_landing 回复
 * Topic: thing/product/{gateway_sn}/drc/up
 * Method: drc_force_landing
 * Data: { result: int }   // 非 0 代表错误
 *
 * // drone_emergency_stop 请求（Dock3 DRC）
 * Topic: thing/product/{gateway_sn}/drc/down
 * Method: drone_emergency_stop
 * Data: null
 * }</pre>
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/remote-control.html">
 * DJI Dock2 远程控制</a>（drc_force_landing / drc_emergency_landing）</li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html">
 * DJI Dock3 指令飞行</a>（drone_emergency_stop）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_FORCE_LANDING
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRONE_EMERGENCY_STOP
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_EMERGENCY_LANDING
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.safety;
