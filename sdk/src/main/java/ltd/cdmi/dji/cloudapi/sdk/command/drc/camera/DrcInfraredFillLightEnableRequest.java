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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drc_infrared_fill_light_enable 指令请求 data。
 *
 * <p>近红外补光使能。仅变焦 7x 以上生效。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code payloadIndex} — 相机枚举，格式 {type-subtype-gimbalindex}</li>
 *   <li>{@code enable} — 近红外补光使能：true=开启, false=关闭</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_INFRARED_FILL_LIGHT_ENABLE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerCameraAdvancedHandlers 已对接 hivemind 验证")
public record DrcInfraredFillLightEnableRequest(
    String payloadIndex,
    Boolean enable
) {
    public DrcInfraredFillLightEnableRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(enable, "enable 必填，DJI JSON 缺失 enable 字段");
    }
}
