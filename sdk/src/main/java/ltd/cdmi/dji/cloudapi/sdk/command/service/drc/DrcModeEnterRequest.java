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

package ltd.cdmi.dji.cloudapi.sdk.command.service.drc;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drc_mode_enter 指令请求 data。
 *
 * <p>进入 DRC（设备远程控制）模式。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 *
 * <p>字段语义（依据 DJI 官方文档 drc.md 字段表）：
 * <ul>
 *   <li>{@code mqtt_broker}：DRC 专用 MQTT 连接凭证。调试器等工具下发的 drc_mode_enter
 *       可能不携带该字段（设备回退主连接），故不强制校验，接收端需自行判空
 *       （模拟器 TC-DRC-056/072 已处理）</li>
 *   <li>{@code hsi_frequency}：HIS 频率（Hz），可空（接收端仅作上报频率参考）</li>
 *   <li>{@code osd_frequency}：OSD 频率（Hz，1~30），可空</li>
 * </ul>
 *
 * <p>历史问题：v1.16.1.2 及之前全部字段 requireNonNull 强制必填，平台/工具下发缺省
 * 字段的合法报文时反序列化失败，导致设备端跳过 mqtt_broker 处理。
 *
 * @see DrcMqttBroker
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/drc.html")
@Verified(basis = "DJI 文档字段表（rc-pro/drc.md、dock2 110.drc.md）：drc_mode_enter data 含 mqtt_broker/hsi_frequency/osd_frequency")
public record DrcModeEnterRequest(
    DrcMqttBroker mqttBroker,
    Integer hsiFrequency,
    Integer osdFrequency
) {
}
