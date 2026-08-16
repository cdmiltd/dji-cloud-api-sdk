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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flight;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * fly_to_point_update 指令请求 data。
 *
 * <p>更新飞向目标点的目标点。DJI 协议规定 data 字段为
 * {@code {max_speed, points: [{latitude, longitude, height}]}}，
 * 与 {@link FlyToPointRequest} 共用 {@code points} 数组结构。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.handleFlyToPointUpdate 已对接 hivemind 验证 points 数组结构")
public record FlyToPointUpdateRequest(
    Integer maxSpeed,
    List<FlyToPointTarget> points
) {
    public FlyToPointUpdateRequest {
        Objects.requireNonNull(points, "points 必填，DJI JSON 缺失 points 字段");
        if (points.isEmpty()) {
            throw new IllegalArgumentException("points 不能为空数组，DJI 协议规定至少 1 个目标点");
        }
    }
}
