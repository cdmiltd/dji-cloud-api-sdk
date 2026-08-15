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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flight_areas_drone_location 事件 data。
 *
 * <p>对应 DJI Cloud API {@code flight_areas_drone_location} 事件（events 通道）的 data。
 * 用于飞行器位置告警推送，{@code need_reply=0} 单向通知，data 含飞行器位置列表。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FLIGHT_AREAS_DRONE_LOCATION}
 *
 * <p>字段依据：simulator {@code FlightAreaSimulator.publishDroneLocationEvent}（L124-L142）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link DroneLocationItem} — 位置项（area_distance/area_id/is_in_area）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator FlightAreaSimulator.publishDroneLocationEvent L124-L142 已对接 hivemind 验证")
public record FlightAreasDroneLocationData(
    List<DroneLocationItem> droneLocations
) {
    public FlightAreasDroneLocationData {
        Objects.requireNonNull(droneLocations, "droneLocations 必填，DJI JSON 缺失 drone_locations 字段");
    }

    /** drone_locations 数组元素，飞行器位置项。 */
    public record DroneLocationItem(
        Double areaDistance,
        String areaId,
        Boolean isInArea
    ) {}
}
