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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint;

/**
 * return_home_info 事件 data。
 *
 * <p>对应 DJI Cloud API {@code return_home_info} 事件（events 通道）的 data。
 * 用于返航信息上报，{@code need_reply=0} 单向通知，含规划返航轨迹点、最后航点类型、flight_id，
 * 以及 Dock2/3 蛙跳场景的 home_dock_sn 与 multi_dock_home_info。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#RETURN_HOME_INFO}
 *
 * <p>{@code planned_path_points} 为<b>全量更新</b>语义：每次上报为完整的规划轨迹点集合，
 * 而非增量追加。
 *
 * <p>{@code last_point_type} 为<b>显示判定</b>语义：标识末段航点类型，用于平台侧返航进度显示
 * （见 DJI Dock2/Dock3 航线管理页 {@code return_home_info} 字段说明）。
 *
 * <p>{@code home_dock_sn}/{@code multi_dock_home_info} 仅 Dock2/3 支持蛙跳场景，
 * Dock1 无此字段；字段存在性与可选性已由 DJI Dock2/Dock3 官方文档明确声明，故非推断项。
 *
 * <p>相关枚举助手（字段类型保持 {@code Integer}）：
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.enumtype.LastPointType}（last_point_type）、
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.enumtype.HomePlanStatus}（plan_status）。
 *
 * <p>字段依据：DJI Dock3 航线管理页（wayline.html）Data 表 +
 * simulator {@code WaylineTaskSimulator.publishReturnHomeInfo}（L980-L1036）已对接 hivemind 验证。
 *
 * <p>跨包共享 record：{@link PathPoint}（planned_path_points 数组元素）。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link MultiDockHomeInfo} — 多机场返航信息（sn/plan_status/estimated_battery_consumption/home_distance）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline.html return_home_info Data 表 + simulator WaylineTaskSimulator.publishReturnHomeInfo L980-L1036 已对接 hivemind 验证")
public record ReturnHomeInfoData(
    List<PathPoint> plannedPathPoints,
    Integer lastPointType,
    String flightId,
    String homeDockSn,
    List<MultiDockHomeInfo> multiDockHomeInfo
) {
    public ReturnHomeInfoData {
        Objects.requireNonNull(plannedPathPoints, "plannedPathPoints 必填，DJI JSON 缺失 planned_path_points 字段");
        Objects.requireNonNull(lastPointType, "lastPointType 必填，DJI JSON 缺失 last_point_type 字段");
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }

    /** multi_dock_home_info 数组元素，多机场返航信息（仅 Dock2/3 蛙跳场景）。 */
    public record MultiDockHomeInfo(
        String sn,
        Integer planStatus,
        Integer estimatedBatteryConsumption,
        Double homeDistance
    ) {}
}
