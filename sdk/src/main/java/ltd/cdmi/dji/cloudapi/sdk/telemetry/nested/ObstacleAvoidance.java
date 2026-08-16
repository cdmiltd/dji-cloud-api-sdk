package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器避障状态（Drone 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code horizon} — 水平避障状态（0=关闭, 1=开启）</li>
 *   <li>{@code upside} — 上视避障状态（0=关闭, 1=开启）</li>
 *   <li>{@code downside} — 下视避障状态（0=关闭, 1=开启）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#obstacleAvoidance()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 obstacle_avoidance 字段")
public record ObstacleAvoidance(
        /** 水平避障状态（0=关闭, 1=开启） */
        Integer horizon,

        /** 上视避障状态（0=关闭, 1=开启） */
        Integer upside,

        /** 下视避障状态（0=关闭, 1=开启） */
        Integer downside
) {
}
