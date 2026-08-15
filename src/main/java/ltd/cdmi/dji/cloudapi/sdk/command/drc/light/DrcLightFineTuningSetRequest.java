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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.light;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drc_light_fine_tuning_set 指令请求 data。
 *
 * <p>探照灯左右角度微调。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code psdkIndex} — PSDK 负载设备索引</li>
 *   <li>{@code position} — 灯位置：0=左灯, 1=右灯</li>
 *   <li>{@code value} — 微调角度值（范围 -3° 到 +3°）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_LIGHT_FINE_TUNING_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerLightHandlers 已对接 hivemind 验证")
public record DrcLightFineTuningSetRequest(
    Integer psdkIndex,
    Integer position,
    Integer value
) {
    public DrcLightFineTuningSetRequest {
        Objects.requireNonNull(psdkIndex, "psdkIndex 必填，DJI JSON 缺失 psdk_index 字段");
        Objects.requireNonNull(position, "position 必填，DJI JSON 缺失 position 字段");
        Objects.requireNonNull(value, "value 必填，DJI JSON 缺失 value 字段");
    }
}
