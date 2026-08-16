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

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_ai_identify_filter_set} 指令请求 data。
 *
 * <p>设置 AI 识别目标过滤列表。仅识别 filters 列表中包含的目标类型。
 *
 * <p><b>filters 值域规则</b>：
 * <ul>
 *   <li><b>DJI 内置模型</b>：filters 值直接使用 label index
 *       （对应 SEI {@code DJI_AI_OBJECT_TYPE}：1=人, 2=车, 3=船）</li>
 *   <li><b>三方模型</b>：filters 值 = label index + 128 偏移
 *       （如 label index=1 → filters 值=129）</li>
 * </ul>
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code filters} — AI 识别目标过滤列表，元素为 label index（内置模型）或 label index+128（三方模型）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_IDENTIFY_FILTER_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_identify_filter_set Data 表 filters 字段 array<int> + 128 偏移规则说明")
public record AiIdentifyFilterSetRequest(
    List<Integer> filters
) {
    public AiIdentifyFilterSetRequest {
        Objects.requireNonNull(filters, "filters 必填，DJI JSON 缺失 filters 字段");
    }
}
