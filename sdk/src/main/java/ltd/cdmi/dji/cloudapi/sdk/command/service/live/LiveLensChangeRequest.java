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

package ltd.cdmi.dji.cloudapi.sdk.command.service.live;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * live_lens_change 指令请求 data。
 *
 * <p>切换直播镜头。
 *
 * <p><b>设备差异</b>：
 * <table>
 * <tr><th>设备</th><th>video_type</th><th>video_id</th><th>文档来源</th></tr>
 * <tr><td>Dock1/2/3</td><td>必填</td><td>不适用</td><td>Dock3 live.html</td></tr>
 * <tr><td>RC Plus（原版）</td><td>必填</td><td>必填</td><td>GitHub others/rc/live.md</td></tr>
 * <tr><td>RC Pro</td><td>必填</td><td>必填</td><td>GitHub rc-pro/live.md</td></tr>
 * </table>
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}
 * （services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/live.html")
@Verified(basis = "DJI Dock3 live.html + GitHub others/rc/live.md + rc-pro/live.md — video_id 为 RC Plus/RC Pro 必填，Dock 不适用")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LiveLensChangeRequest(
    String videoType,
    String videoId
) {
    public LiveLensChangeRequest {
        Objects.requireNonNull(videoType, "videoType 必填，DJI JSON 缺失 video_type 字段");
    }
}
