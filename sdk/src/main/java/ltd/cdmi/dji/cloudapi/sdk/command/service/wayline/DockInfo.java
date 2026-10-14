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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AlternateLandPoint;

/**
 * flighttask_execute 指令请求的蛙跳机场信息。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 请求 data 中
 * {@code multi_dock_task.dock_infos} 数组元素。
 *
 * <p><b>dockType 字段</b>：simulator 用 {@code asText()} 解析，类型为 String。
 *
 * <p><b>可复用字段</b>：{@code alternate_land_point} 复用
 * {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AlternateLandPoint}（5 字段）。
 *
 * @see MultiDockTask#dockInfos()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline.html flighttask_execute.multi_dock_task.dock_infos 字段表：sn/dock_type/index/heading/home_position_is_valid/latitude/longitude/height/rtcm_info/alternate_land_point")
public record DockInfo(
    String sn,
    String dockType,
    Integer index,
    /** 航向角（double，度） */
    Double heading,
    /** 返航点是否有效（enum_int {0=无效, 1=有效}） */
    Integer homePositionIsValid,
    Double latitude,
    Double longitude,
    Double height,
    /** RTCM 差分数据源信息 */
    RtcmInfo rtcmInfo,
    /** 备降点信息 */
    AlternateLandPoint alternateLandPoint
) {}
