package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器电池信息（Drone 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code capacity_percent} — 电池总剩余电量（0-100）</li>
 *   <li>{@code remain_flight_time} — 剩余飞行时间（秒）</li>
 *   <li>{@code return_home_power} — 返航所需电量百分比（0-100）</li>
 *   <li>{@code landing_power} — 强制降落电量百分比（0-100）</li>
 *   <li>{@code batteries} — 电池详细信息数组</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#battery()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 battery 字段")
public record Battery(
        /** 电池总剩余电量（0-100） */
        Integer capacityPercent,

        /** 剩余飞行时间（秒） */
        Integer remainFlightTime,

        /** 返航所需电量百分比（0-100） */
        Integer returnHomePower,

        /** 强制降落电量百分比（0-100） */
        Integer landingPower,

        /** 电池详细信息数组 */
        List<BatteryCell> batteries
) {
    /**
     * 电池详细信息（Battery.batteries 数组元素）。
     *
     * <p>与 {@link DroneBatteryMaintenanceInfo.MaintenanceBattery} 的区别：本结构字段更完整
     * （含 sn, type, sub_type, firmware_version, loop_times, high_voltage_storage_days），
     * 因为飞行器开机时可获取完整电池信息。
     *
     * <p>DJI 文档字段：
     * <ul>
     *   <li>{@code capacity_percent} — 电池剩余电量（0-100）</li>
     *   <li>{@code index} — 电池序号</li>
     *   <li>{@code sn} — 电池序列号（SN）</li>
     *   <li>{@code type} — 电池类型</li>
     *   <li>{@code sub_type} — 电池子类型</li>
     *   <li>{@code firmware_version} — 固件版本</li>
     *   <li>{@code loop_times} — 电池循环次数</li>
     *   <li>{@code voltage} — 电压（mV）</li>
     *   <li>{@code temperature} — 温度（°C，保留小数点后一位）</li>
     *   <li>{@code high_voltage_storage_days} — 高电压存储天数（日）</li>
     * </ul>
     */
    public record BatteryCell(
            /** 电池剩余电量（0-100） */
            Integer capacityPercent,

            /** 电池序号 */
            Integer index,

            /** 电池序列号（SN） */
            String sn,

            /** 电池类型 */
            Integer type,

            /** 电池子类型 */
            Integer subType,

            /** 固件版本 */
            String firmwareVersion,

            /** 电池循环次数 */
            Integer loopTimes,

            /** 电压（mV） */
            Integer voltage,

            /** 温度（°C，保留小数点后一位） */
            Double temperature,

            /** 高电压存储天数（日） */
            Integer highVoltageStorageDays
    ) {
    }
}
