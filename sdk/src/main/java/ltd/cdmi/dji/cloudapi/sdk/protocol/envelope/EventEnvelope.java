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
import ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod;

/**
 * DJI Cloud API 事件消息信封结构（设备→云）。
 *
 * <p>用于 events 通道（设备→云），平台通过 events_reply 下行应答。
 * 字段与 {@link RequestEnvelope} 一致，{@code data} 结构由 method 决定。
 * {@code need_reply} 表示是否需要平台回复（1=需要，0=单向通知），
 * {@code gateway} 为网关 SN（Dock 场景下为机场 SN）。
 *
 * <p><b>推荐使用 {@link #of(String, String, long, EventMethod, Object, String)} 静态工厂方法</b>，
 * 从 {@link EventMethod#needReply()} 自动获取 {@code needReply} 值，
 * 调用方无需查阅 DJI 文档即可正确构造事件消息。
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "flighttask_progress",
 *   "data": { ... },
 *   "need_reply": 1,
 *   "gateway": "1581F4BHD232Q00BP0HN"
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/thing/model/events.html">Dock3 events 事件</a>
 *
 * @param tid       事务 ID
 * @param bid       批次 ID
 * @param timestamp 时间戳（毫秒）
 * @param method    事件方法名，见 {@link EventMethod}
 * @param data      事件数据负载，具体结构由 method 决定
 * @param needReply 是否需要平台回复（1=需要 events_reply，0=单向通知）
 * @param gateway   网关 SN（Dock 场景下为机场 SN，允许 null）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/thing/model/events.html")
@Verified(basis = "DJI Cloud API 官方文档事件消息信封字段定义，need_reply 取值与 EventMethod 枚举绑定")
public record EventEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        Object data,
        int needReply,
        String gateway
) {

    /**
     * 便捷构造：从 {@link EventMethod} 自动获取 {@code needReply} 值。
     *
     * <p>调用方只需选择事件类型，无需查阅 DJI 文档确认 {@code need_reply} 取值。
     *
     * @param tid       事务 ID
     * @param bid       批次 ID
     * @param timestamp 时间戳（毫秒）
     * @param method    事件方法枚举
     * @param data      事件数据负载
     * @param gateway   网关 SN（允许 null）
     * @return 填充了 {@code needReply} 的 {@link EventEnvelope}
     */
    public static EventEnvelope of(String tid, String bid, long timestamp,
                                   EventMethod method, Object data, String gateway) {
        return new EventEnvelope(tid, bid, timestamp, method.methodName(), data,
                method.needReply(), gateway);
    }
}
