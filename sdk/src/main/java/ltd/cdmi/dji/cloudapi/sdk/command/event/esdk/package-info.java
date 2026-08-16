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
 * events 通道 ESDK（Enterprise SDK）类事件 POJO。
 *
 * <p>本包含 1 个 ESDK 相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.esdk.CustomDataFromEsdkData CustomDataFromEsdkData}
 *       — custom_data_transmission_from_esdk ESDK 自定义消息接收（need_reply=0 @Inferred，
 *       DJI 文档未标注 need_reply 值）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/esdk-transmit-custom-data.html">
 * DJI Dock3 ESDK 自定义数据传输</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.esdk;
