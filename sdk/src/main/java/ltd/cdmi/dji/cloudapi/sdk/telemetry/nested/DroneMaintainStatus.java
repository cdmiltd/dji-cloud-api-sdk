package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器保养信息（Drone 专属）。
 *
 * <p>与 {@link DockMaintainStatus} 的区别：本结构的数组元素含 {@code last_maintain_flight_time} +
 * {@code last_maintain_flight_sorties}，而 Dock 版本仅含 {@code last_maintain_work_sorties}；
 * 且 {@code last_maintain_type} 枚举值不同（Drone 为 1-3，Dock 为 0/17/18）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code maintain_status_array} — 保养信息数组</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#maintainStatus()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 maintain_status 字段")
public record DroneMaintainStatus(
        /** 保养信息数组 */
        List<MaintainStatusItem> maintainStatusArray
) {
    /**
     * 保养信息数组元素（Drone 版本）。
     *
     * <p>DJI 文档字段：
     * <ul>
     *   <li>{@code state} — 保养状态（0=无保养, 1=有保养）</li>
     *   <li>{@code last_maintain_type} — 上一次保养类型（1=基础保养, 2=常规保养, 3=深度保养）</li>
     *   <li>{@code last_maintain_time} — 上一次保养时间（unix 秒）</li>
     *   <li>{@code last_maintain_flight_time} — 上一次保养时飞行航时（小时）</li>
     *   <li>{@code last_maintain_flight_sorties} — 上一次保养时飞行架次</li>
     * </ul>
     */
    public record MaintainStatusItem(
            /** 保养状态（0=无保养, 1=有保养） */
            Integer state,

            /** 上一次保养类型（1=基础保养, 2=常规保养, 3=深度保养） */
            Integer lastMaintainType,

            /** 上一次保养时间（unix 秒） */
            Long lastMaintainTime,

            /** 上一次保养时飞行航时（小时） */
            Integer lastMaintainFlightTime,

            /** 上一次保养时飞行架次 */
            Integer lastMaintainFlightSorties
    ) {
    }
}
