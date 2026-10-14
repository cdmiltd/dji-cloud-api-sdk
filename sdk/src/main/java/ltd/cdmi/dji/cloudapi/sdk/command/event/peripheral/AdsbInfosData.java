// Copyright (C) 2026 CDMI.LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * adsb_infos 事件 data（events 通道，设备→云）。
 *
 * <p>对应 DJI Cloud API {@code adsb_infos} 事件，上报 ADS-B 广播信息。
 * {@code need_reply=0}（单向通知，见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#ADSB_INFOS}）。
 *
 * <p>字段依据：DJI O4 地面站 airspace-safety 文档——
 * {@code capacity_percent / device_sn / power_supply / gnss / adsb[]}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html">
 * DJI O4 地面站 空域安全</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html")
@Verified(basis = "DJI O4 ground-station airspace-safety.html adsb_infos Data 表（capacity_percent/device_sn/power_supply/gnss/adsb[]）")
public record AdsbInfosData(
    /** 广播容量占用百分比（0-100） */
    Integer capacityPercent,
    /** 设备序列号 */
    String deviceSn,
    /** 供电状态 */
    Integer powerSupply,
    /** GNSS 定位信息 */
    Gnss gnss,
    /** ADS-B 目标列表 */
    List<AdsbTarget> adsb
) {
    /**
     * ADS-B 单个广播目标。
     *
     * <p>文档对 {@code height}/{@code speed}/{@code heading}/{@code uprate} 标注 int。
     *
     * @param icao    ICAO 地址
     * @param callsign 呼号
     * @param height  高度（int）
     * @param lat     目标纬度
     * @param lon     目标经度
     * @param speed   速度（int）
     * @param heading 航向（int）
     * @param uprate  垂直速度（int）
     */
    public record AdsbTarget(
        String icao,
        String callsign,
        Integer height,
        Double lat,
        Double lon,
        Integer speed,
        Integer heading,
        Integer uprate
    ) {}
}
