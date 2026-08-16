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
 * DJI Dock3 AI 目标识别 DRC 指令 POJO（v1.16 新增，Dock3 专属）。
 *
 * <p>覆盖大疆机场 3 AI 识别功能（人/车/船识别 + 目标跟随），包含 11 个 DRC 下行控制指令
 * 和 1 个 DRC 上行状态推送。
 *
 * <h3>指令清单</h3>
 *
 * <h4>DRC 下行（drc/down，11 个，回复均使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}）</h4>
 * <ul>
 *   <li><b>drc_ai_model_select</b> — AI 模型选择（{@code index}，依据 drc_ai_info_push 推送）</li>
 *   <li><b>drc_ai_identify_set</b> — AI 识别开关设置（{@code on}：0=关闭, 1=开启）</li>
 *   <li><b>drc_ai_identify_score_mode_set</b> — 设置置信度模式
 *       （{@code score_mode}：0=无效, 1=计数, 2=搜救, 3=自定义）</li>
 *   <li><b>drc_ai_identify_score_set</b> — 设置置信度（{@code score}：0-100，仅自定义模式生效）</li>
 *   <li><b>drc_ai_identify_score_reset</b> — 重置置信度（无参数，使用 NoParameterDrcRequest）</li>
 *   <li><b>drc_ai_identify_filter_set</b> — 设置目标过滤列表
 *       （{@code filters}：label index，三方模型 +128 偏移）</li>
 *   <li><b>drc_ai_spotlight_zoom_set</b> — AI 跟随开关设置（{@code on}：0=关闭, 1=开启）</li>
 *   <li><b>drc_ai_spotlight_zoom_track</b> — AI 识别目标跟随（{@code target_index}，点选跟随）</li>
 *   <li><b>drc_ai_spotlight_zoom_select</b> — AI 框选目标跟随
 *       （{@code center_x/center_y/width/height}：归一化坐标 ×10000）</li>
 *   <li><b>drc_ai_spotlight_zoom_confirm</b> — AI 框选目标跟随确认（无参数，使用 NoParameterDrcRequest）</li>
 *   <li><b>drc_ai_spotlight_zoom_stop</b> — 停止目标跟随（无参数，使用 NoParameterDrcRequest）</li>
 * </ul>
 *
 * <h4>DRC 上行（drc/up，1 个）</h4>
 * <ul>
 *   <li><b>drc_ai_info_push</b> — AI 状态推送，含识别/跟随开关、跟随状态、模型列表、
 *       当前选中模型与参数、航线 AI 状态（{@link AiInfoPushData}）</li>
 * </ul>
 *
 * <h3>枚举类（6 个）</h3>
 * <ul>
 *   <li>{@link AiSwitchState} — AI 功能开关（0=关闭, 1=开启）</li>
 *   <li>{@link AiScoreMode} — 置信度模式（0=无效, 1=计数, 2=搜救, 3=自定义）</li>
 *   <li>{@link AiTrackState} — 跟随状态（0=空闲, 1=等待选择, 2=等待确认, 3=跟随中）</li>
 *   <li>{@link AiTrackStateReason} — 跟随状态原因（0-15 正常, 160-168 退出）</li>
 *   <li>{@link AiImageSource} — 图像源（1=广角, 2=变焦, 3=红外, 7=可见光，enum_list 多选）</li>
 *   <li>{@link AiDigitalEffect} — 调色模式（0=白热, 1=黑热, 2=红热，enum_list 多选）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/feature-set/dock-feature-set/ai-target-recognition.html">
 * DJI AI 目标识别功能介绍</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制接口</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.NoParameterDrcRequest
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;
