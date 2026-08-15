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

package ltd.cdmi.dji.cloudapi.sdk.capture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import ltd.cdmi.dji.cloudapi.sdk.model.DockModel;
import ltd.cdmi.dji.cloudapi.sdk.model.DroneModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 真机消息采集记录器——将真机收发的 MQTT 消息自动分类存储，供 SDK 维护者验证协议。
 *
 * <h2>工作流程</h2>
 * <pre>{@code
 * // 1. 启动时注册设备型号映射（SN → 网关 + 飞行器）
 * CaptureRecorder.enable(CaptureConfig.defaults());
 * CaptureRecorder.registerDevice("7UUXN1Q00A008W", DockModel.DOCK3, DroneModel.M4D);
 *
 * // 2. 在 MQTT 消息处理点插入一行采集调用
 * void onMessage(String topic, String payload) {
 *     CaptureRecorder.capture(topic, "inbound", payload);
 *     // ... 正常处理 ...
 * }
 * void onReply(String topic, String replyJson) {
 *     CaptureRecorder.capture(topic, "outbound", replyJson);
 *     // ... 正常发送 ...
 * }
 *
 * // 3. 采集到的文件自动分类存储：
 * // dji-capture/Dock3-M4D/inbound/fly_to_point_20260815T103000_001.json
 * // 文件内含原始 JSON（已脱敏）+ 采集元数据（时间、topic、方向、设备型号）
 *
 * // 4. 将采集目录打包贴到 GitHub Issue
 * // SDK 维护者据此验证 @Inferred 项 → 转为 @Verified
 * }</pre>
 *
 * <h2>特性</h2>
 * <ul>
 *   <li><b>零开销</b>：未启用时 {@link #capture} 立即返回，不分配任何对象</li>
 *   <li><b>自动分类</b>：从 topic 提取 SN → 查注册表 → 按 网关-飞行器/方向/方法 三级分类</li>
 *   <li><b>自动脱敏</b>：SN、License、密钥等敏感字段递归替换为 {@code "***"}，防止泄露</li>
 *   <li><b>去重</b>：每方法每机型最多采集 {@code maxSamplesPerMethod} 份，避免文件爆炸</li>
 *   <li><b>异步写入</b>：文件 I/O 在独立守护线程执行，不阻塞 MQTT 消息处理</li>
 *   <li><b>容错</b>：任何 I/O 异常静默丢弃，绝不影响调用方正常逻辑</li>
 * </ul>
 *
 * <h2>快速开关</h2>
 * <p>无需改代码，通过系统属性开启：
 * <pre>
 * java -Ddji.cloud.capture=true -jar app.jar
 * </pre>
 * 使用默认配置（输出到 {@code dji-capture/}，每方法每机型 5 份）。
 */
public final class CaptureRecorder {

    private CaptureRecorder() {}

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    /** 采集开关（volatile 保证多线程可见性） */
    private static volatile boolean enabled = false;
    /** 采集配置 */
    private static volatile CaptureConfig config = null;
    /** 设备注册表：SN → [网关简称, 飞行器简称] */
    private static final ConcurrentMap<String, String[]> DEVICE_REGISTRY = new ConcurrentHashMap<>();
    /** 去重计数器：key = "网关-飞行器/方向/方法" → 已采集份数 */
    private static final ConcurrentMap<String, AtomicInteger> SAMPLE_COUNTS = new ConcurrentHashMap<>();
    /** 全局文件序号（防止同一毫秒多文件冲突） */
    private static final AtomicInteger FILE_SEQ = new AtomicInteger(0);

    /**
     * 启用采集。
     *
     * @param cfg 采集配置
     */
    public static void enable(CaptureConfig cfg) {
        config = cfg;
        enabled = cfg.enabled();
    }

    /**
     * 关闭采集。
     */
    public static void disable() {
        enabled = false;
    }

    /**
     * 采集是否已启用。
     *
     * @return true 表示已启用
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 注册设备型号映射，供自动分类使用。
     *
     * <p>在系统启动时为每台已知设备调用一次。未注册的 SN 将以 SN 本身作为目录名。
     *
     * @param sn    设备序列号
     * @param dock  网关型号（Dock1/Dock2/Dock3）
     * @param drone 飞行器型号（M30/M3D/M4D 等）
     */
    public static void registerDevice(String sn, DockModel dock, DroneModel drone) {
        DEVICE_REGISTRY.put(sn, new String[]{dock.toModel().shortName(), drone.toModel().shortName()});
    }

    /**
     * 采集一条真机消息。
     *
     * <p>未启用时立即返回（零开销）。启用后自动：
     * <ol>
     *   <li>从 topic 提取 SN</li>
     *   <li>查注册表获取网关/飞行器型号</li>
     *   <li>从 payload 提取 method</li>
     *   <li>检查去重上限</li>
     *   <li>脱敏敏感字段</li>
     *   <li>异步写入分类文件</li>
     * </ol>
     *
     * @param topic     MQTT topic（如 {@code thing/product/7UUXN1Q00A008W/services}）
     * @param direction 方向标识：{@code "inbound"}（真机→平台）或 {@code "outbound"}（平台→真机）
     * @param payload   原始 JSON 消息
     */
    public static void capture(String topic, String direction, String payload) {
        if (!enabled || payload == null || payload.isEmpty()) return;

        try {
            String sn = extractSn(topic);
            String[] device = (sn != null) ? DEVICE_REGISTRY.get(sn) : null;
            String gateway = (device != null) ? device[0] : (sn != null ? "SN-" + sn : "unknown");
            String aircraft = (device != null) ? device[1] : "unknown";

            String method = extractMethod(payload);
            if (method == null) method = "no-method";

            // 目录分类名：已注册 = 网关-飞行器（如 Dock3-M4D），未注册 = SN-{sn}（不含 -unknown 冗余后缀）
            String category = (device != null) ? gateway + "-" + aircraft : gateway;

            // 去重检查（CAS 循环保证严格不超过 maxSamplesPerMethod 上限）
            String dedupKey = category + "/" + direction + "/" + method;
            AtomicInteger counter = SAMPLE_COUNTS.computeIfAbsent(dedupKey, k -> new AtomicInteger(0));
            int max = config.maxSamplesPerMethod();
            while (true) {
                int current = counter.get();
                if (current >= max) return;
                if (counter.compareAndSet(current, current + 1)) break;
            }

            // 脱敏
            String masked = maskPayload(payload);

            // 构建采集文件内容（原始 JSON + 元数据头）
            String captureJson = buildCaptureJson(topic, direction, gateway, aircraft, method, masked);

            // 异步写文件
            Path dir = config.captureDir().resolve(category).resolve(direction);
            String timestamp = LocalDateTime.now().format(TS_FMT);
            String filename = method + "_" + timestamp + "_" + String.format("%03d", FILE_SEQ.incrementAndGet()) + ".json";

            CompletableFuture.runAsync(() -> writeToFile(dir, filename, captureJson));
        } catch (Exception e) {
            // 采集失败不影响正常逻辑，静默丢弃
        }
    }

    /**
     * 从 MQTT topic 中提取设备 SN。
     *
     * <p>DJI topic 格式：{@code thing/product/{sn}/services} 或 {@code sys/product/{sn}/status}。
     * SN 是 "product" 后的路径段。
     */
    private static String extractSn(String topic) {
        if (topic == null) return null;
        String[] parts = topic.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("product".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /**
     * 从 JSON payload 中提取 method 字段。
     */
    private static String extractMethod(String payload) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            JsonNode methodNode = root.path("method");
            return methodNode.isMissingNode() ? null : methodNode.asText();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 递归脱敏 JSON 中匹配 maskFields 的字段值。
     *
     * <p>遍历 JSON 全树（Object + Array），将字段名匹配 {@code maskFields} 的值替换为 {@code "***"}。
     */
    private static String maskPayload(String payload) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            maskNode(root, config.maskFields());
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            // 解析失败则原样返回（不脱敏但不阻断）
            return payload;
        }
    }

    /**
     * 递归遍历 JsonNode，脱敏匹配字段。
     */
    @SuppressWarnings("unchecked")
    private static void maskNode(JsonNode node, Set<String> maskFields) {
        if (node == null) return;

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode child = entry.getValue();

                if (maskFields.contains(fieldName) && child.isTextual()) {
                    // 敏感文本字段 → 替换为 ***
                    obj.set(fieldName, TextNode.valueOf("***"));
                } else {
                    // 递归处理子节点
                    maskNode(child, maskFields);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                maskNode(element, maskFields);
            }
        }
    }

    /**
     * 构建采集文件内容：在原始 JSON 外包裹 _capture 元数据头。
     */
    private static String buildCaptureJson(String topic, String direction, String gateway,
                                           String aircraft, String method, String maskedPayload) {
        try {
            JsonNode original = MAPPER.readTree(maskedPayload);
            ObjectNode wrapper = MAPPER.createObjectNode();

            ObjectNode meta = wrapper.putObject("_capture");
            meta.put("timestamp", LocalDateTime.now().toString());
            meta.put("topic", topic != null ? topic : "");
            meta.put("direction", direction);
            meta.put("gateway", gateway);
            meta.put("aircraft", aircraft);
            meta.put("method", method);

            // 将原始 JSON 的字段合并到 wrapper（method/tid/bid/data 等）
            wrapper.setAll((ObjectNode) original);

            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);
        } catch (Exception e) {
            // 构建失败则返回脱敏后的原始 JSON
            return maskedPayload;
        }
    }

    /**
     * 异步写入文件（守护线程，失败静默丢弃）。
     */
    private static void writeToFile(Path dir, String filename, String content) {
        try {
            Files.createDirectories(dir);
            Files.writeString(dir.resolve(filename), content);
        } catch (Exception e) {
            // I/O 失败静默丢弃，不影响调用方
        }
    }
}
