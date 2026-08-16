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
 * events 通道喇叭/音频类事件 POJO（PSDK）。
 *
 * <p>本包含 2 个喇叭/音频相关事件的 data record，以及 1 个共享 output record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.speaker.SpeakerOutput SpeakerOutput}
 *       — 共享 output record（psdk_index/status/md5/progress，含嵌套 SpeakerProgress），
 *       被 TTS/音频播放进度事件共享</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.speaker.SpeakerTtsPlayStartProgressData SpeakerTtsPlayStartProgressData}
 *       — speaker_tts_play_start_progress TTS 播放进度（@Verified，复用 SpeakerOutput）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.speaker.SpeakerAudioPlayStartProgressData SpeakerAudioPlayStartProgressData}
 *       — speaker_audio_play_start_progress 音频播放进度（@Verified，复用 SpeakerOutput）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线任务 events（含 PSDK 喇叭事件）</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.speaker;
