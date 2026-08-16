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
 * DJI Cloud API DRC（Dock Remote Control）通道指令 POJO。
 *
 * <p>本包定义 {@code drc/down} 通道（云→设备，设备通过 {@code drc/up} 回复）的
 * 指令请求 data 字段与回复 data 字段的强类型 record。
 *
 * <p><b>DRC 消息格式与 services 不同</b>：
 * <pre>{@code
 * // DRC 请求（drc/down）
 * {
 *   "method": "stick_control",
 *   "data": { "roll": 0, "pitch": 0, "throttle": 0, "yaw": 0 },
 *   "seq": 1
 * }
 * // DRC 回复（drc/up）— data 直接含 result，无 output 包装
 * {
 *   "method": "drc_force_landing",
 *   "data": { "result": 0 },
 *   "seq": 1
 * }
 * }</pre>
 * <p>对比 services_reply 格式：{@code data: {result, output}}，
 * DRC 回复的 data 直接为 {@code {result}} 或具体字段，无 output 层。
 *
 * <h3>子包结构</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.safety safety/} —
 *       飞行安全指令（drc_force_landing / drone_emergency_stop / drc_emergency_landing，
 *       请求 data 为空，回复使用 {@link DrcResultReply}）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.flight flight/} —
 *       飞行控制指令（stick_control / drone_control / heart_beat）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera camera/} —
 *       相机高级控制（Dock3 专属：night_mode/denoise_level/night_vision/infrared_fill_light）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.light light/} —
 *       探照灯控制（Dock3 专属：brightness/mode/fine_tuning/calibration）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.speaker speaker/} —
 *       喊话器控制（Dock3 专属：play_mode/tts/play_volume/play_stop/replay）</li>
 * </ul>
 *
 * <h3>回复行为分类</h3>
 * <ul>
 *   <li><b>15 个指令</b>回复 {@code {result: 0}}，使用 {@link DrcResultReply}</li>
 *   <li><b>2 个指令</b>（stick_control / drone_control）<b>无回包机制</b>，成功不回复</li>
 *   <li><b>1 个指令</b>（heart_beat）回复 {@code {timestamp}}，使用 HeartBeatReply</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod
 * @see DrcResultReply
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc;
