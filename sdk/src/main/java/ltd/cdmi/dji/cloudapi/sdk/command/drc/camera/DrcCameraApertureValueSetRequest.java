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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply;

/**
 * {@code drc_camera_aperture_value_set} 指令请求 data：相机光圈设置。
 *
 * <p>回包复用 {@link DrcResultReply}。值域参考
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.ApertureValue}（60+ 档）。
 *
 * <p><b>标 @Inferred</b>：文档 Example {@code "aperture_value": 5} 不在枚举值域（0 与 50 之间），
 * 示例与枚举不一致。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_APERTURE_VALUE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_aperture_value_set Data 表 {payload_index, camera_type(wide/zoom), aperture_value(enum_int)}")
@Inferred(
        reason = "文档 Example aperture_value=5 不在枚举值域内，示例与枚举不一致",
        verifyPoint = "待官方反馈 aperture_value 示例与枚举值域矛盾"
)
public record DrcCameraApertureValueSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机类型：wide / zoom */
        String cameraType,
        /** 光圈值（F 值 ×100，0=F_AUTO） */
        Integer apertureValue
) {
    public DrcCameraApertureValueSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraType, "cameraType 必填，DJI JSON 缺失 camera_type 字段");
        Objects.requireNonNull(apertureValue, "apertureValue 必填，DJI JSON 缺失 aperture_value 字段");
    }
}
