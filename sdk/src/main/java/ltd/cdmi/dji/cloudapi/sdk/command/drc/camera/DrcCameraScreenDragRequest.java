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
import ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply;

/**
 * {@code drc_camera_screen_drag} 指令请求 data：相机画面拖拽（俯仰/偏航方向速度）。
 *
 * <p>回包复用 {@link DrcResultReply}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_SCREEN_DRAG
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_screen_drag Data 表 {payload_index, locked, pitch_speed, yaw_speed}")
public record DrcCameraScreenDragRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 是否锁定机头 */
        Boolean locked,
        /** 俯仰方向速度（-1–1） */
        Double pitchSpeed,
        /** 偏航方向速度（-1–1） */
        Double yawSpeed
) {
    public DrcCameraScreenDragRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(locked, "locked 必填，DJI JSON 缺失 locked 字段");
        Objects.requireNonNull(pitchSpeed, "pitchSpeed 必填，DJI JSON 缺失 pitch_speed 字段");
        Objects.requireNonNull(yawSpeed, "yawSpeed 必填，DJI JSON 缺失 yaw_speed 字段");
    }
}
