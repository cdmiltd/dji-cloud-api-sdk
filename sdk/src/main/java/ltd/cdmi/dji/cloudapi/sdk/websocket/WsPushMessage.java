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

import com.fasterxml.jackson.databind.JsonNode;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

/**
 * DJI Pilot 上云 WebSocket 推送消息信封结构（泛型化）与解析逻辑。
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
 * <p><b>使用方式</b>：先用 {@link #extractBizCode(String)} peek 类型再 switch 分发，
 * 每个 case 1 行 {@link #parse(String, Class)}：
 * <pre>{@code
 * String bizCode = WsPushMessage.extractBizCode(payload);
 * switch (WsBizCode.fromCode(bizCode).orElse(null)) {
 *     case DEVICE_OSD -> {
 *         var msg = WsPushMessage.parse(payload, DeviceOsdPushData.class);
 *         msg.data().host().latitude();   // 类型安全，无 cast，无 instanceof
 *     }
 *     case null -> log.warn("未知 biz_code: {}", bizCode);
 * }
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
 * @param <T>       data 字段的 POJO 类型，由 parse 调用方通过 Class&lt;T&gt; 指定
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

    /**
     * 解析 DJI Pilot WebSocket 推送消息为类型安全信封。
     *
     * <p>一次调用完成信封解析（{@code biz_code}/{@code version}/{@code timestamp}）与
     * {@code data} 反序列化，返回 {@link WsPushMessage}。调用方指定 POJO 类型，
     * {@code data} 字段自动反序列化为该类型——<b>编译期类型安全，无需 cast</b>。
     *
     * <p>与 MQTT 通道的 {@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage#parse} 对称，
     * 适用于全部 8 个 biz_code（见 {@link WsBizCode}）。
     *
     * @param payload WebSocket 推送消息的 JSON 字符串
     * @param type    {@code data} 字段的目标 POJO 类型
     * @param <T>     POJO 类型，编译期由 {@code Class<T>} 推断
     * @return 类型安全信封 {@link WsPushMessage}
     * @throws IllegalStateException 如果 JSON 解析或 {@code data} 反序列化失败
     */
    public static <T> WsPushMessage<T> parse(String payload, Class<T> type) {
        try {
            JsonNode root = MessageCodec.readTree(payload);
            String bizCode = textOrNull(root, "biz_code");
            String version = textOrNull(root, "version");
            long timestamp = root.path("timestamp").asLong();
            JsonNode dataNode = root.path("data");
            T data = dataNode.isMissingNode() || dataNode.isNull() ? null : MessageCodec.treeToValue(dataNode, type);
            return new WsPushMessage<>(bizCode, version, timestamp, data);
        } catch (Exception e) {
            throw new IllegalStateException("WebSocket 消息解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 WebSocket 推送 JSON 中提取 biz_code 字段（消息类型）。
     *
     * <p>用于调用方在 switch 路由前先 peek biz_code，再决定传哪个 POJO 给
     * {@link #parse}——与 MQTT 通道的
     * {@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage#extractMethod(String)} 对称。
     *
     * @param json WebSocket 推送消息的 JSON 字符串
     * @return biz_code 值，不存在或解析失败返回 null
     */
    public static String extractBizCode(String json) {
        try {
            JsonNode node = MessageCodec.readTree(json);
            JsonNode value = node.path("biz_code");
            return value.isMissingNode() ? null : value.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? null : value.asText();
    }
}
