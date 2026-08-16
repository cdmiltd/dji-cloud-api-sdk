package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 备降点信息（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code longitude} — 经度</li>
 *   <li>{@code latitude} — 纬度</li>
 *   <li>{@code safe_land_height} — 安全高度（备降转移高）</li>
 *   <li>{@code is_configured} — 是否设置备降点（0=未设置, 1=已设置）</li>
 *   <li>{@code height} — 椭球高度</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#alternateLandPoint()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 alternate_land_point 字段")
public record AlternateLandPoint(
        /** 经度 */
        Double longitude,

        /** 纬度 */
        Double latitude,

        /** 安全高度（备降转移高） */
        Double safeLandHeight,

        /** 是否设置备降点（0=未设置, 1=已设置） */
        Integer isConfigured,

        /** 椭球高度 */
        Double height
) {
}
