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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器 OSD 遥测数据结构。
 *
 * <p>包含飞行器所有 OSD 字段（pushMode=0，周期推送），字段名与
 * {@link OsdField} 中飞行器部分一一对应，并包含与机场共用的字段
 * （{@code modeCode}、{@code latitude}、{@code longitude}、{@code height}、
 * {@code windSpeed}、{@code activationTime}、{@code maintainStatus}、
 * {@code positionState}、{@code storage}）。
 *
 * <p>使用包装类型（Integer/Double/Long/String/Object 等）允许 {@code null}，
 * 因为不同机型上报的字段集可能不同（如 M400 Pilot 不上报限远/返航高度字段）。
 *
 * <p>嵌套结构（如 {@code battery}、{@code positionState}、{@code obstacleAvoidance} 等）
 * 使用 {@link Object} 表示，具体结构参见 DJI 文档属性列表。
 *
 * <p>注意：{@code height} 为绝对高度（椭球面），{@code elevation} 为相对起飞点高度。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">DJI 飞行器设备属性推送</a>
 *
 * @see OsdField
 * @see DockOsd
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.Gear
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html")
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性推送属性列表（M30/M3D/M4D）")
public record DroneOsd(

        /** 模式码，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode} */
        Integer modeCode,

        /** 纬度 */
        Double latitude,

        /** 经度 */
        Double longitude,

        /** 绝对高度（椭球面，米） */
        Double height,

        /** 相对起飞点高度（米） */
        Double elevation,

        /** 俯仰角（°） */
        Double attitudePitch,

        /** 横滚角（°） */
        Double attitudeRoll,

        /** 航向角（°） */
        Integer attitudeHead,

        /** 水平速度（m/s） */
        Double horizontalSpeed,

        /** 垂直速度（m/s） */
        Double verticalSpeed,

        /** 风速（0.1 m/s，上报值需除以 10 得到实际 m/s） */
        Double windSpeed,

        /** 风向（枚举 1-8，正北到西北） */
        Integer windDirection,

        /** RID 工作状态（true=正常） */
        Boolean ridState,

        /** 遥控器失控动作（0=悬停,1=降落,2=返航,3=上升，rw） */
        Integer rcLostAction,

        /** 相机信息（array of struct） */
        Object cameras,

        /** 电池信息（struct：capacity_percent/remain_flight_time/return_home_power/landing_power/batteries） */
        Object battery,

        /** 定位状态（struct：is_fixed/quality/gps_number/rtk_number） */
        Object positionState,

        /** 累计飞行时间（秒） */
        Double totalFlightTime,

        /** 激活时间（unix 秒） */
        Long activationTime,

        /** 固件版本 */
        String firmwareVersion,

        /** 档位，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.Gear} */
        Integer gear,

        /** 限高（米） */
        Integer heightLimit,

        /** 距 Home 点距离（米） */
        Double homeDistance,

        /** 限远状态（struct：state/distance_limit/is_near_distance_limit） */
        Object distanceLimitStatus,

        /** 返航高度（米） */
        Integer rthAltitude,

        /** 接近限飞区（0=未达到，1=接近） */
        Integer isNearAreaLimit,

        /** 接近限高（0=未达到，1=接近） */
        Integer isNearHeightLimit,

        /** 保养状态（struct：maintain_status_array，3 种保养类型） */
        Object maintainStatus,

        /** 夜航灯状态（0=关闭，1=打开） */
        Integer nightLightsState,

        /** 避障状态（struct：horizon/upside/downside） */
        Object obstacleAvoidance,

        /** 存储（struct：total/used） */
        Object storage,

        /** 累计飞行里程（米） */
        Double totalFlightDistance,

        /** 累计飞行架次 */
        Integer totalFlightSorties,

        /** 轨迹 ID */
        String trackId,

        /** 国家/地区代码（ISO 3166-1 标准，如 CN/US） */
        String country
) {
}
