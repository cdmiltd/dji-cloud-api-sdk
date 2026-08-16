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
 * {@code drc_ai_model_select} 指令请求 data。
 *
 * <p>AI 模型选择。模型索引值依据 {@code drc_ai_info_push} 推送的 {@code ai_model_list} 中的 {@code index}。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code index} — 模型索引，依据 drc_ai_info_push 推送的 ai_model_list[].index</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_MODEL_SELECT
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_model_select Data 表 index 字段")
public record AiModelSelectRequest(
    Integer index
) {
    public AiModelSelectRequest {
        Objects.requireNonNull(index, "index 必填，DJI JSON 缺失 index 字段");
    }
}
