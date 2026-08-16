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
 * {@code drc_ai_identify_score_mode_set} 指令请求 data。
 *
 * <p>设置 AI 识别置信度模式。计数模式和搜救模式由系统自动适配阈值，
 * 自定义模式可由业务侧通过 {@link AiIdentifyScoreSetRequest} 设置具体阈值（0–100）。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code scoreMode} — AI 识别置信度模式：0=无效值, 1=计数模式, 2=搜救模式, 3=用户自定义模式
 *       （{@link AiScoreMode}）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_IDENTIFY_SCORE_MODE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_identify_score_mode_set Data 表 score_mode 字段枚举值 {0:无效值, 1:计数模式, 2:搜救模式, 3:用户自定义模式}")
public record AiIdentifyScoreModeSetRequest(
    AiScoreMode scoreMode
) {
    public AiIdentifyScoreModeSetRequest {
        Objects.requireNonNull(scoreMode, "scoreMode 必填，DJI JSON 缺失 score_mode 字段");
    }
}
