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

import com.fasterxml.jackson.annotation.JsonInclude;
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

/**
 * DJI Cloud API 的 JSON 编解码基础设施（纯 Jackson 工具，不含协议解析逻辑）。
 *
 * <p>本类仅负责 {@link ObjectMapper} 的配置与通用 JSON 编解码方法，不包含任何
 * DJI 协议知识（信封结构、字段名、嵌套路径等）。协议信封解析逻辑由各信封类自行实现：
 * <ul>
 *   <li>MQTT 信封 → {@link DjiMessage#parse(String, Class)} /
 *       {@link DjiMessage#extractMethod(String)} 等</li>
 *   <li>WebSocket 信封 → {@link ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage#parse(String, Class)} /
 *       {@link ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage#extractBizCode(String)}</li>
 *   <li>HTTP 信封 → {@link ltd.cdmi.dji.cloudapi.sdk.http.HttpResponseEnvelope#parse(String, Class)}</li>
 * </ul>
 *
 * <p>ObjectMapper 配置：
 * <ul>
 *   <li>{@code FAIL_ON_UNKNOWN_PROPERTIES=false} — 兼容协议字段增量</li>
 *   <li>{@link PropertyNamingStrategies#SNAKE_CASE} — DJI JSON 使用 snake_case
 *       （如 {@code mode_code}、{@code position_state}），Java record 字段使用
 *       camelCase（如 {@code modeCode}、{@code positionState}），配置命名策略
 *       后双向匹配，避免反序列化字段全 null</li>
 *   <li>{@link JsonInclude.Include#NON_NULL NON_NULL} — 序列化时忽略 null 字段。
 *       DJI 协议中字段缺失与 null 语义一致（均为「无值」），真机推送的 JSON 中
 *       不存在的字段是缺失的（非 {@code "field":null}）。此配置支持「部分字段构造」
 *       场景——如机场 OSD 分多条推送，每条只含部分字段，null 字段自动忽略不输出</li>
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
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
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
     * 将 JSON 字符串解析为 {@link JsonNode} 树。
     *
     * <p>供信封类（{@link DjiMessage}、{@link ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage}、
     * {@link ltd.cdmi.dji.cloudapi.sdk.http.HttpResponseEnvelope}）的静态工厂方法使用，
     * 先解析为树再提取信封字段与 data 子节点。
     *
     * @param json JSON 字符串
     * @return JSON 树
     * @throws IllegalStateException 如果 JSON 解析失败
     */
    public static JsonNode readTree(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 {@link JsonNode} 转为 Java 对象。
     *
     * <p>供信封类的静态工厂方法使用，将 {@code data} 子节点反序列化为指定 POJO 类型。
     *
     * @param node JSON 节点
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 反序列化对象
     * @throws IllegalStateException 如果转换失败
     */
    public static <T> T treeToValue(JsonNode node, Class<T> type) {
        try {
            return MAPPER.treeToValue(node, type);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 节点转换失败: " + e.getMessage(), e);
        }
    }
}
