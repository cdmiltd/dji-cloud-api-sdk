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
 * {@code drc_camera_exposure_mode_set} 指令请求 data：设置相机曝光模式。
 *
 * <p>与 services 通道同名指令结构一致。回包复用 {@link DrcResultReply}。
 * {@code exposure_mode} 值域见
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.ExposureMode}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_EXPOSURE_MODE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_exposure_mode_set Data 表 {payload_index, camera_type, exposure_mode(enum_int 1–4)}")
public record DrcCameraExposureModeSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机类型 */
        String cameraType,
        /** 曝光模式：1=自动, 2=快门优先, 3=光圈优先, 4=手动 */
        Integer exposureMode
) {
    public DrcCameraExposureModeSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraType, "cameraType 必填，DJI JSON 缺失 camera_type 字段");
        Objects.requireNonNull(exposureMode, "exposureMode 必填，DJI JSON 缺失 exposure_mode 字段");
    }
}
