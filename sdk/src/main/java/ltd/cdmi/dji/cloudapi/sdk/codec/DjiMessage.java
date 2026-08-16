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

package ltd.cdmi.dji.cloudapi.sdk.codec;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DJI Cloud API 消息的解析结果（类型安全信封）与 MQTT 消息解析逻辑。
 *
 * <p>本 record 封装 DJI MQTT 消息的信封字段（{@code method}/{@code tid}/{@code bid}/{@code data}），
 * 同时提供静态工厂方法 {@link #parse(String, Class)} 一步完成信封解析 + data 反序列化，
 * 以及 {@link #extractMethod(String)} 等字段提取方法用于 switch 路由前的 peek。
 *
 * <p>信封结构适用于全部 5 个 MQTT 通道（services / drc / events / requests / status）：
 * <pre>{@code
 * {
 *   "method": "fly_to_point",
 *   "tid": "uuid-transaction",
 *   "bid": "uuid-business",    // 仅 service/event 通道携带
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p><b>使用示例</b>——先 peek method 再 switch 分发，每个 case 1 行 {@code parse}：
 * <pre>{@code
 * String method = DjiMessage.extractMethod(payload);
 * switch (ServiceMethod.fromMethodName(method).orElseThrow()) {
 *     case FLY_TO_POINT -> {
 *         var msg = DjiMessage.parse(payload, FlyToPointRequest.class);
 *         msg.data().flyToId();               // 类型安全，无需 cast
 *         sendReply(msg.tid(), new NoOutputReply());
 *     }
 *     case COVER_OPEN -> {
 *         var msg = DjiMessage.parse(payload, NoParameterRequest.class);
 *         sendReply(msg.tid(), new NoOutputReply());
 *     }
 *     default -> log.warn("未处理: {}", method);
 * }
 * }</pre>
 *
 * @param method DJI 方法名（如 {@code "fly_to_point"}），JSON 中无此字段时为 null
 * @param tid    事务 ID，JSON 中无此字段时为 null
 * @param bid    业务 ID，仅 service/event 通道携带，其余为 null
 * @param data   反序列化后的 POJO，JSON 中无 data 字段时为 null
 * @param <T>    POJO 类型，由 {@code parse} 调用方通过 {@code Class<T>} 指定
 */
public record DjiMessage<T>(String method, String tid, String bid, T data) {

    /**
     * 解析 DJI Cloud API MQTT 消息为类型安全信封。
     *
     * <p>一次调用完成信封解析（{@code method}/{@code tid}/{@code bid}）与
     * {@code data} 反序列化，返回 {@link DjiMessage}。调用方指定 POJO 类型，
     * {@code data} 字段自动反序列化为该类型——<b>编译期类型安全，无需 cast</b>。
     *
     * <p>适用于全部 5 个 MQTT 通道（services / drc / events / requests / status）。
     * {@code bid} 仅 service/event 通道携带，其余通道为 null。
     *
     * @param payload MQTT 消息的 JSON 字符串
     * @param type    {@code data} 字段的目标 POJO 类型
     * @param <T>     POJO 类型，编译期由 {@code Class<T>} 推断
     * @return 类型安全信封 {@link DjiMessage}
     * @throws IllegalStateException 如果 JSON 解析或 {@code data} 反序列化失败
     */
    public static <T> DjiMessage<T> parse(String payload, Class<T> type) {
        try {
            JsonNode root = MessageCodec.readTree(payload);
            String method = textOrNull(root, "method");
            String tid = textOrNull(root, "tid");
            String bid = textOrNull(root, "bid");
            JsonNode dataNode = root.path("data");
            T data = dataNode.isMissingNode() || dataNode.isNull() ? null : MessageCodec.treeToValue(dataNode, type);
            return new DjiMessage<>(method, tid, bid, data);
        } catch (Exception e) {
            throw new IllegalStateException("消息解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 JSON 中提取 method 字段。
     *
     * <p>用于调用方在 switch 路由前先 peek method，再决定传哪个 POJO 给
     * {@link #parse}。
     *
     * @param json JSON 字符串
     * @return method 值，不存在或解析失败返回 null
     */
    public static String extractMethod(String json) {
        return extractText(json, "method");
    }

    /**
     * 从 JSON 中提取 tid 字段（事务 ID）。
     *
     * @param json JSON 字符串
     * @return tid 值，不存在或解析失败返回 null
     */
    public static String extractTid(String json) {
        return extractText(json, "tid");
    }

    /**
     * 从 JSON 中提取 bid 字段（业务 ID）。
     *
     * @param json JSON 字符串
     * @return bid 值，不存在或解析失败返回 null
     */
    public static String extractBid(String json) {
        return extractText(json, "bid");
    }

    /**
     * 从回复中提取 result 字段。
     *
     * <p>DJI 回复报文的 result 位于 {@code data.result}（如 {@code {"data":{"result":0}}}），
     * 非 0 代表错误。
     *
     * @param json JSON 字符串
     * @return result 值，不存在或解析失败返回 -1
     */
    public static int extractResult(String json) {
        try {
            JsonNode node = MessageCodec.readTree(json);
            return node.path("data").path("result").asInt(-1);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 从 JSON 中提取 data 字段并转为 Java 对象。
     *
     * @param json JSON 字符串
     * @return data 字段对应的 Java 对象（对象映射为 Map、数组映射为 List），不存在或解析失败返回 null
     */
    public static Object extractData(String json) {
        try {
            JsonNode node = MessageCodec.readTree(json);
            JsonNode data = node.path("data");
            if (data.isMissingNode()) {
                return null;
            }
            return MessageCodec.treeToValue(data, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从已解析的 JsonNode 中提取文本字段，缺失时返回 null。
     *
     * @param node  已解析的 JSON 节点
     * @param field 字段名
     * @return 文本值，字段不存在时返回 null
     */
    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? null : value.asText();
    }

    private static String extractText(String json, String field) {
        try {
            JsonNode node = MessageCodec.readTree(json);
            JsonNode value = node.path(field);
            return value.isMissingNode() ? null : value.asText();
        } catch (Exception e) {
            return null;
        }
    }
}
