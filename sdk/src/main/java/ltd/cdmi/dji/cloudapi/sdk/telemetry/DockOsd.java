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
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AirConditioner;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AlternateLandPoint;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.BackupBattery;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DockMaintainStatus;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DockPositionState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DroneBatteryMaintenanceInfo;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DroneChargeState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.MediaFileDetail;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.NetworkState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.Storage;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.SubDevice;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.WirelessLink;

/**
 * 机场 OSD 遥测数据结构。
 *
 * <p>包含机场所有 OSD 字段（pushMode=0，周期推送），字段名与
 * {@link OsdField} 中机场部分一一对应。使用包装类型（Integer/Double/Long 等）
 * 允许 {@code null}，因为 DJI 机场 OSD 分多条推送，单条消息中部分字段可能缺失。
 *
 * <p>嵌套结构使用 {@code telemetry.nested} 包中的 typed record 定义，
 * 替代早期的 {@link Object} 类型，提供编译时类型安全。
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

        /** 备用电池信息 */
        BackupBattery backupBattery,

        /** 飞行器电池保养信息 */
        DroneBatteryMaintenanceInfo droneBatteryMaintenanceInfo,

        /** 保养状态 */
        DockMaintainStatus maintainStatus,

        /** 任务步骤码 */
        Integer flighttaskStepCode,

        /** 媒体文件详情 */
        MediaFileDetail mediaFileDetail,

        /** 无线链路 */
        WirelessLink wirelessLink,

        /** DRC 链路状态 */
        Integer drcState,

        /** 网络状态 */
        NetworkState networkState,

        /** 存储 */
        Storage storage,

        /** 子设备信息 */
        SubDevice subDevice,

        /** 舱盖状态（0=关闭，1=打开） */
        Integer coverState,

        /** 飞行器在舱（0=不在舱，1=在舱） */
        Integer droneInDock,

        /** Home 点有效性（0=无效，1=有效，Dock2/Dock3 only） */
        Integer homePositionIsValid,

        /** 机场朝向角（0-359，Dock2/Dock3 only） */
        Integer heading,

        /** 飞行器充电状态 */
        DroneChargeState droneChargeState,

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

        /** 机场空调 */
        AirConditioner airConditioner,

        /** 紧急停止按钮状态（0=未按下，1=按下） */
        Integer emergencyStopState,

        /** 声光报警状态（0=关闭，1=开启） */
        Integer alarmState,

        /** 推杆状态（0=收回，1=展开） */
        Integer putterState,

        /** 电池存储模式 */
        Integer batteryStoreMode,

        /** 备用降落点 */
        AlternateLandPoint alternateLandPoint,

        /** 首次上电时间（unix 毫秒） */
        Long firstPowerOn,

        /** 定位状态 */
        DockPositionState positionState,

        /**
         * 自收敛坐标（Dock3 专属）。
         * <p>{@code @Inferred}：DJI Dock3 文档列为 struct 但未定义子字段结构，
         * 保持 {@link Object} 类型待真机验证后补充 typed record。
         */
        @Inferred(
                reason = "DJI Dock3 文档列为 struct 但未定义子字段结构，基于 DJI 文档字段存在性推断",
                verifyPoint = "待 DJI 文档补充或真机验证字段名、类型、是否必填、业务含义"
        )
        Object selfConvergeCoordinate
) {
    /**
     * 转换为 Builder（用于字段级合并场景）。
     * <p>DJI 机场 OSD 分多条推送，单条消息中部分字段可能缺失，调用方需合并多源 OSD。
     * <pre>{@code
     * DockOsd merged = existing.toBuilder()
     *     .mergeNonNullFrom(partial)
     *     .build();
     * }</pre>
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * DockOsd 的可变构造器，支持字段级合并。
     *
     * <p>设计动机：机场 OSD 分多条推送，调用方需合并多源 partial OSD，
     * 全参数 record 重建（35 字段）繁琐易错。Builder 提供便捷的字段设置与合并辅助。
     *
     * <p><b>合并语义</b>：{@link #mergeNonNullFrom(DockOsd)} 仅覆盖 source 中非 null 字段。
     */
    public static final class Builder {
        private Integer modeCode;
        private Double latitude;
        private Double longitude;
        private Double height;
        private Integer jobNumber;
        private Long activationTime;
        private Double workingCurrent;
        private Integer workingVoltage;
        private Double electricSupplyVoltage;
        private BackupBattery backupBattery;
        private DroneBatteryMaintenanceInfo droneBatteryMaintenanceInfo;
        private DockMaintainStatus maintainStatus;
        private Integer flighttaskStepCode;
        private MediaFileDetail mediaFileDetail;
        private WirelessLink wirelessLink;
        private Integer drcState;
        private NetworkState networkState;
        private Storage storage;
        private SubDevice subDevice;
        private Integer coverState;
        private Integer droneInDock;
        private Integer homePositionIsValid;
        private Integer heading;
        private DroneChargeState droneChargeState;
        private Double temperature;
        private Double humidity;
        private Double windSpeed;
        private Double rainfall;
        private Double environmentTemperature;
        private Integer supplementLightState;
        private AirConditioner airConditioner;
        private Integer emergencyStopState;
        private Integer alarmState;
        private Integer putterState;
        private Integer batteryStoreMode;
        private AlternateLandPoint alternateLandPoint;
        private Long firstPowerOn;
        private DockPositionState positionState;
        private Object selfConvergeCoordinate;

        public Builder() {}

        Builder(DockOsd o) {
            this.modeCode = o.modeCode;
            this.latitude = o.latitude;
            this.longitude = o.longitude;
            this.height = o.height;
            this.jobNumber = o.jobNumber;
            this.activationTime = o.activationTime;
            this.workingCurrent = o.workingCurrent;
            this.workingVoltage = o.workingVoltage;
            this.electricSupplyVoltage = o.electricSupplyVoltage;
            this.backupBattery = o.backupBattery;
            this.droneBatteryMaintenanceInfo = o.droneBatteryMaintenanceInfo;
            this.maintainStatus = o.maintainStatus;
            this.flighttaskStepCode = o.flighttaskStepCode;
            this.mediaFileDetail = o.mediaFileDetail;
            this.wirelessLink = o.wirelessLink;
            this.drcState = o.drcState;
            this.networkState = o.networkState;
            this.storage = o.storage;
            this.subDevice = o.subDevice;
            this.coverState = o.coverState;
            this.droneInDock = o.droneInDock;
            this.homePositionIsValid = o.homePositionIsValid;
            this.heading = o.heading;
            this.droneChargeState = o.droneChargeState;
            this.temperature = o.temperature;
            this.humidity = o.humidity;
            this.windSpeed = o.windSpeed;
            this.rainfall = o.rainfall;
            this.environmentTemperature = o.environmentTemperature;
            this.supplementLightState = o.supplementLightState;
            this.airConditioner = o.airConditioner;
            this.emergencyStopState = o.emergencyStopState;
            this.alarmState = o.alarmState;
            this.putterState = o.putterState;
            this.batteryStoreMode = o.batteryStoreMode;
            this.alternateLandPoint = o.alternateLandPoint;
            this.firstPowerOn = o.firstPowerOn;
            this.positionState = o.positionState;
            this.selfConvergeCoordinate = o.selfConvergeCoordinate;
        }

        public Builder modeCode(Integer v) { this.modeCode = v; return this; }
        public Builder latitude(Double v) { this.latitude = v; return this; }
        public Builder longitude(Double v) { this.longitude = v; return this; }
        public Builder height(Double v) { this.height = v; return this; }
        public Builder jobNumber(Integer v) { this.jobNumber = v; return this; }
        public Builder activationTime(Long v) { this.activationTime = v; return this; }
        public Builder workingCurrent(Double v) { this.workingCurrent = v; return this; }
        public Builder workingVoltage(Integer v) { this.workingVoltage = v; return this; }
        public Builder electricSupplyVoltage(Double v) { this.electricSupplyVoltage = v; return this; }
        public Builder backupBattery(BackupBattery v) { this.backupBattery = v; return this; }
        public Builder droneBatteryMaintenanceInfo(DroneBatteryMaintenanceInfo v) { this.droneBatteryMaintenanceInfo = v; return this; }
        public Builder maintainStatus(DockMaintainStatus v) { this.maintainStatus = v; return this; }
        public Builder flighttaskStepCode(Integer v) { this.flighttaskStepCode = v; return this; }
        public Builder mediaFileDetail(MediaFileDetail v) { this.mediaFileDetail = v; return this; }
        public Builder wirelessLink(WirelessLink v) { this.wirelessLink = v; return this; }
        public Builder drcState(Integer v) { this.drcState = v; return this; }
        public Builder networkState(NetworkState v) { this.networkState = v; return this; }
        public Builder storage(Storage v) { this.storage = v; return this; }
        public Builder subDevice(SubDevice v) { this.subDevice = v; return this; }
        public Builder coverState(Integer v) { this.coverState = v; return this; }
        public Builder droneInDock(Integer v) { this.droneInDock = v; return this; }
        public Builder homePositionIsValid(Integer v) { this.homePositionIsValid = v; return this; }
        public Builder heading(Integer v) { this.heading = v; return this; }
        public Builder droneChargeState(DroneChargeState v) { this.droneChargeState = v; return this; }
        public Builder temperature(Double v) { this.temperature = v; return this; }
        public Builder humidity(Double v) { this.humidity = v; return this; }
        public Builder windSpeed(Double v) { this.windSpeed = v; return this; }
        public Builder rainfall(Double v) { this.rainfall = v; return this; }
        public Builder environmentTemperature(Double v) { this.environmentTemperature = v; return this; }
        public Builder supplementLightState(Integer v) { this.supplementLightState = v; return this; }
        public Builder airConditioner(AirConditioner v) { this.airConditioner = v; return this; }
        public Builder emergencyStopState(Integer v) { this.emergencyStopState = v; return this; }
        public Builder alarmState(Integer v) { this.alarmState = v; return this; }
        public Builder putterState(Integer v) { this.putterState = v; return this; }
        public Builder batteryStoreMode(Integer v) { this.batteryStoreMode = v; return this; }
        public Builder alternateLandPoint(AlternateLandPoint v) { this.alternateLandPoint = v; return this; }
        public Builder firstPowerOn(Long v) { this.firstPowerOn = v; return this; }
        public Builder positionState(DockPositionState v) { this.positionState = v; return this; }
        public Builder selfConvergeCoordinate(Object v) { this.selfConvergeCoordinate = v; return this; }

        /**
         * 合并 source 中非 null 字段到当前 Builder（覆盖语义）。
         * <p>DJI 机场 OSD 分多条推送，调用方用此方法合并多源 partial OSD。
         *
         * @param source 提供覆盖字段的 DockOsd（null 字段不覆盖）
         * @return this（链式调用）
         */
        public Builder mergeNonNullFrom(DockOsd source) {
            if (source == null) return this;
            if (source.modeCode != null) this.modeCode = source.modeCode;
            if (source.latitude != null) this.latitude = source.latitude;
            if (source.longitude != null) this.longitude = source.longitude;
            if (source.height != null) this.height = source.height;
            if (source.jobNumber != null) this.jobNumber = source.jobNumber;
            if (source.activationTime != null) this.activationTime = source.activationTime;
            if (source.workingCurrent != null) this.workingCurrent = source.workingCurrent;
            if (source.workingVoltage != null) this.workingVoltage = source.workingVoltage;
            if (source.electricSupplyVoltage != null) this.electricSupplyVoltage = source.electricSupplyVoltage;
            if (source.backupBattery != null) this.backupBattery = source.backupBattery;
            if (source.droneBatteryMaintenanceInfo != null) this.droneBatteryMaintenanceInfo = source.droneBatteryMaintenanceInfo;
            if (source.maintainStatus != null) this.maintainStatus = source.maintainStatus;
            if (source.flighttaskStepCode != null) this.flighttaskStepCode = source.flighttaskStepCode;
            if (source.mediaFileDetail != null) this.mediaFileDetail = source.mediaFileDetail;
            if (source.wirelessLink != null) this.wirelessLink = source.wirelessLink;
            if (source.drcState != null) this.drcState = source.drcState;
            if (source.networkState != null) this.networkState = source.networkState;
            if (source.storage != null) this.storage = source.storage;
            if (source.subDevice != null) this.subDevice = source.subDevice;
            if (source.coverState != null) this.coverState = source.coverState;
            if (source.droneInDock != null) this.droneInDock = source.droneInDock;
            if (source.homePositionIsValid != null) this.homePositionIsValid = source.homePositionIsValid;
            if (source.heading != null) this.heading = source.heading;
            if (source.droneChargeState != null) this.droneChargeState = source.droneChargeState;
            if (source.temperature != null) this.temperature = source.temperature;
            if (source.humidity != null) this.humidity = source.humidity;
            if (source.windSpeed != null) this.windSpeed = source.windSpeed;
            if (source.rainfall != null) this.rainfall = source.rainfall;
            if (source.environmentTemperature != null) this.environmentTemperature = source.environmentTemperature;
            if (source.supplementLightState != null) this.supplementLightState = source.supplementLightState;
            if (source.airConditioner != null) this.airConditioner = source.airConditioner;
            if (source.emergencyStopState != null) this.emergencyStopState = source.emergencyStopState;
            if (source.alarmState != null) this.alarmState = source.alarmState;
            if (source.putterState != null) this.putterState = source.putterState;
            if (source.batteryStoreMode != null) this.batteryStoreMode = source.batteryStoreMode;
            if (source.alternateLandPoint != null) this.alternateLandPoint = source.alternateLandPoint;
            if (source.firstPowerOn != null) this.firstPowerOn = source.firstPowerOn;
            if (source.positionState != null) this.positionState = source.positionState;
            if (source.selfConvergeCoordinate != null) this.selfConvergeCoordinate = source.selfConvergeCoordinate;
            return this;
        }

        public DockOsd build() {
            return new DockOsd(
                    modeCode, latitude, longitude, height, jobNumber, activationTime,
                    workingCurrent, workingVoltage, electricSupplyVoltage, backupBattery,
                    droneBatteryMaintenanceInfo, maintainStatus, flighttaskStepCode,
                    mediaFileDetail, wirelessLink, drcState, networkState, storage,
                    subDevice, coverState, droneInDock, homePositionIsValid, heading,
                    droneChargeState, temperature, humidity, windSpeed, rainfall,
                    environmentTemperature, supplementLightState, airConditioner,
                    emergencyStopState, alarmState, putterState, batteryStoreMode,
                    alternateLandPoint, firstPowerOn, positionState, selfConvergeCoordinate);
        }
    }
}
