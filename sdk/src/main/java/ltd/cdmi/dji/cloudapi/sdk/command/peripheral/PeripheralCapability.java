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

package ltd.cdmi.dji.cloudapi.sdk.command.peripheral;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 外设能力集（O4 地面站 state peripheral_list 数组元素）。
 *
 * <p>DJI 文档字段：{@code sn} + {@code rid}/{@code adsb}/{@code dock_control} 三组能力，
 * 分别见 {@link RidCapability}/{@link AdsbCapability}/{@link DockControlCapability}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.GroundStationState#peripheralList()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 peripheral_list 数组元素字段（sn/rid/adsb/dock_control）")
public record PeripheralCapability(
        /** 外设序列号 */
        String sn,

        /** RID 能力 */
        RidCapability rid,

        /** ADSB 能力 */
        AdsbCapability adsb,

        /** 机场控制能力 */
        DockControlCapability dockControl
) {
}
