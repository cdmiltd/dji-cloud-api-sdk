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
 * {@code drc_camera_photo_format_set} 指令请求 data：相机照片格式设置。
 *
 * <p>与 services 通道同名指令结构一致（{@code photo_format} 用 int 码值，值域见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.PhotoFormat}）。
 * 回包复用 {@link DrcResultReply}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_PHOTO_FORMAT_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_photo_format_set Data 表 {payload_index, photo_format(enum_int)}")
public record DrcCameraPhotoFormatSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 照片格式（int 码值，值域见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.PhotoFormat}：7=RJPEG（含红外元数据），16=DLT664（DJI 私有红外格式）） */
        Integer photoFormat
) {
    public DrcCameraPhotoFormatSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(photoFormat, "photoFormat 必填，DJI JSON 缺失 photo_format 字段");
    }
}
