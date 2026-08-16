package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 网络状态（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code type} — 网络类型（1=4G, 2=以太网）</li>
 *   <li>{@code quality} — 网络质量（0=无信号, 1=差, 2=较差, 3=一般, 4=较好, 5=好）</li>
 *   <li>{@code rate} — 网络速率（KB/s）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#networkState()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 network_state 字段")
public record NetworkState(
        /** 网络类型（1=4G, 2=以太网） */
        Integer type,

        /** 网络质量（0=无信号, 1=差, 2=较差, 3=一般, 4=较好, 5=好） */
        Integer quality,

        /** 网络速率（KB/s） */
        Double rate
) {
}
