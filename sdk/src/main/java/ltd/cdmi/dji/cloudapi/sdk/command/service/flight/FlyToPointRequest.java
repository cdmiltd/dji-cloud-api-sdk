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
 * fly_to_point 指令请求 data。
 *
 * <p>飞向目标点（指令飞行）。DJI 协议规定 data 字段为
 * {@code {fly_to_id, max_speed, points: [{latitude, longitude, height}]}}，
 * 其中 {@code points} 是目标点坐标数组（当前 DJI 文档示例均仅用 points[0]，
 * SDK 按协议定义保留 List 结构）。
 *
 * <p>与 {@link TakeoffToPointRequest} 的 {@code target_latitude} 平铺字段不同，
 * 本指令必须用 {@code points} 数组——直接对齐 simulator 解析逻辑
 * （{@code data.path("points").get(0)}），避免反序列化失败。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.handleFlyToPoint 已对接 hivemind 验证 points 数组结构")
public record FlyToPointRequest(
    String flyToId,
    Integer maxSpeed,
    List<FlyToPointTarget> points
) {
    public FlyToPointRequest {
        Objects.requireNonNull(flyToId, "flyToId 必填，DJI JSON 缺失 fly_to_id 字段");
        Objects.requireNonNull(points, "points 必填，DJI JSON 缺失 points 字段");
        if (points.isEmpty()) {
            throw new IllegalArgumentException("points 不能为空数组，DJI 协议规定至少 1 个目标点");
        }
    }
}
