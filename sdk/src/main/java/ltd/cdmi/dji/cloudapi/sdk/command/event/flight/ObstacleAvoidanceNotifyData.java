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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * obstacle_avoidance_notify 事件 data。
 *
 * <p>对应 DJI Cloud API {@code obstacle_avoidance_notify} 事件（events 通道）的 data。
 * 用于避障记录上报，{@code need_reply=1} 需平台回复，仅 Dock3 支持。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#OBSTACLE_AVOIDANCE_NOTIFY}
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.triggerObstacleAvoidanceNotify}（L485-L504）
 * 已对接 hivemind 验证。{@code obstacles} 数组元素字段依据 simulator REST API 请求体示例
 * （SimulatorController L1059）。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link ObstacleInfo} — 障碍物信息（id/type/timestamp/latitude/longitude/height/wayline_id/waypoint_index）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html">
 * DJI Dock3 DRC events</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.triggerObstacleAvoidanceNotify L485-L504 + SimulatorController L1059 已对接 hivemind 验证")
public record ObstacleAvoidanceNotifyData(
    /** 航线 UUID */
    String waylineUuid,
    /** 飞行任务 ID */
    String flightId,
    /** 障碍物列表 */
    List<ObstacleInfo> obstacles,
    /** 是否最终报告（true=本次为最终避障记录上报） */
    boolean isFinalReport
) {
    /** obstacles 数组元素，单个障碍物信息。 */
    public record ObstacleInfo(
        /** 障碍物 ID */
        String id,
        /** 障碍物类型 */
        int type,
        /** 时间戳（毫秒） */
        long timestamp,
        /** 纬度（WGS84） */
        double latitude,
        /** 经度（WGS84） */
        double longitude,
        /** 高度（相对起飞点，米） */
        double height,
        /** 航线 ID */
        String waylineId,
        /** 航点索引 */
        int waypointIndex
    ) {}
}
