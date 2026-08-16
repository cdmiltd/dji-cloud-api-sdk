package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 机场搜星状态（Dock 专属）。
 *
 * <p>与 {@link DronePositionState} 的区别：Dock 多了 {@code is_calibration}（是否标定）字段，
 * 且 {@code quality} 枚举范围不同（Dock 含 10=RTK fixed，Drone 不含）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code is_calibration} — 是否标定（0=未标定, 1=已标定）</li>
 *   <li>{@code is_fixed} — 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败）</li>
 *   <li>{@code quality} — 搜星档位（1-5, 10=RTK fixed）</li>
 *   <li>{@code gps_number} — GPS 搜星数量</li>
 *   <li>{@code rtk_number} — RTK 搜星数量</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#positionState()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 position_state 字段")
public record DockPositionState(
        /** 是否标定（0=未标定, 1=已标定） */
        Integer isCalibration,

        /** 是否收敛（0=未开始, 1=收敛中, 2=收敛成功, 3=收敛失败） */
        Integer isFixed,

        /** 搜星档位（1-5, 10=RTK fixed） */
        Integer quality,

        /** GPS 搜星数量 */
        Integer gpsNumber,

        /** RTK 搜星数量 */
        Integer rtkNumber
) {
}
