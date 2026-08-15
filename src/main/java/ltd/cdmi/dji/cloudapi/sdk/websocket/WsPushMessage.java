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

package ltd.cdmi.dji.cloudapi.sdk.websocket;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Pilot 上云 WebSocket 推送消息信封结构（泛型化）。
 *
 * <p>hivemind 通过 WebSocket 向 Pilot 推送消息，所有推送消息共用此信封：
 * <pre>{@code
 * {
 *   "biz_code": "map_element_create",
 *   "version": "1.0",
 *   "timestamp": 1700000000000,
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p>{@code biz_code} 标识消息类型（见 {@link WsBizCode}），{@code data} 的具体结构
 * 由 {@code biz_code} 决定。泛型参数 {@code <T>} 让调用方在解析时指定 {@code data}
 * 的 POJO 类型，编译期类型安全——与 MQTT 通道的
 * {@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage} 对称。
 *
 * <p><b>使用方式</b>：用 {@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec#parseWs}
 * 一步完成信封解析 + data 反序列化：
 * <pre>{@code
 * var msg = MessageCodec.parseWs(payload, DeviceOsdPushData.class);
 * msg.data().host().latitude();   // 类型安全，无 cast，无 instanceof
 * }</pre>
 *
 * <p>SDK 已为高频 biz_code 提供 POJO（{@code websocket.data} 包）：
 * <ul>
 *   <li>{@code device_osd} → {@link ltd.cdmi.dji.cloudapi.sdk.websocket.data.DeviceOsdPushData}</li>
 *   <li>{@code map_group_refresh} → {@link ltd.cdmi.dji.cloudapi.sdk.websocket.data.MapGroupRefreshData}</li>
 *   <li>{@code map_element_create/update/delete} → {@link ltd.cdmi.dji.cloudapi.sdk.websocket.data.MapElementPushData}</li>
 *   <li>{@code device_online/offline/update_topo} → {@link ltd.cdmi.dji.cloudapi.sdk.websocket.data.WsEmptyData}</li>
 * </ul>
 * 调用方也可传自定义 POJO，由 Jackson SNAKE_CASE 策略自动映射。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html">
 * DJI Pilot WebSocket 消息发布</a>
 *
 * @param <T>       data 字段的 POJO 类型，由 parseWs 调用方通过 Class&lt;T&gt; 指定
 * @param bizCode   消息类型，见 {@link WsBizCode}
 * @param version   协议版本（如 "1.0"）
 * @param timestamp 时间戳（毫秒）
 * @param data      数据负载（反序列化后的 POJO，JSON 无 data 字段时为 null）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html")
@Verified(basis = "simulator WsMessageHandler.java 已对接 hivemind 验证的消息信封字段定义")
public record WsPushMessage<T>(
        String bizCode,
        String version,
        long timestamp,
        T data
) {
}
