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
 * {@code drc_camera_photo_size_set} 指令请求 data：相机照片分辨率设置。
 *
 * <p>回包复用 {@link DrcResultReply}（{@code {result}}）。
 *
 * <p><b>标 @Inferred</b>：{@code photo_size} 承载类型（数字 vs 枚举串）文档未明确，
 * 按 {@code Integer} 承载，值域参考 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.PhotoResolution}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_PHOTO_SIZE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_photo_size_set Data 表（Dock2/Dock3 统一，值域 {0:R4K,4:R8K}）")
@Inferred(
        reason = "文档未明确 photo_size 的 wire 承载类型",
        verifyPoint = "真机验证 drc_camera_photo_size_set.photo_size 的 wire 类型"
)
public record DrcCameraPhotoSizeSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 照片分辨率：0=4K, 4=8K */
        Integer photoSize
) {
    public DrcCameraPhotoSizeSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(photoSize, "photoSize 必填，DJI JSON 缺失 photo_size 字段");
    }
}
