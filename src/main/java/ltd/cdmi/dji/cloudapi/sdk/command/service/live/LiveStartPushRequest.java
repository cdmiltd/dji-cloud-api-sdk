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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * live_start_push 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code live_start_push} 指令（services 通道）的请求 data。
 * 用于开启直播推流，所有字段均为必填。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#LIVE_START_PUSH}
 *
 * <p>字段依据：simulator {@code LiveStreamSimulator.handleStartPush} 已对接 hivemind 验证。
 *
 * <p>{@code url_type} 取值：{@code 1=RTMP}、{@code 4=WebRTC} 等。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator LiveStreamSimulator.handleStartPush 已对接 hivemind 验证")
public record LiveStartPushRequest(
    String videoId,
    String url,
    Integer urlType,
    Integer videoQuality
) {
    public LiveStartPushRequest {
        Objects.requireNonNull(videoId, "videoId 必填，DJI JSON 缺失 video_id 字段");
        Objects.requireNonNull(url, "url 必填，DJI JSON 缺失 url 字段");
        Objects.requireNonNull(urlType, "urlType 必填，DJI JSON 缺失 url_type 字段");
        Objects.requireNonNull(videoQuality, "videoQuality 必填，DJI JSON 缺失 video_quality 字段");
    }
}
