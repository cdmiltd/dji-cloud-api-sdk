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
 * drc_camera_night_mode_set 指令请求 data。
 *
 * <p>相机夜景模式设置。自动模式根据环境光线自动切换。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code payloadIndex} — 相机枚举，格式 {type-subtype-gimbalindex}</li>
 *   <li>{@code mode} — 夜景模式：0=关闭, 1=开启, 2=自动</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_NIGHT_MODE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerCameraAdvancedHandlers 已对接 hivemind 验证")
public record DrcCameraNightModeSetRequest(
    String payloadIndex,
    Integer mode
) {
    public DrcCameraNightModeSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(mode, "mode 必填，DJI JSON 缺失 mode 字段");
    }
}
