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

package ltd.cdmi.dji.cloudapi.sdk.protocol.envelope;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API 事件消息信封结构（设备→云）。
 *
 * <p>用于 events 通道（设备→云），平台通过 events_reply 下行应答。
 * 字段与 {@link RequestEnvelope} 一致，{@code data} 结构由 method 决定。
 * 事件消息额外可携带 {@code need_reply}（是否需要平台回复）与 {@code gateway}（网关 SN），
 * 此处仅保留协议核心字段，扩展字段由使用方按需补充。
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "flight_task_progress",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/thing/model/events.html">Dock3 events 事件</a>
 *
 * @param tid       事务 ID
 * @param bid       批次 ID
 * @param timestamp 时间戳（毫秒）
 * @param method    事件方法名，见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod}
 * @param data      事件数据负载，具体结构由 method 决定
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/thing/model/events.html")
@Verified(basis = "DJI Cloud API 官方文档事件消息信封字段定义")
public record EventEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        Object data
) {
}
