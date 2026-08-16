package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器搜星状态（Drone 专属）。
 *
 * <p>与 {@link DockPositionState} 的区别：Drone 无 {@code is_calibration} 字段，
 * 且 {@code quality} 枚举范围不同（Drone 仅 1-5，不含 10=RTK fixed）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code is_fixed} — 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败）</li>
 *   <li>{@code quality} — 搜星档位（1-5）</li>
 *   <li>{@code gps_number} — GPS 搜星数量</li>
 *   <li>{@code rtk_number} — RTK 搜星数量</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#positionState()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 position_state 字段")
public record DronePositionState(
        /** 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败） */
        Integer isFixed,

        /** 搜星档位（1-5） */
        Integer quality,

        /** GPS 搜星数量 */
        Integer gpsNumber,

        /** RTK 搜星数量 */
        Integer rtkNumber
) {
}
