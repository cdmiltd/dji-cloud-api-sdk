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
 * speaker_audio_play_start_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code speaker_audio_play_start_progress} 事件（events 通道）的 data。
 * 用于音频播放进度上报，{@code need_reply=1} 需平台回复，含执行结果与 output（复用 SpeakerOutput）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#SPEAKER_AUDIO_PLAY_START_PROGRESS}
 *
 * <p>字段依据：simulator {@code PsdkSimulator.triggerAudioPlayProgress}（L359-L378）
 * 已对接 hivemind 验证。
 *
 * <p>同包共享 record：{@link SpeakerOutput}（output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator PsdkSimulator.triggerAudioPlayProgress L359-L378 已对接 hivemind 验证")
public record SpeakerAudioPlayStartProgressData(
    Integer result,
    SpeakerOutput output
) {
    public SpeakerAudioPlayStartProgressData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
