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
 * {@code drc_camera_exposure_set} 指令请求 data：设置相机曝光。
 *
 * <p>与 services 通道同名指令结构一致（{@code exposure_value} 按 {@code String} 承载，与文档 enum_string 一致）。
 * 回包复用 {@link DrcResultReply}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_EXPOSURE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_exposure_set Data 表 {payload_index, camera_type, exposure_value(enum_string 1–31/255)}")
public record DrcCameraExposureSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机类型 */
        String cameraType,
        /** 曝光值（enum_string，"1"…"31" / "255"） */
        String exposureValue
) {
    public DrcCameraExposureSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraType, "cameraType 必填，DJI JSON 缺失 camera_type 字段");
        Objects.requireNonNull(exposureValue, "exposureValue 必填，DJI JSON 缺失 exposure_value 字段");
    }
}
