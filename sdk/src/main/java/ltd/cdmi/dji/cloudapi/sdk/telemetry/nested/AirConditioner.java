package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 机场空调工作状态信息（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code air_conditioner_state} — 机场空调状态（枚举 0-15）</li>
 *   <li>{@code switch_time} — 剩余等待可切换时间（秒）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#airConditioner()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 air_conditioner 字段")
public record AirConditioner(
        /** 机场空调状态（枚举 0-15，共 16 值；0=空闲，1=制冷，2=制热，3=除湿，… 各值含义详见 DJI 机场设备属性页 air_conditioner 字段） */
        Integer airConditionerState,

        /** 剩余等待可切换时间（秒） */
        Integer switchTime
) {
}
