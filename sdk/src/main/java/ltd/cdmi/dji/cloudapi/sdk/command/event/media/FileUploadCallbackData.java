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

package ltd.cdmi.dji.cloudapi.sdk.command.event.media;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * file_upload_callback 事件 data。
 *
 * <p>对应 DJI Cloud API {@code file_upload_callback} 事件（events 通道）的 data。
 * 用于文件上传结果回调，逐个文件上传完成后触发，{@code need_reply=1} 需平台回复，
 * data 含单个文件信息（含元数据与拍摄位置）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FILE_UPLOAD_CALLBACK}
 *
 * <p>字段依据：simulator {@code MediaUploadSimulator.publishFileUploadCallback}（L271-L306）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，4 层嵌套保留在类体内以保持高内聚）：
 * <ul>
 *   <li>{@link MediaFile} — 文件信息（cloud_to_cloud_id/ext/metadata/name/object_key/path）</li>
 *   <li>{@link MediaFile.MediaFileExt} — 扩展信息（drone_model_key/flight_id/is_original/payload_model_key）</li>
 *   <li>{@link MediaFile.MediaFileMetadata} — 元数据（absolute_altitude/create_time/gimbal_yaw_degree/
 *       relative_altitude/shoot_position）</li>
 *   <li>{@link MediaFile.MediaFileMetadata.ShootPosition} — 拍摄位置（lat/lng）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/media.html")
@Verified(basis = "simulator MediaUploadSimulator.publishFileUploadCallback L271-L306 已对接 hivemind 验证")
public record FileUploadCallbackData(
    MediaFile file
) {
    public FileUploadCallbackData {
        Objects.requireNonNull(file, "file 必填，DJI JSON 缺失 file 字段");
    }

    /** file_upload_callback 事件 file 字段，单个文件信息。 */
    public record MediaFile(
        String cloudToCloudId,
        MediaFileExt ext,
        MediaFileMetadata metadata,
        String name,
        String objectKey,
        String path
    ) {}

    /** file.ext 字段，文件扩展信息。 */
    public record MediaFileExt(
        String droneModelKey,
        String flightId,
        Boolean isOriginal,
        String payloadModelKey
    ) {}

    /** file.metadata 字段，文件元数据。 */
    public record MediaFileMetadata(
        Double absoluteAltitude,
        String createTime,
        String gimbalYawDegree,
        Double relativeAltitude,
        ShootPosition shootPosition
    ) {}

    /** file.metadata.shoot_position 字段，拍摄位置。 */
    public record ShootPosition(
        Double lat,
        Double lng
    ) {}
}
