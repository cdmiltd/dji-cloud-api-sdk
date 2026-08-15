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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.nio.file.Path;

import ltd.cdmi.dji.cloudapi.sdk.http.HttpResponseEnvelope;
import ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage;

/**
 * DJI Cloud API 消息的 JSON 编解码工具。
 *
 * <p>基于 Jackson {@link ObjectMapper}，统一处理 DJI MQTT 消息的序列化、反序列化
 * 与常用字段（method / tid / bid / result / data）的提取。ObjectMapper 配置：
 * <ul>
 *   <li>{@code FAIL_ON_UNKNOWN_PROPERTIES=false} — 兼容协议字段增量</li>
 *   <li>{@link PropertyNamingStrategies#SNAKE_CASE} — DJI JSON 使用 snake_case
 *       （如 {@code mode_code}、{@code position_state}），Java record 字段使用
 *       camelCase（如 {@code modeCode}、{@code positionState}），配置命名策略
 *       后双向匹配，避免反序列化字段全 null</li>
 *   <li>{@link SimpleModule Path 模块} — 将 {@link Path} 序列化为纯路径字符串
 *       （{@link Path#toString()}），反序列化时通过 {@link Path#of(String, String...)}
 *       重建。Jackson 默认将 Path 序列化为 URI 形式且无反序列化器，注册本模块后
 *       支持 JSON 往返闭环</li>
 * </ul>
 *
 * <p>线程安全：{@link ObjectMapper} 在配置完成后线程安全，本类以静态单例持有，
 * 方法均为静态方法，可被模拟器与 hivemind 平台直接调用。
 */
public final class MessageCodec {

    /** 内部持有的 ObjectMapper 实例（配置后线程安全） */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .registerModule(pathModule());

    private MessageCodec() {
    }

    /**
     * 注册 {@link Path} 的 Jackson 序列化/反序列化模块。
     *
     * <p>Jackson 默认将 Path 序列化为 URI 形式（如 {@code file:///D:/.../dji-capture}），
     * 且无反序列化器。本模块将 Path 序列化为纯路径字符串（{@link Path#toString()}），
     * 反序列化时通过 {@link Path#of(String, String...)} 重建，支持 JSON 往返闭环。
     *
     * @return Path 序列化模块
     */
    private static SimpleModule pathModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Path.class, new ToStringSerializer());
        module.addDeserializer(Path.class, new JsonDeserializer<>() {
            @Override
            public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return Path.of(p.getValueAsString());
            }
        });
        return module;
    }

    /**
     * 序列化为 JSON 字符串。
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 反序列化为 Java 对象。
     *
     * @param json JSON 字符串
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 反序列化对象
     */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 反序列化为泛型类型。
     *
     * @param json    JSON 字符串
     * @param typeRef 泛型类型引用
     * @param <T>     目标类型
     * @return 反序列化对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 JSON 中提取 method 字段。
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
            JsonNode node = MAPPER.readTree(json);
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
            JsonNode node = MAPPER.readTree(json);
            JsonNode data = node.path("data");
            if (data.isMissingNode()) {
                return null;
            }
            return MAPPER.treeToValue(data, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析 DJI Cloud API 消息为类型安全信封。
     *
     * <p>一次调用完成信封解析（{@code method}/{@code tid}/{@code bid}）与
     * {@code data} 反序列化，返回 {@link DjiMessage}。调用方指定 POJO 类型，
     * {@code data} 字段自动反序列化为该类型——<b>编译期类型安全，无需 cast</b>。
     *
     * <p>适用于全部 5 个 MQTT 通道（services / drc / events / requests / status）。
     * {@code bid} 仅 service/event 通道携带，其余通道为 null。
     *
     * <p><b>使用示例</b>：
     * <pre>{@code
     * var msg = MessageCodec.parse(payload, FlyToPointRequest.class);
     * msg.data().flyToId();   // 类型安全，无需 cast
     * msg.tid();               // 事务 ID
     * msg.method();            // 方法名
     * }</pre>
     *
     * <p>典型调用方在每个 switch case 中调用一次 {@code parse}，
     * 每个 case 只需 1 行样板代码：
     * <pre>{@code
     * switch (ServiceMethod.fromMethodName(method).orElseThrow()) {
     *     case FLY_TO_POINT -> {
     *         var msg = MessageCodec.parse(payload, FlyToPointRequest.class);
     *         msg.data().flyToId();   // 业务逻辑
     *     }
     *     case COVER_OPEN -> {
     *         var msg = MessageCodec.parse(payload, NoParameterRequest.class);
     *         // 无参数 POJO，直接处理
     *     }
     * }
     * }</pre>
     *
     * @param payload MQTT 消息的 JSON 字符串
     * @param type     {@code data} 字段的目标 POJO 类型
     * @param <T>      POJO 类型，编译期由 {@code Class<T>} 推断
     * @return 类型安全信封 {@link DjiMessage}
     * @throws IllegalStateException 如果 JSON 解析或 {@code data} 反序列化失败
     */
    public static <T> DjiMessage<T> parse(String payload, Class<T> type) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            String method = textOrNull(root, "method");
            String tid = textOrNull(root, "tid");
            String bid = textOrNull(root, "bid");
            JsonNode dataNode = root.path("data");
            T data = dataNode.isMissingNode() || dataNode.isNull() ? null : MAPPER.treeToValue(dataNode, type);
            return new DjiMessage<>(method, tid, bid, data);
        } catch (Exception e) {
            throw new IllegalStateException("消息解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 WebSocket 推送 JSON 中提取 biz_code 字段（消息类型）。
     *
     * <p>用于调用方在 switch 路由前先 peek biz_code，再决定传哪个 POJO 给
     * {@link #parseWs}——与 MQTT 通道的 {@link #extractMethod} 对称。
     *
     * @param json WebSocket 推送消息的 JSON 字符串
     * @return biz_code 值，不存在或解析失败返回 null
     */
    public static String extractBizCode(String json) {
        return extractText(json, "biz_code");
    }

    /**
     * 解析 DJI Pilot WebSocket 推送消息为类型安全信封。
     *
     * <p>一次调用完成信封解析（{@code biz_code}/{@code version}/{@code timestamp}）与
     * {@code data} 反序列化，返回 {@link WsPushMessage}。调用方指定 POJO 类型，
     * {@code data} 字段自动反序列化为该类型——<b>编译期类型安全，无需 cast</b>。
     *
     * <p>与 MQTT 通道的 {@link #parse} 对称，适用于全部 8 个 biz_code
     * （见 {@link ltd.cdmi.dji.cloudapi.sdk.websocket.WsBizCode}）。SDK 已为高频 biz_code
     * 提供 POJO（{@code websocket.data} 包），调用方也可传自定义 POJO。
     *
     * <p><b>使用示例</b>：
     * <pre>{@code
     * var msg = MessageCodec.parseWs(payload, DeviceOsdPushData.class);
     * msg.data().host().latitude();   // 类型安全，无 cast
     * }</pre>
     *
     * <p>典型调用方先用 {@link #extractBizCode} peek 类型再 switch 分发，每个 case 1 行
     * {@code parseWs}，与 MQTT 的 {@code extractMethod} + {@code parse} 模式完全统一：
     * <pre>{@code
     * String bizCode = MessageCodec.extractBizCode(payload);
     * switch (WsBizCode.fromCode(bizCode).orElse(null)) {
     *     case DEVICE_OSD -> {
     *         var msg = MessageCodec.parseWs(payload, DeviceOsdPushData.class);
     *         msg.data().host().latitude();
     *     }
     *     case null -> log.warn("未知 biz_code: {}", bizCode);
     * }
     * }</pre>
     *
     * @param payload WebSocket 推送消息的 JSON 字符串
     * @param type    {@code data} 字段的目标 POJO 类型
     * @param <T>     POJO 类型，编译期由 {@code Class<T>} 推断
     * @return 类型安全信封 {@link WsPushMessage}
     * @throws IllegalStateException 如果 JSON 解析或 {@code data} 反序列化失败
     */
    public static <T> WsPushMessage<T> parseWs(String payload, Class<T> type) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            String bizCode = textOrNull(root, "biz_code");
            String version = textOrNull(root, "version");
            long timestamp = root.path("timestamp").asLong();
            JsonNode dataNode = root.path("data");
            T data = dataNode.isMissingNode() || dataNode.isNull() ? null : MAPPER.treeToValue(dataNode, type);
            return new WsPushMessage<>(bizCode, version, timestamp, data);
        } catch (Exception e) {
            throw new IllegalStateException("WebSocket 消息解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 DJI Cloud API HTTP 响应为类型安全信封。
     *
     * <p>一次调用完成信封解析（{@code code}/{@code message}）与
     * {@code data} 反序列化，返回 {@link HttpResponseEnvelope}。调用方指定 POJO 类型，
     * {@code data} 字段自动反序列化为该类型——<b>编译期类型安全，无需 cast</b>。
     *
     * <p>与 MQTT 通道的 {@link #parse} 和 WebSocket 通道的 {@link #parseWs} 对称。
     * DJI HTTP API 响应统一信封为 {@code {"code":0, "message":"...", "data":{...}}}，
     * {@code code} 非 0 时 {@code data} 可能为 null。
     *
     * <p><b>修复 fromJson Bug</b>：直接用 {@link #fromJson} 反序列化 HTTP 响应体
     * 会将 {@code code}/{@code message}/{@code data} 三字段当作 POJO 的顶层字段，
     * 而业务数据实际在 {@code data} 内层，导致 POJO 字段全 null。{@code parseHttp}
     * 正确提取 {@code data} 子节点再反序列化为指定 POJO 类型。
     *
     * <p><b>使用示例</b>：
     * <pre>{@code
     * var resp = MessageCodec.parseHttp(httpBody, StsCredentials.class);
     * if (resp.code() == 0) {
     *     resp.data().bucket();   // 类型安全，无 cast
     * } else {
     *     log.error("HTTP 错误: {} - {}", resp.code(), resp.message());
     * }
     * }</pre>
     *
     * @param payload HTTP 响应体 JSON 字符串
     * @param type    {@code data} 字段的目标 POJO 类型
     * @param <T>     POJO 类型，编译期由 {@code Class<T>} 推断
     * @return 类型安全信封 {@link HttpResponseEnvelope}
     * @throws IllegalStateException 如果 JSON 解析或 {@code data} 反序列化失败
     */
    public static <T> HttpResponseEnvelope<T> parseHttp(String payload, Class<T> type) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            int code = root.path("code").asInt(-1);
            String message = textOrNull(root, "message");
            JsonNode dataNode = root.path("data");
            T data = dataNode.isMissingNode() || dataNode.isNull() ? null : MAPPER.treeToValue(dataNode, type);
            return new HttpResponseEnvelope<>(code, message, data);
        } catch (Exception e) {
            throw new IllegalStateException("HTTP 响应解析失败: " + e.getMessage(), e);
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
            JsonNode node = MAPPER.readTree(json);
            JsonNode value = node.path(field);
            return value.isMissingNode() ? null : value.asText();
        } catch (Exception e) {
            return null;
        }
    }
}
