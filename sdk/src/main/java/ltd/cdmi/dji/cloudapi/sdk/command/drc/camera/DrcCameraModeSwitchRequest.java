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
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

/**
 * {@code drc_camera_mode_switch} 指令请求 data：切换相机模式。
 *
 * <p>与 services 通道同名指令结构一致（{@code camera_mode} 用共享枚举 {@link CameraMode}）。
 * 回包复用 {@link DrcResultReply}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_MODE_SWITCH
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_mode_switch Data 表 {payload_index, camera_mode(enum_int，共享 services camera_mode 值域)}")
public record DrcCameraModeSwitchRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机模式（共享 CameraMode 枚举：0=拍照,1=录像,2=智能低光,3=全景,4=定时拍） */
        CameraMode cameraMode
) {
    public DrcCameraModeSwitchRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraMode, "cameraMode 必填，DJI JSON 缺失 camera_mode 字段");
    }
}
