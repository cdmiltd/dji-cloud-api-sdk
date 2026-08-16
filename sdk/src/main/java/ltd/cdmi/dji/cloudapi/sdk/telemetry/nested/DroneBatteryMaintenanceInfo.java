package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器电池保养信息（Dock 专属）。
 *
 * <p>当飞行器舱内关机时由本物模型上报机场连接飞行器的电池信息。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code maintenance_state} — 保养状态（0=无需保养, 1=待保养, 2=正在保养）</li>
 *   <li>{@code maintenance_time_left} — 电池保养剩余时间（小时，向下取整）</li>
 *   <li>{@code heat_state} — 电池加热保温状态（0=未开启, 1=加热中, 2=保温中）</li>
 *   <li>{@code batteries} — 电池详细信息数组</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#droneBatteryMaintenanceInfo()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 drone_battery_maintenance_info 字段")
public record DroneBatteryMaintenanceInfo(
        /** 保养状态（0=无需保养, 1=待保养, 2=正在保养） */
        Integer maintenanceState,

        /** 电池保养剩余时间（小时，向下取整） */
        Integer maintenanceTimeLeft,

        /** 电池加热保温状态（0=未开启, 1=加热中, 2=保温中） */
        Integer heatState,

        /** 电池详细信息数组 */
        List<MaintenanceBattery> batteries
) {
    /**
     * 保养电池详细信息（DroneBatteryMaintenanceInfo.batteries 数组元素）。
     *
     * <p>与 {@link Battery.BatteryCell} 的区别：本结构字段更少（仅 capacity_percent, index, voltage, temperature），
     * 因为舱内关机时无法获取完整电池信息。
     */
    public record MaintenanceBattery(
            /** 电池剩余电量（0-100，异常值为 32767） */
            Integer capacityPercent,

            /** 电池序号（0=左电池, 1=右电池） */
            Integer index,

            /** 电压（mV，异常值为 32767） */
            Integer voltage,

            /** 温度（°C，保留小数点后一位，异常值为 32767） */
            Double temperature
    ) {
    }
}
