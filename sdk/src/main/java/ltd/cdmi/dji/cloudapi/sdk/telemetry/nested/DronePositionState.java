package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器搜星状态（Drone 专属）。
 *
 * <p>与 {@link DockPositionState} 的区别：Drone 无 {@code is_calibration} 字段，
 * 且 {@code quality} 枚举范围不同（Drone 为 1-5，另含 10=RTK fixed，见 M4D/M4TD 页）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code is_fixed} — 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败）</li>
 *   <li>{@code quality} — 搜星档位（1-5，10=RTK fixed）</li>
 *   <li>{@code gps_number} — GPS 搜星数量</li>
 *   <li>{@code rtk_number} — RTK 搜星数量</li>
 * </ul>
 *
 * <p>飞行器适用性：字段均为 {@code Integer}（DJI 文档 int），M4D/M4TD 飞行器 position_state
 * 页与 M30/M3D 页一致，quality 值域 1-5 及 10=RTK fixed。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#positionState()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 position_state 字段（M4D/M4TD、M30、M3D 页均为 Integer int 类型）")
public record DronePositionState(
        /** 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败） */
        Integer isFixed,

        /** 搜星档位（1-5，10=RTK fixed） */
        Integer quality,

        /** GPS 搜星数量 */
        Integer gpsNumber,

        /** RTK 搜星数量 */
        Integer rtkNumber
) {
}
