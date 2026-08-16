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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission;

/**
 * takeoff_to_point 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code takeoff_to_point} 指令（services 通道）的请求 data。
 * 用于飞向目标点（指令飞行）。异步双阶段确认指令，进度通过
 * {@code takeoff_to_point_progress} 事件上报。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#TAKEOFF_TO_POINT}
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.handleTakeoffToPoint}
 * （L208-L266）已对接 hivemind 验证。
 *
 * <p>{@code security_takeoff_height} 单位为相对起飞点 ALT。
 * {@code rth_mode=0}（智能高度）机场不支持，simulator 拒绝。
 * {@code max_speed} simulator 默认 10，本 POJO 不做默认值兜底。
 *
 * <p>{@code simulate_mission} 字段：simulator 仅解析 3 字段（无 altitude），
 * 但本 POJO 的 {@link SimulateMission} 统一含 4 字段（altitude 缺失为 {@code null}）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator FlightCommandSimulator.handleTakeoffToPoint L208-L266 已对接 hivemind 验证")
public record TakeoffToPointRequest(
    String flightId,
    Integer maxSpeed,
    Double targetLatitude,
    Double targetLongitude,
    Double targetHeight,
    Double securityTakeoffHeight,
    Integer rthAltitude,
    Integer rthMode,
    Integer rcLostAction,
    Integer commanderModeLostAction,
    Integer commanderFlightMode,
    Double commanderFlightHeight,
    Integer flightSafetyAdvanceCheck,
    SimulateMission simulateMission
) {
    public TakeoffToPointRequest {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
        Objects.requireNonNull(targetLatitude, "targetLatitude 必填，DJI JSON 缺失 target_latitude 字段");
        Objects.requireNonNull(targetLongitude, "targetLongitude 必填，DJI JSON 缺失 target_longitude 字段");
        Objects.requireNonNull(targetHeight, "targetHeight 必填，DJI JSON 缺失 target_height 字段");
    }
}
