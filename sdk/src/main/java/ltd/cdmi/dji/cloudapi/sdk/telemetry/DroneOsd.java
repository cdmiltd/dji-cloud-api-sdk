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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.Battery;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.CameraInfo;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DistanceLimitStatus;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DroneMaintainStatus;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.DronePositionState;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.ObstacleAvoidance;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.Storage;

/**
 * 飞行器 OSD 遥测数据结构。
 *
 * <p>包含飞行器所有 OSD 字段（pushMode=0，周期推送），字段名与
 * {@link OsdField} 中飞行器部分一一对应，并包含与机场共用的字段
 * （{@code modeCode}、{@code latitude}、{@code longitude}、{@code height}、
 * {@code windSpeed}、{@code activationTime}、{@code maintainStatus}、
 * {@code positionState}、{@code storage}）。
 *
 * <p>使用包装类型（Integer/Double/Long/String 等）允许 {@code null}，
 * 因为不同机型上报的字段集可能不同（如 M400 Pilot 不上报限远/返航高度字段）。
 *
 * <p>嵌套结构使用 {@code telemetry.nested} 包中的 typed record 定义，
 * 替代早期的 {@link Object} 类型，提供编译时类型安全。
 *
 * <p>注意：{@code height} 为绝对高度（椭球面），{@code elevation} 为相对起飞点高度。
 *
 * <p><b>动态 key 字段（extras）</b>：M30 旧版协议使用负载索引（如 {@code "52-0-0"}）作为
 * JSON 顶层 key 上报负载属性，无法用 record 固定字段表达。{@code extras} 字段配合
 * {@link JsonAnyGetter} 注解，序列化时将 Map 内容展开到 JSON 顶层——与固定字段合并
 * 输出。其他机型 {@code extras} 为 null，在 {@code NON_NULL} 配置下不输出。
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

        /** 相机信息数组 */
        List<CameraInfo> cameras,

        /** 电池信息 */
        Battery battery,

        /** 定位状态 */
        DronePositionState positionState,

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

        /** 限远状态 */
        DistanceLimitStatus distanceLimitStatus,

        /** 返航高度（米） */
        Integer rthAltitude,

        /** 接近限飞区（0=未达到，1=接近） */
        Integer isNearAreaLimit,

        /** 接近限高（0=未达到，1=接近） */
        Integer isNearHeightLimit,

        /** 保养状态 */
        DroneMaintainStatus maintainStatus,

        /** 夜航灯状态（0=关闭，1=打开） */
        Integer nightLightsState,

        /** 避障状态 */
        ObstacleAvoidance obstacleAvoidance,

        /** 存储 */
        Storage storage,

        /** 累计飞行里程（米） */
        Double totalFlightDistance,

        /** 累计飞行架次 */
        Integer totalFlightSorties,

        /** 轨迹 ID */
        String trackId,

        /** 国家/地区代码（ISO 3166-1 标准，如 CN/US） */
        String country,

        /**
         * 动态 key 字段容器，用于容纳无法用 record 固定字段表达的协议字段。
         * <p>M30 旧版协议使用负载索引（如 {@code "52-0-0"}）作为 JSON 顶层 key，
         * 序列化时通过 {@link JsonAnyGetter} 展开到 JSON 顶层。其他机型为 null。
         */
        Map<String, Object> extras
) {
    /**
     * 序列化时将 extras 展开为顶层属性（不包裹在 "extras" 键下）。
     * <p>与 {@link ltd.cdmi.dji.cloudapi.sdk.command.property.PropertySetRequest} 的
     * {@code @JsonAnyGetter} 模式一致，实现动态 key 与固定字段合并输出。
     * <p>覆盖 record 默认 accessor 以添加 {@link JsonAnyGetter} 注解，
     * Jackson 识别后不再将 extras 序列化为普通属性，而是展开到 JSON 顶层。
     *
     * @return extras Map，null 时返回空 Map（不输出任何动态字段）
     */
    @JsonAnyGetter
    public Map<String, Object> extras() {
        return extras != null ? extras : java.util.Map.of();
    }

    /**
     * 转换为 Builder（用于字段级合并场景）。
     * <p>调用方典型用法：合并两个 DroneOsd，source 中非 null 字段覆盖 target。
     * <pre>{@code
     * DroneOsd merged = existing.toBuilder()
     *     .mergeNonNullFrom(partial)
     *     .build();
     * }</pre>
     *
     * @return 包含当前所有字段值的 Builder
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * 创建空 Builder。
     * <p>用于从零构造 DroneOsd 场景（如 MQTT OSD 解析）。
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * DroneOsd 的可变构造器，支持字段级合并。
     *
     * <p>设计动机：record 不可变，调用方在 OSD 合并场景（如无人机自报 OSD 覆盖式合并、
     * DRC 高频字段合并）需要字段级更新，全参数 record 重建（33 字段）繁琐易错。
     * Builder 提供便捷的字段设置与合并辅助。
     *
     * <p><b>合并语义</b>：{@link #mergeNonNullFrom(DroneOsd)} 仅覆盖 source 中非 null 字段，
     * 对应"高频遥测字段非 null 覆盖"语义。调用方可多次调用此方法实现多源合并。
     */
    public static final class Builder {
        private Integer modeCode;
        private Double latitude;
        private Double longitude;
        private Double height;
        private Double elevation;
        private Double attitudePitch;
        private Double attitudeRoll;
        private Integer attitudeHead;
        private Double horizontalSpeed;
        private Double verticalSpeed;
        private Double windSpeed;
        private Integer windDirection;
        private Boolean ridState;
        private Integer rcLostAction;
        private List<CameraInfo> cameras;
        private Battery battery;
        private DronePositionState positionState;
        private Double totalFlightTime;
        private Long activationTime;
        private String firmwareVersion;
        private Integer gear;
        private Integer heightLimit;
        private Double homeDistance;
        private DistanceLimitStatus distanceLimitStatus;
        private Integer rthAltitude;
        private Integer isNearAreaLimit;
        private Integer isNearHeightLimit;
        private DroneMaintainStatus maintainStatus;
        private Integer nightLightsState;
        private ObstacleAvoidance obstacleAvoidance;
        private Storage storage;
        private Double totalFlightDistance;
        private Integer totalFlightSorties;
        private String trackId;
        private String country;
        private Map<String, Object> extras;

        public Builder() {}

        Builder(DroneOsd o) {
            this.modeCode = o.modeCode;
            this.latitude = o.latitude;
            this.longitude = o.longitude;
            this.height = o.height;
            this.elevation = o.elevation;
            this.attitudePitch = o.attitudePitch;
            this.attitudeRoll = o.attitudeRoll;
            this.attitudeHead = o.attitudeHead;
            this.horizontalSpeed = o.horizontalSpeed;
            this.verticalSpeed = o.verticalSpeed;
            this.windSpeed = o.windSpeed;
            this.windDirection = o.windDirection;
            this.ridState = o.ridState;
            this.rcLostAction = o.rcLostAction;
            this.cameras = o.cameras;
            this.battery = o.battery;
            this.positionState = o.positionState;
            this.totalFlightTime = o.totalFlightTime;
            this.activationTime = o.activationTime;
            this.firmwareVersion = o.firmwareVersion;
            this.gear = o.gear;
            this.heightLimit = o.heightLimit;
            this.homeDistance = o.homeDistance;
            this.distanceLimitStatus = o.distanceLimitStatus;
            this.rthAltitude = o.rthAltitude;
            this.isNearAreaLimit = o.isNearAreaLimit;
            this.isNearHeightLimit = o.isNearHeightLimit;
            this.maintainStatus = o.maintainStatus;
            this.nightLightsState = o.nightLightsState;
            this.obstacleAvoidance = o.obstacleAvoidance;
            this.storage = o.storage;
            this.totalFlightDistance = o.totalFlightDistance;
            this.totalFlightSorties = o.totalFlightSorties;
            this.trackId = o.trackId;
            this.country = o.country;
            this.extras = o.extras;
        }

        public Builder modeCode(Integer v) { this.modeCode = v; return this; }
        public Builder latitude(Double v) { this.latitude = v; return this; }
        public Builder longitude(Double v) { this.longitude = v; return this; }
        public Builder height(Double v) { this.height = v; return this; }
        public Builder elevation(Double v) { this.elevation = v; return this; }
        public Builder attitudePitch(Double v) { this.attitudePitch = v; return this; }
        public Builder attitudeRoll(Double v) { this.attitudeRoll = v; return this; }
        public Builder attitudeHead(Integer v) { this.attitudeHead = v; return this; }
        public Builder horizontalSpeed(Double v) { this.horizontalSpeed = v; return this; }
        public Builder verticalSpeed(Double v) { this.verticalSpeed = v; return this; }
        public Builder windSpeed(Double v) { this.windSpeed = v; return this; }
        public Builder windDirection(Integer v) { this.windDirection = v; return this; }
        public Builder ridState(Boolean v) { this.ridState = v; return this; }
        public Builder rcLostAction(Integer v) { this.rcLostAction = v; return this; }
        public Builder cameras(List<CameraInfo> v) { this.cameras = v; return this; }
        public Builder battery(Battery v) { this.battery = v; return this; }
        public Builder positionState(DronePositionState v) { this.positionState = v; return this; }
        public Builder totalFlightTime(Double v) { this.totalFlightTime = v; return this; }
        public Builder activationTime(Long v) { this.activationTime = v; return this; }
        public Builder firmwareVersion(String v) { this.firmwareVersion = v; return this; }
        public Builder gear(Integer v) { this.gear = v; return this; }
        public Builder heightLimit(Integer v) { this.heightLimit = v; return this; }
        public Builder homeDistance(Double v) { this.homeDistance = v; return this; }
        public Builder distanceLimitStatus(DistanceLimitStatus v) { this.distanceLimitStatus = v; return this; }
        public Builder rthAltitude(Integer v) { this.rthAltitude = v; return this; }
        public Builder isNearAreaLimit(Integer v) { this.isNearAreaLimit = v; return this; }
        public Builder isNearHeightLimit(Integer v) { this.isNearHeightLimit = v; return this; }
        public Builder maintainStatus(DroneMaintainStatus v) { this.maintainStatus = v; return this; }
        public Builder nightLightsState(Integer v) { this.nightLightsState = v; return this; }
        public Builder obstacleAvoidance(ObstacleAvoidance v) { this.obstacleAvoidance = v; return this; }
        public Builder storage(Storage v) { this.storage = v; return this; }
        public Builder totalFlightDistance(Double v) { this.totalFlightDistance = v; return this; }
        public Builder totalFlightSorties(Integer v) { this.totalFlightSorties = v; return this; }
        public Builder trackId(String v) { this.trackId = v; return this; }
        public Builder country(String v) { this.country = v; return this; }
        public Builder extras(Map<String, Object> v) { this.extras = v; return this; }

        /**
         * 合并 source 中非 null 字段到当前 Builder（覆盖语义）。
         * <p>对应"高频遥测字段非 null 覆盖"语义，用于无人机自报 OSD 覆盖式合并、
         * DRC 高频字段合并等场景。可多次调用实现多源合并。
         *
         * @param source 提供覆盖字段的 DroneOsd（null 字段不覆盖）
         * @return this（链式调用）
         */
        public Builder mergeNonNullFrom(DroneOsd source) {
            if (source == null) return this;
            if (source.modeCode != null) this.modeCode = source.modeCode;
            if (source.latitude != null) this.latitude = source.latitude;
            if (source.longitude != null) this.longitude = source.longitude;
            if (source.height != null) this.height = source.height;
            if (source.elevation != null) this.elevation = source.elevation;
            if (source.attitudePitch != null) this.attitudePitch = source.attitudePitch;
            if (source.attitudeRoll != null) this.attitudeRoll = source.attitudeRoll;
            if (source.attitudeHead != null) this.attitudeHead = source.attitudeHead;
            if (source.horizontalSpeed != null) this.horizontalSpeed = source.horizontalSpeed;
            if (source.verticalSpeed != null) this.verticalSpeed = source.verticalSpeed;
            if (source.windSpeed != null) this.windSpeed = source.windSpeed;
            if (source.windDirection != null) this.windDirection = source.windDirection;
            if (source.ridState != null) this.ridState = source.ridState;
            if (source.rcLostAction != null) this.rcLostAction = source.rcLostAction;
            if (source.cameras != null) this.cameras = source.cameras;
            if (source.battery != null) this.battery = source.battery;
            if (source.positionState != null) this.positionState = source.positionState;
            if (source.totalFlightTime != null) this.totalFlightTime = source.totalFlightTime;
            if (source.activationTime != null) this.activationTime = source.activationTime;
            if (source.firmwareVersion != null) this.firmwareVersion = source.firmwareVersion;
            if (source.gear != null) this.gear = source.gear;
            if (source.heightLimit != null) this.heightLimit = source.heightLimit;
            if (source.homeDistance != null) this.homeDistance = source.homeDistance;
            if (source.distanceLimitStatus != null) this.distanceLimitStatus = source.distanceLimitStatus;
            if (source.rthAltitude != null) this.rthAltitude = source.rthAltitude;
            if (source.isNearAreaLimit != null) this.isNearAreaLimit = source.isNearAreaLimit;
            if (source.isNearHeightLimit != null) this.isNearHeightLimit = source.isNearHeightLimit;
            if (source.maintainStatus != null) this.maintainStatus = source.maintainStatus;
            if (source.nightLightsState != null) this.nightLightsState = source.nightLightsState;
            if (source.obstacleAvoidance != null) this.obstacleAvoidance = source.obstacleAvoidance;
            if (source.storage != null) this.storage = source.storage;
            if (source.totalFlightDistance != null) this.totalFlightDistance = source.totalFlightDistance;
            if (source.totalFlightSorties != null) this.totalFlightSorties = source.totalFlightSorties;
            if (source.trackId != null) this.trackId = source.trackId;
            if (source.country != null) this.country = source.country;
            if (source.extras != null) this.extras = source.extras;
            return this;
        }

        public DroneOsd build() {
            return new DroneOsd(
                    modeCode, latitude, longitude, height, elevation,
                    attitudePitch, attitudeRoll, attitudeHead,
                    horizontalSpeed, verticalSpeed, windSpeed, windDirection,
                    ridState, rcLostAction, cameras, battery, positionState,
                    totalFlightTime, activationTime, firmwareVersion, gear,
                    heightLimit, homeDistance, distanceLimitStatus, rthAltitude,
                    isNearAreaLimit, isNearHeightLimit, maintainStatus, nightLightsState,
                    obstacleAvoidance, storage, totalFlightDistance, totalFlightSorties,
                    trackId, country, extras);
        }
    }
}
