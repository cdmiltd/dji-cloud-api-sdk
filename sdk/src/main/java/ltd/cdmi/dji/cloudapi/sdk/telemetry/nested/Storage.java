package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 存储容量信息（Dock 与 Drone 共用）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code total} — 总容量（KB）</li>
 *   <li>{@code used} — 已使用容量（KB）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#storage()
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#storage()
 */
@Verified(basis = "DJI Cloud API 官方文档机场/飞行器设备属性 storage 字段")
public record Storage(
        /** 总容量（KB） */
        Integer total,

        /** 已使用容量（KB） */
        Integer used
) {
}
