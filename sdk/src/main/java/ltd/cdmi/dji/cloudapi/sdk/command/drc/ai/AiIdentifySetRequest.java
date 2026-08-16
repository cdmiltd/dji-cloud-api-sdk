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
 * {@code drc_ai_identify_set} 指令请求 data。
 *
 * <p>AI 识别开关设置。开启后直播画面将实时显示识别到的目标（人/车/船）。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code on} — AI 识别开关：0=关闭, 1=开启（{@link AiSwitchState}）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_IDENTIFY_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_identify_set Data 表 on 字段枚举值 {0:关闭, 1:开启}")
public record AiIdentifySetRequest(
    AiSwitchState on
) {
    public AiIdentifySetRequest {
        Objects.requireNonNull(on, "on 必填，DJI JSON 缺失 on 字段");
    }
}
