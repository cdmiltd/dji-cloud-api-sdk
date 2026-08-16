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
 * live_stop_push 指令请求 data。
 *
 * <p>停止直播推流。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator LiveStreamSimulator 已对接 hivemind 验证")
public record LiveStopPushRequest(
    String videoId
) {
    public LiveStopPushRequest {
        Objects.requireNonNull(videoId, "videoId 必填，DJI JSON 缺失 video_id 字段");
    }
}
