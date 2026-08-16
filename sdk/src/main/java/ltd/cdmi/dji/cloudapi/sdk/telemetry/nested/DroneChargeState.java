package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器充电状态（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code capacity_percent} — 电量百分比（0-100）</li>
 *   <li>{@code state} — 充电状态（0=空闲, 1=充电中）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#droneChargeState()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 drone_charge_state 字段")
public record DroneChargeState(
        /** 电量百分比（0-100） */
        Integer capacityPercent,

        /** 充电状态（0=空闲, 1=充电中） */
        Integer state
) {
}
