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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint;

/**
 * takeoff_to_point_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code takeoff_to_point_progress} 事件（events 通道）的 data。
 * 用于一键起飞结果通知，{@code need_reply=1} 需平台回复，含 flight_id、track_id、状态、
 * 剩余距离/时间与规划轨迹点。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#TAKEOFF_TO_POINT_PROGRESS}
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.publishTakeoffProgress}（L466-L479）
 * 已对接 hivemind 验证。
 *
 * <p>{@code status} 枚举值：{@code task_ready}/{@code wayline_progress}/
 * {@code wayline_ok}/{@code task_finish}。
 *
 * <p>跨包共享 record：{@link PathPoint}（planned_path_points 数组元素）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.publishTakeoffProgress L466-L479 已对接 hivemind 验证")
public record TakeoffToPointProgressData(
    String status,
    Integer result,
    String flightId,
    String trackId,
    Integer wayPointIndex,
    Double remainingDistance,
    Double remainingTime,
    List<PathPoint> plannedPathPoints
) {
    public TakeoffToPointProgressData {
        Objects.requireNonNull(status, "status 必填，DJI JSON 缺失 status 字段");
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
        Objects.requireNonNull(trackId, "trackId 必填，DJI JSON 缺失 track_id 字段");
    }
}
