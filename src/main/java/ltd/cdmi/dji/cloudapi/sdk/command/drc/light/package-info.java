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
 * DRC 探照灯控制指令 POJO（Dock3 专属）。
 *
 * <p>四个指令均携带 {@code psdk_index}（PSDK 负载设备索引），回复均使用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}（{@code {result: 0}}）。
 *
 * <h3>指令清单（4 个，Dock3 专属，全部 @Verified）</h3>
 * <ul>
 *   <li><b>drc_light_brightness_set</b> — 探照灯亮度设置（{@code brightness}）</li>
 *   <li><b>drc_light_mode_set</b> — 探照灯模式设置
 *       （{@code mode}: 0=关闭, 1=常亮, 2=爆闪, 3=快速爆闪, 4=交替爆闪）</li>
 *   <li><b>drc_light_fine_tuning_set</b> — 探照灯左右角度微调
 *       （{@code position}: 0=左灯, 1=右灯; {@code value}: 角度值）</li>
 *   <li><b>drc_light_calibration</b> — 探照灯云台校准（无额外字段）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.light;
