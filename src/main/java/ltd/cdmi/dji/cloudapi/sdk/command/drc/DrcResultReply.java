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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DRC 通道通用回复 data。
 *
 * <p>DRC 指令回复格式（{@code drc/up}）：
 * <pre>{@code
 * {
 *   "method": "drc_force_landing",
 *   "data": { "result": 0 },
 *   "seq": 1
 * }
 * }</pre>
 *
 * <p>注意：DRC 回复与 {@code services_reply} 不同 —
 * {@code services_reply} 的 data 为 {@code {result, output}}，
 * DRC 回复的 data 直接为 {@code {result}}，无 output 字段。
 *
 * <p><b>适用指令（15 个，全部 @Verified）</b>：
 * <ul>
 *   <li><b>飞行安全（3 个，请求 data 为空，Dock2 remote-control 文档确认）</b>：drc_force_landing /
 *       drone_emergency_stop / drc_emergency_landing（drc_force_landing/drc_emergency_landing
 *       在 Dock3 文档中未找到，待真机验证）</li>
 *   <li><b>相机控制（4 个，Dock3 专属）</b>：drc_camera_night_mode_set /
 *       drc_camera_denoise_level_set / drc_camera_night_vision_enable /
 *       drc_infrared_fill_light_enable</li>
 *   <li><b>探照灯控制（4 个，Dock3 专属）</b>：drc_light_brightness_set /
 *       drc_light_mode_set / drc_light_fine_tuning_set / drc_light_calibration</li>
 *   <li><b>喊话器控制（4 个，Dock3 专属）</b>：drc_speaker_play_mode_set /
 *       drc_speaker_tts_set / drc_speaker_play_volume_set /
 *       drc_speaker_play_stop / drc_speaker_replay（共 5 个，均回复 result）</li>
 * </ul>
 *
 * <p><b>不适用的指令（4 个）</b>：
 * <ul>
 *   <li>stick_control / drone_control — 无回包机制（成功不回复）</li>
 *   <li>heart_beat — 回复 {@code {timestamp}}，使用 HeartBeatReply</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.flight.HeartBeatReply
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler 已对接 hivemind 验证 15 个 DRC 指令回复 {result:0}；DJI 文档 drc/up 确认 result 字段")
public record DrcResultReply(Integer result) {
    public DrcResultReply {
        Objects.requireNonNull(result, "result 必填，DJI DRC 回复缺失 result 字段");
    }
}
