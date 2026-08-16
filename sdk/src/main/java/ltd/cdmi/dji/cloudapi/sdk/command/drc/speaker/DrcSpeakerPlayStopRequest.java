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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drc_speaker_play_stop 指令请求 data。
 *
 * <p>喊话器停止播放。除 {@code psdk_index} 外无其他业务字段。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code psdkIndex} — PSDK 负载设备索引</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_SPEAKER_PLAY_STOP
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerSpeakerHandlers 已对接 hivemind 验证")
public record DrcSpeakerPlayStopRequest(
    Integer psdkIndex
) {
    public DrcSpeakerPlayStopRequest {
        Objects.requireNonNull(psdkIndex, "psdkIndex 必填，DJI JSON 缺失 psdk_index 字段");
    }
}
