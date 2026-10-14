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
 * {@code drc_video_resolution_set} 指令请求 data：视频分辨率设置。
 *
 * <p>回包复用 {@link DrcResultReply}（{@code {result}}）。
 *
 * <p><b>标 @Inferred</b>：文档 Data 表 Type 标 {@code enum_string}（{@code "0":"19201080"}），
 * 但 Example 为数字 {@code 0}（三次复现的矛盾）→ 按 {@code Integer} 承载，值域参考
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.VideoResolution}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_VIDEO_RESOLUTION_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_video_resolution_set Data 表 {payload_index, video_resolution}（值域 0/1）")
@Inferred(
        reason = "文档 Data 表 Type(enum_string) 与 Example(数字) 矛盾",
        verifyPoint = "真机验证 drc_video_resolution_set.video_resolution 的 wire 类型"
)
public record DrcVideoResolutionSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 视频分辨率：0=19201080(FHD), 1=38402160(UHD) */
        Integer videoResolution
) {
    public DrcVideoResolutionSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(videoResolution, "videoResolution 必填，DJI JSON 缺失 video_resolution 字段");
    }
}
