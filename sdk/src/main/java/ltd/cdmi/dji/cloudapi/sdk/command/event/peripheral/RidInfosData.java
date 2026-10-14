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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * rid_infos 事件 data（events 通道，设备→云）。
 *
 * <p>对应 DJI Cloud API {@code rid_infos} 事件，上报 RID（远程识别）广播信息。
 * {@code need_reply=0}（单向通知，见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#RID_INFOS}）。
 *
 * <p>字段依据：DJI O4 地面站 airspace-safety 文档——
 * {@code capacity_percent / power_supply / device_sn / gnss / rid[]（单次最多 50 条）}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html">
 * DJI O4 地面站 空域安全</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html")
@Verified(basis = "DJI O4 ground-station airspace-safety.html rid_infos Data 表（capacity_percent/power_supply/device_sn/gnss/rid[]）")
public record RidInfosData(
    /** 广播容量占用百分比（0-100） */
    Integer capacityPercent,
    /** 供电状态 */
    Integer powerSupply,
    /** 设备序列号 */
    String deviceSn,
    /** GNSS 定位信息 */
    Gnss gnss,
    /** RID 目标列表（单次最多 50 条） */
    List<RidTarget> rid
) {
    /**
     * RID 单个广播目标。
     *
     * <p>文档仅对 {@code speed}/{@code uprate} 标注 float，其余子字段未逐项标注类型 → 标 @Inferred。
     *
     * @param osid     RID 唯一标识
     * @param rssi     信号强度
     * @param fre      广播频率
     * @param uatype   UA 类型
     * @param status   状态
     * @param heading  航向
     * @param speed    速度（float，Double 承载）
     * @param uprate   垂直速度（float，Double 承载）
     * @param lon      目标经度
     * @param lat      目标纬度
     * @param altgeo   几何高度
     * @param altbaro  气压高度
     * @param height   高度
     * @param opLon    操作员经度
     * @param opLat    操作员纬度
     * @param opAlt    操作员高度
     * @param uatime   UA 时间
     */
    @Inferred(
        reason = "DJI O4 airspace-safety 文档 rid[] 仅标注 speed/uprate 为 float，其余子字段未逐项标注类型，按其语义推断",
        verifyPoint = "真机核对 heading/lon/lat/altgeo/altbaro/height/op_*/uatime 的实际类型"
    )
    public record RidTarget(
        String osid,
        Integer rssi,
        Integer fre,
        Integer uatype,
        Integer status,
        Double heading,
        Double speed,
        Double uprate,
        Double lon,
        Double lat,
        Double altgeo,
        Double altbaro,
        Double height,
        Double opLon,
        Double opLat,
        Double opAlt,
        Long uatime
    ) {}
}
