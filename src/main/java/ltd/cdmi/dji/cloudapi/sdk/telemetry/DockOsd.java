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
 * 机场 OSD 遥测数据结构。
 *
 * <p>包含机场所有 OSD 字段（pushMode=0，周期推送），字段名与
 * {@link OsdField} 中机场部分一一对应。使用包装类型（Integer/Double/Long/Object 等）
 * 允许 {@code null}，因为 DJI 机场 OSD 分多条推送，单条消息中部分字段可能缺失。
 *
 * <p>嵌套结构（如 {@code backupBattery}、{@code networkState}、{@code storage} 等）
 * 使用 {@link Object} 表示，具体结构参见 DJI 文档属性列表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html">DJI 机场设备属性推送</a>
 *
 * @see OsdField
 * @see DroneOsd
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DockModeCode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档机场设备属性推送属性列表")
public record DockOsd(

        /** 模式码，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DockModeCode} */
        Integer modeCode,

        /** 纬度 */
        Double latitude,

        /** 经度 */
        Double longitude,

        /**
         * 机场高度。
         * <p>语义：相对起飞点的高度（ALT，单位米），与 {@link DroneOsd#height()}（椭球面高度）
         * 不同——机场静止不动，无椭球面/相对高度区分需求，DJI 文档统一为相对起飞点 ALT。
         * <p>{@code @Inferred}：simulator DockOsdBuilder 上报此字段，DJI 机场 properties 文档
         * 未明确字段类型（绝对/相对），按 simulator 行为推断为相对起飞点 ALT，待真机验证。
         */
        Double height,

        /** 机场累计作业次数 */
        Integer jobNumber,

        /** 激活时间（unix 秒） */
        Long activationTime,

        /** 工作电流（mA） */
        Double workingCurrent,

        /** 工作电压（mV） */
        Integer workingVoltage,

        /** 市电电压（V） */
        Double electricSupplyVoltage,

        /** 备用电池信息（struct：switch/voltage/temperature） */
        Object backupBattery,

        /** 飞行器电池保养信息（struct：maintenance_state/maintenance_time_left/heat_state/batteries） */
        Object droneBatteryMaintenanceInfo,

        /** 保养状态（struct：maintain_status_array） */
        Object maintainStatus,

        /** 任务步骤码 */
        Integer flighttaskStepCode,

        /** 媒体文件详情（struct：remain_upload） */
        Object mediaFileDetail,

        /** 无线链路（struct：dongle_number/4g_link_state/sdr_link_state/link_workmode 等） */
        Object wirelessLink,

        /** DRC 链路状态 */
        Integer drcState,

        /** 网络状态（struct：type/quality/rate） */
        Object networkState,

        /** 存储（struct：total/used） */
        Object storage,

        /** 子设备信息（struct：device_sn/device_model_key/device_online_status/device_paired） */
        Object subDevice,

        /** 舱盖状态（0=关闭，1=打开） */
        Integer coverState,

        /** 飞行器在舱（0=不在舱，1=在舱） */
        Integer droneInDock,

        /** Home 点有效性（0=无效，1=有效，Dock2/Dock3 only） */
        Integer homePositionIsValid,

        /** 机场朝向角（0-359，Dock2/Dock3 only） */
        Integer heading,

        /** 飞行器充电状态（struct：capacity_percent/state） */
        Object droneChargeState,

        /** 机场温度（°C） */
        Double temperature,

        /** 湿度（%） */
        Double humidity,

        /** 风速（m/s） */
        Double windSpeed,

        /** 降雨量（mm） */
        Double rainfall,

        /** 环境温度（°C） */
        Double environmentTemperature,

        /** 补光灯状态（0=关闭，1=打开） */
        Integer supplementLightState,

        /** 机场空调（struct：air_conditioner_state/switch_time） */
        Object airConditioner,

        /** 紧急停止按钮状态（0=未按下，1=按下） */
        Integer emergencyStopState,

        /** 声光报警状态（0=关闭，1=开启） */
        Integer alarmState,

        /** 推杆状态（0=收回，1=展开） */
        Integer putterState,

        /** 电池存储模式 */
        Integer batteryStoreMode,

        /** 备用降落点（struct：longitude/latitude/safe_land_height/is_configured/height） */
        Object alternateLandPoint,

        /** 首次上电时间（unix 毫秒） */
        Long firstPowerOn,

        /** 定位状态（struct：is_calibration/is_fixed/quality/gps_number/rtk_number） */
        Object positionState,

        /** 自收敛坐标（struct，pushMode=0 周期推送） */
        Object selfConvergeCoordinate
) {
}
