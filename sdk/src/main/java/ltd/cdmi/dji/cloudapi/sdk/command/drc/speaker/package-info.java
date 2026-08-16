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
 * DRC 喊话器控制指令 POJO（Dock3 专属）。
 *
 * <p>五个指令均携带 {@code psdk_index}（PSDK 负载设备索引），回复均使用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}（{@code {result: 0}}）。
 *
 * <h3>指令清单（5 个，Dock3 专属，全部 @Verified）</h3>
 * <ul>
 *   <li><b>drc_speaker_play_mode_set</b> — 喊话器播放模式设置
 *       （{@code play_mode}: 0=单次播放, 1=循环播放）</li>
 *   <li><b>drc_speaker_tts_set</b> — 喊话器 TTS 喊话设置
 *       （{@code volume}/{@code type}/{@code language}/{@code speed}）</li>
 *   <li><b>drc_speaker_play_volume_set</b> — 喊话器音量设置
 *       （{@code play_volume}）</li>
 *   <li><b>drc_speaker_play_stop</b> — 喊话器停止播放（无额外字段）</li>
 *   <li><b>drc_speaker_replay</b> — 喊话器重新播放（无额外字段）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.speaker;
