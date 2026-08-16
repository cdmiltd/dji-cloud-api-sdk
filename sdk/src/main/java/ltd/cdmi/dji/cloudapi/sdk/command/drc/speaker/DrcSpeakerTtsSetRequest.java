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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.speaker;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drc_speaker_tts_set 指令请求 data。
 *
 * <p>喊话器 TTS 喊话设置。设置后喊话器开始播放 TTS 语音。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code psdkIndex} — PSDK 负载设备索引</li>
 *   <li>{@code volume} — TTS 播放音量</li>
 *   <li>{@code type} — TTS 播放类型：0=男声, 1=女声</li>
 *   <li>{@code language} — 语言：0=中文, 1=英文</li>
 *   <li>{@code speed} — 语速</li>
 * </ul>
 *
 * <p><b>@Inferred 待确认</b>：TTS 逻辑上需要 {@code text} 文本字段，
 * 但 simulator 未解析此字段，DJI drc/up PSDK 状态上报中也无对应字段。
 * 待 DJI drc/down 文档确认是否含 {@code text} 字段。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_SPEAKER_TTS_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerSpeakerHandlers 已对接 hivemind 验证")
@Inferred(
    reason = "TTS 逻辑上需要 text 文本字段，但 simulator 未解析，DJI drc/up PSDK 状态上报无对应字段",
    verifyPoint = "待 DJI drc/down 文档确认 drc_speaker_tts_set 是否含 text 字段"
)
public record DrcSpeakerTtsSetRequest(
    Integer psdkIndex,
    Integer volume,
    Integer type,
    Integer language,
    Integer speed
) {
    public DrcSpeakerTtsSetRequest {
        Objects.requireNonNull(psdkIndex, "psdkIndex 必填，DJI JSON 缺失 psdk_index 字段");
        Objects.requireNonNull(volume, "volume 必填，DJI JSON 缺失 volume 字段");
        Objects.requireNonNull(type, "type 必填，DJI JSON 缺失 type 字段");
        Objects.requireNonNull(language, "language 必填，DJI JSON 缺失 language 字段");
        Objects.requireNonNull(speed, "speed 必填，DJI JSON 缺失 speed 字段");
    }
}
