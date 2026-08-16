package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import com.fasterxml.jackson.annotation.JsonProperty;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 图传链路信息（Dock 与 Controller 共用）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code dongle_number} — 飞行器上 Dongle 数量</li>
 *   <li>{@code 4g_link_state} — 4G 链路连接状态（0=断开, 1=连接）</li>
 *   <li>{@code sdr_link_state} — SDR 链路连接状态（0=断开, 1=连接）</li>
 *   <li>{@code link_workmode} — 图传链路模式（0=SDR 模式, 1=4G 融合模式）</li>
 *   <li>{@code sdr_quality} — SDR 信号质量（0-5）</li>
 *   <li>{@code 4g_quality} — 总体 4G 信号质量（0-5）</li>
 *   <li>{@code 4g_uav_quality} — 天端 4G 信号质量（0-5）</li>
 *   <li>{@code 4g_gnd_quality} — 地端 4G 信号质量（0-5）</li>
 *   <li>{@code sdr_freq_band} — SDR 频段</li>
 *   <li>{@code 4g_freq_band} — 4G 频段</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#wirelessLink()
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.RcOsd#wirelessLink()
 */
@Verified(basis = "DJI Cloud API 官方文档机场/遥控器设备属性 wireless_link 字段")
public record WirelessLink(
        /** 飞行器上 Dongle 数量 */
        Integer dongleNumber,

        /** 4G 链路连接状态（0=断开, 1=连接） */
        @JsonProperty("4g_link_state") Integer fourGLinkState,

        /** SDR 链路连接状态（0=断开, 1=连接） */
        Integer sdrLinkState,

        /** 图传链路模式（0=SDR 模式, 1=4G 融合模式） */
        Integer linkWorkmode,

        /** SDR 信号质量（0-5） */
        Integer sdrQuality,

        /** 总体 4G 信号质量（0-5） */
        @JsonProperty("4g_quality") Integer fourGQuality,

        /** 天端 4G 信号质量（0-5） */
        @JsonProperty("4g_uav_quality") Integer fourGUavQuality,

        /** 地端 4G 信号质量（0-5） */
        @JsonProperty("4g_gnd_quality") Integer fourGGndQuality,

        /** SDR 频段 */
        Double sdrFreqBand,

        /** 4G 频段 */
        @JsonProperty("4g_freq_band") Double fourGFreqBand
) {
}
