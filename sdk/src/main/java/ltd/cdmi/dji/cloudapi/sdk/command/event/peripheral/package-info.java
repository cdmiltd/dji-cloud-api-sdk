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

/**
 * events 通道空域安全类事件 POJO（O4 地面站）。
 *
 * <p>本包含 RID / ADS-B 广播信息上报事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral.RidInfosData RidInfosData}
 *       — rid_infos RID 广播信息上报（含嵌套 {@code RidTarget}）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral.AdsbInfosData AdsbInfosData}
 *       — adsb_infos ADS-B 广播信息上报（含嵌套 {@code AdsbTarget}）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral.Gnss Gnss}
 *       — 两事件共享的 GNSS 定位子结构</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/airspace-safety.html">
 * DJI O4 地面站 空域安全</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral;
