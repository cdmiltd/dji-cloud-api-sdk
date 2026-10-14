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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * GNSS 定位信息（{@code gnss} struct）。
 *
 * <p>{@code rid_infos} / {@code adsb_infos} 两事件的 {@code gnss} 子结构共享本 record。
 *
 * <p><b>关键</b>：{@code time} 字段为**字符串** {@code YYYYMMDD-HHmmss}（非时间戳），
 * 不可当数值时间戳反序列化。
 *
 * <p>字段依据：DJI O4 地面站 airspace-safety 文档——
 * {@code gnss{ engine, flag, time(String YYYYMMDD-HHmmss), lat, lon, alt, satellite, hdop }}。
 *
 * <p>文档未逐项标注子字段类型 → 标 @Inferred。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html">
 * DJI O4 地面站 空域安全</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html")
@Inferred(
    reason = "DJI O4 airspace-safety 文档 gnss 子字段未逐项标注类型，按字段语义推断（time 明确为字符串 YYYYMMDD-HHmmss）",
    verifyPoint = "真机核对 engine/flag/satellite/hdop 的实际类型与 time 字符串格式"
)
public record Gnss(
    /** GNSS 引擎标识 */
    Integer engine,
    /** 定位质量标志 */
    Integer flag,
    /** 时间字符串（格式 YYYYMMDD-HHmmss，非时间戳） */
    String time,
    /** 纬度 */
    Double lat,
    /** 经度 */
    Double lon,
    /** 高度 */
    Double alt,
    /** 卫星数量 */
    Integer satellite,
    /** 水平精度因子 HDOP */
    Double hdop
) {}
