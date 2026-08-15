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

package ltd.cdmi.dji.cloudapi.sdk.command.event.psdk;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * custom_data_transmission_from_psdk 事件 data。
 *
 * <p>对应 DJI Cloud API {@code custom_data_transmission_from_psdk} 事件（events 通道）的 data。
 * 用于 PSDK 到云的自定义消息推送，data 含 value 文本（长度 < 256）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#CUSTOM_DATA_TRANSMISSION_FROM_PSDK}
 *
 * <p>字段依据：simulator {@code PsdkSimulator.triggerCustomDataFromPsdk}（L430-L446）
 * 已对接 hivemind 验证。
 *
 * <p>{@code need_reply} 值未在 DJI 文档中标注，simulator 遵循现有 PSDK 事件设置使用
 * {@code need_reply=0}（单向通知），标 @Inferred 待真机验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk-transmit-custom-data.html">
 * DJI Dock3 PSDK 自定义数据传输</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk-transmit-custom-data.html")
@Verified(basis = "simulator PsdkSimulator.triggerCustomDataFromPsdk L430-L446 已对接 hivemind 验证")
@Inferred(
    reason = "need_reply 值未在 DJI 文档中标注，simulator 遵循现有 PSDK 事件设置使用 need_reply=0",
    verifyPoint = "真机验证 need_reply 是否为 0（单向通知）"
)
public record CustomDataFromPsdkData(
    /** 数据内容（长度 < 256） */
    String value
) {}
