package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器限远状态（Drone 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code state} — 是否开启限远（0=未设置, 1=已设置）</li>
 *   <li>{@code distance_limit} — 限远距离（15-8000 米）</li>
 *   <li>{@code is_near_distance_limit} — 是否接近设定的限制距离（0=未达到, 1=接近）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#distanceLimitStatus()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 distance_limit_status 字段")
public record DistanceLimitStatus(
        /** 是否开启限远（0=未设置, 1=已设置） */
        Integer state,

        /** 限远距离（15-8000 米） */
        Integer distanceLimit,

        /** 是否接近设定的限制距离（0=未达到, 1=接近） */
        Integer isNearDistanceLimit
) {
}
