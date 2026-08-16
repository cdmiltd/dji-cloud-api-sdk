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
 * DRC 相机高级控制指令 POJO（Dock3 专属）。
 *
 * <p>四个指令均携带 {@code payload_index}（相机枚举），回复均使用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}（{@code {result: 0}}）。
 *
 * <h3>指令清单（4 个，Dock3 专属，全部 @Verified）</h3>
 * <ul>
 *   <li><b>drc_camera_night_mode_set</b> — 夜景模式设置
 *       （{@code mode}: 0=关闭, 1=开启, 2=自动）</li>
 *   <li><b>drc_camera_denoise_level_set</b> — 降噪等级设置
 *       （{@code level}: 0=关闭, 1=标准, 2=增强15fps, 3=超强5fps）</li>
 *   <li><b>drc_camera_night_vision_enable</b> — 黑白夜视使能
 *       （{@code enable}: true/false，仅变焦7x以上生效）</li>
 *   <li><b>drc_infrared_fill_light_enable</b> — 近红外补光使能
 *       （{@code enable}: true/false，仅变焦7x以上生效）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;
