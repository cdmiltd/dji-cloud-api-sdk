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
 * highest_priority_upload_flighttask_media 事件 data。
 *
 * <p>对应 DJI Cloud API {@code highest_priority_upload_flighttask_media} 事件（events 通道）的 data。
 * 用于媒体上传优先级上报，航线任务完成后触发，{@code need_reply=1} 需平台回复，data 仅含 flight_id。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#HIGHEST_PRIORITY_UPLOAD_FLIGHTTASK_MEDIA}
 *
 * <p>字段依据：simulator {@code MediaUploadSimulator.publishHighestPriority}（L258-L261）
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/media.html")
@Verified(basis = "simulator MediaUploadSimulator.publishHighestPriority L258-L261 已对接 hivemind 验证")
public record HighestPriorityUploadFlighttaskMediaData(
    String flightId
) {
    public HighestPriorityUploadFlighttaskMediaData {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }
}
