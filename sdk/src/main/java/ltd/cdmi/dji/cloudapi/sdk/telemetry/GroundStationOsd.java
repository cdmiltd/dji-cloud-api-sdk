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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AlternateLandPoint;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DockPositionState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.NetworkState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.Storage;

/**
 * O4 地面站 OSD（pushMode=0 定频上报）遥测数据。
 *
 * <p>对应 DJI Cloud API O4 地面站设备属性中 pushMode=0 的字段集合，通过
 * {@code thing/product/{device_sn}/osd} 上报。嵌套结构：
 * <ul>
 *   <li>{@code mode_code} — 工作模式，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.GroundStationModeCode}</li>
 *   <li>{@code low_power_switch_state} — 低电量开关状态（0=关闭, 1=开启）</li>
 *   <li>{@code electric_supply_power} — 市电功率（W）</li>
 *   <li>{@code power_mode} — 供电模式，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.SupplyPowerMode}</li>
 *   <li>{@code battery_voltage} — 电池电压（mV）</li>
 *   <li>{@code tilt_angle} — 倾斜角，见 {@link TiltAngle}</li>
 *   <li>{@code basestation_work_mode} — 见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.BasestationWorkMode}</li>
 *   <li>{@code under_work_mode} — 见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.UnderWorkMode}</li>
 *   <li>{@code lan_status} / {@code poe_status} — 见相应枚举</li>
 *   <li>{@code battery} — 见 {@link BatteryInfo}</li>
 *   <li>{@code self_converge_coordinate} — 见 {@link SelfConvergeCoordinate}</li>
 *   <li>{@code electric_supply_voltage} — 市电电压（V，double）</li>
 *   <li>{@code acc_time} — 累计运行时长（秒）</li>
 *   <li>{@code position_state} / {@code alternate_land_point} / {@code network_state} / {@code storage} — 复用既有结构</li>
 *   <li>{@code home_position_is_valid} — 见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.GroundStationHomePositionIsValid}</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 pushMode=0 字段集合")
public record GroundStationOsd(
        /** 工作模式（0-6, 9），见 GroundStationModeCode */
        Integer modeCode,

        /** 低电量开关状态（0=关闭, 1=开启） */
        Integer lowPowerSwitchState,

        /** 市电功率（W） */
        Integer electricSupplyPower,

        /** 供电模式（0=电池, 1=PD, 2=POE） */
        Integer powerMode,

        /** 电池电压（mV） */
        Integer batteryVoltage,

        /** 倾斜角 */
        TiltAngle tiltAngle,

        /** 基站工作模式（0=漫游基站, 1=外置机场, 2=简易机场） */
        Integer basestationWorkMode,

        /** 工作环境模式（0=非棚下, 1=棚下） */
        Integer underWorkMode,

        /** 局域网状态（0-4），见 LanStatus */
        Integer lanStatus,

        /** POE 状态（0-4），见 PoeStatus */
        Integer poeStatus,

        /** 电池信息 */
        BatteryInfo battery,

        /** 自收敛坐标 */
        SelfConvergeCoordinate selfConvergeCoordinate,

        /** 市电电压（V） */
        Double electricSupplyVoltage,

        /** 累计运行时长（秒） */
        Integer accTime,

        /** 经度 */
        Double longitude,

        /** 纬度 */
        Double latitude,

        /** 椭球高度 */
        Double height,

        /** 搜星状态（复用 DockPositionState 结构） */
        DockPositionState positionState,

        /** 备降点信息 */
        AlternateLandPoint alternateLandPoint,

        /** 网络状态 */
        NetworkState networkState,

        /** 存储容量信息 */
        Storage storage,

        /** home 点有效性（0=无效, 1=有效），见 GroundStationHomePositionIsValid */
        Integer homePositionIsValid
) {
    /**
     * 倾斜角（tilt_angle struct）。
     *
     * <p>DJI 文档字段：{@code value}（倾斜角数值）/ {@code valid}（0=无效, 1=有效）。
     *
     * <p>{@code @Inferred}：value 的精确类型未逐项标注，按角度语义推断为 Double。
     */
    @Inferred(
            reason = "DJI O4 页 tilt_angle.value 类型未逐项标注，按角度语义推断为 Double",
            verifyPoint = "待 O4 页原文核实 tilt_angle.value 的精确类型"
    )
    public record TiltAngle(
            /** 倾斜角数值 */
            Double value,

            /** 是否有效（0=无效, 1=有效） */
            Integer valid
    ) {
    }

    /**
     * 地面站电池信息（battery struct）。
     *
     * <p>DJI 文档字段：{@code capacity_percent} / {@code voltage}（mV）/ {@code current} / {@code temperature}。
     *
     * <p>{@code @Inferred}：current / temperature 的精确类型未逐项标注，按物理量语义推断为 Double。
     */
    @Inferred(
            reason = "DJI O4 页 battery.current/temperature 类型未逐项标注，按物理量语义推断为 Double",
            verifyPoint = "待 O4 页原文核实 battery.current/temperature 的精确类型"
    )
    public record BatteryInfo(
            /** 电池剩余电量（0-100） */
            Integer capacityPercent,

            /** 电压（mV） */
            Integer voltage,

            /** 电流 */
            Double current,

            /** 温度（°C） */
            Double temperature
    ) {
    }

    /**
     * 自收敛坐标（self_converge_coordinate struct）。
     *
     * <p>DJI 文档字段：{@code longitude} / {@code latitude} / {@code height}。
     */
    @Verified(basis = "DJI O4 地面站设备属性页 self_converge_coordinate struct（longitude/latitude/height）")
    public record SelfConvergeCoordinate(
            /** 经度 */
            Double longitude,

            /** 纬度 */
            Double latitude,

            /** 椭球高度 */
            Double height
    ) {
    }
}
