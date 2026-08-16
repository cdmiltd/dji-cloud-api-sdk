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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_ai_spotlight_zoom_track} 指令请求 data。
 *
 * <p>AI 识别目标跟随（点选跟随）。对直播流 SEI 返回的目标列表选择一个 target_index 进行跟随。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code targetIndex} — 目标 index，依据直播流 SEI 推送的目标列表</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_SPOTLIGHT_ZOOM_TRACK
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_spotlight_zoom_track Data 表 target_index 字段")
public record AiSpotlightZoomTrackRequest(
    Integer targetIndex
) {
    public AiSpotlightZoomTrackRequest {
        Objects.requireNonNull(targetIndex, "targetIndex 必填，DJI JSON 缺失 target_index 字段");
    }
}
