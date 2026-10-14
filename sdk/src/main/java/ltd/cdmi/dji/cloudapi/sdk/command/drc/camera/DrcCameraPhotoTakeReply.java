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

/**
 * {@code drc_camera_photo_take} 指令回复 data。
 *
 * <p>对应 DJI Cloud API DRC 通道 {@code camera_photo_take}（DRC down，wire {@code drc_camera_photo_take}）
 * 的上行回复 data。services 通道的同名 {@code camera_photo_take} 回复仅 {@code {result}}，
 * 但 DRC 通道回复比 services 多 {@code status}（enum_string），故不能复用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p><b>标 @Inferred</b>：DJI 文档记录了 {@code status} 字段存在，但其出现条件与值域尚未明确。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI Dock3 remote-control 文档：DRC 通道 camera_photo_take 回复 data 含 result + status（enum_string）")
@Inferred(
    reason = "DRC 通道 drc_camera_photo_take 回复比 services 通道多 status（enum_string）字段，DJI 文档未说明 status 出现条件与值域",
    verifyPoint = "真机验证 drc_camera_photo_take DRC 回复 data 是否总是含 status 及其枚举值域"
)
public record DrcCameraPhotoTakeReply(
    Integer result,
    String status
) {
    public DrcCameraPhotoTakeReply {
        Objects.requireNonNull(result, "result 必填，DJI DRC 回复缺失 result 字段");
    }
}
