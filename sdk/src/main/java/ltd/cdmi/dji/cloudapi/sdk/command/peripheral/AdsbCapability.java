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
 * ADSB 能力（O4 地面站 peripheral_list[].adsb）。
 *
 * <p>DJI 文档字段：{@code supported}（是否支持）/ {@code version}（版本）。
 *
 * @see PeripheralCapability#adsb()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 peripheral_list[].adsb 字段（supported/version）")
public record AdsbCapability(
        /**
         * 是否支持 ADSB（bool）。
         * <p>{@code @Inferred}：文档标注 bool，SDK 以 Integer（0/1）承载。
         */
        @Inferred(
                reason = "DJI O4 页 adsb.supported 类型未逐项标注，按 bool 语义用 Integer(0/1) 承载",
                verifyPoint = "待 O4 页原文核实 supported 的 wire 取值形式"
        )
        Integer supported,

        /** 版本 */
        String version
) {
}
