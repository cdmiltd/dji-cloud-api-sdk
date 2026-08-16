package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 子设备状态（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code device_sn} — 子设备序列号（SN）</li>
 *   <li>{@code device_model_key} — 子设备枚举值，格式为 {domain-type-subtype}</li>
 *   <li>{@code device_online_status} — 飞行器开机状态（0=关机, 1=开机）</li>
 *   <li>{@code device_paired} — 飞行器是否与机场对频（0=未对频, 1=已对频）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#subDevice()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 sub_device 字段")
public record SubDevice(
        /** 子设备序列号（SN） */
        String deviceSn,

        /** 子设备枚举值，格式为 {domain-type-subtype} */
        String deviceModelKey,

        /** 飞行器开机状态（0=关机, 1=开机） */
        Integer deviceOnlineStatus,

        /** 飞行器是否与机场对频（0=未对频, 1=已对频） */
        Integer devicePaired
) {
}
