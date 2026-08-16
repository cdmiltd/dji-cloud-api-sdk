package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import com.fasterxml.jackson.annotation.JsonProperty;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 机场备用电池信息（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code switch} — 备用电池开关（0=关闭, 1=开启）</li>
 *   <li>{@code voltage} — 备用电池电压（mV）</li>
 *   <li>{@code temperature} — 备用电池温度（°C）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#backupBattery()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 backup_battery 字段")
public record BackupBattery(
        /** 备用电池开关（0=关闭, 1=开启） */
        @JsonProperty("switch") Integer switchState,

        /** 备用电池电压（mV，关闭时为 0） */
        Integer voltage,

        /** 备用电池温度（°C） */
        Double temperature
) {
}
