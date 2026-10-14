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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 外设接入登记信息（O4 地面站 state peripherals_info 数组元素）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code device_sn} — 外设序列号</li>
 *   <li>{@code name} — 外设名称</li>
 *   <li>{@code port} — 接入端口（0=LAN, 1=WAN），见
 *   {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.PeripheralPort}</li>
 *   <li>{@code type} — 外设类型（0=其它, 1=相机, 2=DAA, 3=雷达, 4=电源, 5=第三方机库, 6=边缘计算, 7=RID），见
 *   {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.PeripheralType}</li>
 *   <li>{@code ip_addr} — 外设 IP 地址</li>
 *   <li>{@code need_auth} — 是否需要鉴权</li>
 *   <li>{@code main_device} — 是否为主设备</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.GroundStationState#peripheralsInfo()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 peripherals_info 数组元素字段名")
public record PeripheralInfo(
        /** 外设序列号 */
        String deviceSn,

        /** 外设名称 */
        String name,

        /** 接入端口（0=LAN, 1=WAN） */
        Integer port,

        /** 外设类型（0=其它, 1=相机, 2=DAA, 3=雷达, 4=电源, 5=第三方机库, 6=边缘计算, 7=RID） */
        Integer type,

        /** 外设 IP 地址 */
        String ipAddr,

        /**
         * 是否需要鉴权（bool）。
         * <p>{@code @Inferred}：文档标注 bool，SDK 以 Integer（0/1）承载。
         */
        @Inferred(
                reason = "DJI O4 页 need_auth 类型未逐项标注，按 bool 语义用 Integer(0/1) 承载",
                verifyPoint = "待 O4 页原文核实 need_auth 的 wire 取值形式"
        )
        Integer needAuth,

        /**
         * 是否为主设备（bool）。
         * <p>{@code @Inferred}：文档标注 bool，SDK 以 Integer（0/1）承载。
         */
        @Inferred(
                reason = "DJI O4 页 main_device 类型未逐项标注，按 bool 语义用 Integer(0/1) 承载",
                verifyPoint = "待 O4 页原文核实 main_device 的 wire 取值形式"
        )
        Integer mainDevice
) {
}
