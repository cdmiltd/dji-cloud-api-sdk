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

package ltd.cdmi.dji.cloudapi.sdk.command.event.speaker;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * speaker 事件共享 output record（PSDK 喇叭/音频播放进度）。
 *
 * <p>对应 DJI Cloud API events 通道 {@code speaker_tts_play_start_progress} 与
 * {@code speaker_audio_play_start_progress} 事件的 output 字段。被以下事件共享使用：
 * <ul>
 *   <li>{@link SpeakerTtsPlayStartProgressData#output()}</li>
 *   <li>{@link SpeakerAudioPlayStartProgressData#output()}</li>
 * </ul>
 *
 * <p>字段依据：simulator {@code PsdkSimulator.triggerTtsPlayProgress}/
 * {@code PsdkSimulator.triggerAudioPlayProgress} 已对接 hivemind 验证。
 *
 * <p>嵌套 record（共享，定义在本类体内）：
 * <ul>
 *   <li>{@link SpeakerProgress} — 进度（percent/step_key）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator PsdkSimulator.triggerTtsPlayProgress/triggerAudioPlayProgress 已对接 hivemind 验证")
public record SpeakerOutput(
    Integer psdkIndex,
    String status,
    String md5,
    SpeakerProgress progress
) {
    public SpeakerOutput {
        Objects.requireNonNull(psdkIndex, "psdkIndex 必填，DJI JSON 缺失 psdk_index 字段");
        Objects.requireNonNull(status, "status 必填，DJI JSON 缺失 status 字段");
    }

    /** output.progress 字段，播放进度。 */
    public record SpeakerProgress(
        Integer percent,
        String stepKey
    ) {}
}
