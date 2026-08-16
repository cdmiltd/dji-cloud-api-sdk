package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 机场保养信息（Dock 专属）。
 *
 * <p>与 {@link DroneMaintainStatus} 的区别：本结构的数组元素含 {@code last_maintain_work_sorties}，
 * 而 Drone 版本含 {@code last_maintain_flight_time} + {@code last_maintain_flight_sorties}。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code maintain_status_array} — 保养信息数组</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#maintainStatus()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 maintain_status 字段")
public record DockMaintainStatus(
        /** 保养信息数组 */
        List<MaintainStatusItem> maintainStatusArray
) {
    /**
     * 保养信息数组元素（Dock 版本）。
     *
     * <p>DJI 文档字段：
     * <ul>
     *   <li>{@code state} — 保养状态（0=无保养, 1=有保养）</li>
     *   <li>{@code last_maintain_type} — 上一次保养类型（0=无保养, 17=机场常规保养, 18=机场深度保养）</li>
     *   <li>{@code last_maintain_time} — 上一次保养时间（unix 秒）</li>
     *   <li>{@code last_maintain_work_sorties} — 上一次保养时作业架次</li>
     * </ul>
     */
    public record MaintainStatusItem(
            /** 保养状态（0=无保养, 1=有保养） */
            Integer state,

            /** 上一次保养类型（0=无保养, 17=机场常规保养, 18=机场深度保养） */
            Integer lastMaintainType,

            /** 上一次保养时间（unix 秒） */
            Long lastMaintainTime,

            /** 上一次保养时作业架次 */
            Integer lastMaintainWorkSorties
    ) {
    }
}
