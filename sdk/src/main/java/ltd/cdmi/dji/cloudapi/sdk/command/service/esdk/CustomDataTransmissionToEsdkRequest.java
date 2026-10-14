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

package ltd.cdmi.dji.cloudapi.sdk.command.service.esdk;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code custom_data_transmission_to_esdk} 指令请求 data：向 ESDK 传输自定义数据。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/EsdkSimulator.java#L87-L92">
 * EsdkSimulator custom_data_transmission_to_esdk 处理逻辑</a> 已对接 hivemind 验证。
 *
 * <p>DJI Dock1「ESDK 互联互通」页显式列出 service
 * {@code custom_data_transmission_to_esdk}（down，{@code {value(text <256)}}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/esdk-transmit-custom-data.html">
 * DJI Dock3 ESDK 互联互通 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/esdk-transmit-custom-data.html")
@Verified(basis = "DJI Dock1「ESDK 互联互通」页官方文档显式列出 custom_data_transmission_to_esdk（{value(text <256)}）")
public record CustomDataTransmissionToEsdkRequest(
        /** 自定义数据内容（长度 < 256 字节） */
        String value
) {}
