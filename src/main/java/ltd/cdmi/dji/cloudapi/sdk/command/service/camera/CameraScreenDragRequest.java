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

package ltd.cdmi.dji.cloudapi.sdk.command.service.camera;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * camera_screen_drag 指令请求 data。
 *
 * <p>触摸拖动屏幕控制云台。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator PayloadControlHandler 已对接 hivemind 验证")
public record CameraScreenDragRequest(
    String payloadIndex,
    Boolean locked,
    Double pitchSpeed,
    Double yawSpeed
) {
    public CameraScreenDragRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(locked, "locked 必填，DJI JSON 缺失 locked 字段");
        Objects.requireNonNull(pitchSpeed, "pitchSpeed 必填，DJI JSON 缺失 pitch_speed 字段");
        Objects.requireNonNull(yawSpeed, "yawSpeed 必填，DJI JSON 缺失 yaw_speed 字段");
    }
}
