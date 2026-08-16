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
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CaptureConfig} 真机采集配置 record 测试。
 *
 * <p>验证：{@code defaults()} 工厂、字段访问器、默认脱敏字段集合、record 相等性、
 * 以及序列化/反序列化往返闭环（snake_case 字段名 + Path 纯路径字符串）。
 */
class CaptureConfigTest {

    // ==================== defaults() 工厂 ====================

    @Test
    @DisplayName("defaults()：启用采集，输出 dji-capture 目录，每方法每机型 5 份")
    void testDefaults() {
        CaptureConfig cfg = CaptureConfig.defaults();
        assertTrue(cfg.enabled());
        assertEquals(Path.of("dji-capture"), cfg.captureDir());
        assertEquals(5, cfg.maxSamplesPerMethod());
        assertNotNull(cfg.maskFields());
        assertFalse(cfg.maskFields().isEmpty());
    }

    @Test
    @DisplayName("defaults()：maskFields 含 sn/app_license/access_key_id/security_token/signature 等敏感字段")
    void testDefaultMaskFields() {
        Set<String> masks = CaptureConfig.defaults().maskFields();
        assertTrue(masks.contains("sn"));
        assertTrue(masks.contains("app_license"));
        assertTrue(masks.contains("app_id"));
        assertTrue(masks.contains("app_key"));
        assertTrue(masks.contains("access_key_id"));
        assertTrue(masks.contains("secret_access_key"));
        assertTrue(masks.contains("security_token"));
        assertTrue(masks.contains("client_token"));
        assertTrue(masks.contains("nonce"));
        assertTrue(masks.contains("signature"));
        // 默认脱敏字段至少 10 个
        assertTrue(masks.size() >= 10);
    }

    // ==================== record 契约 ====================

    @Test
    @DisplayName("record 访问器：enabled/captureDir/maxSamplesPerMethod/maskFields")
    void testAccessors() {
        Path dir = Path.of("/tmp/capture");
        Set<String> masks = Set.of("sn", "token");
        CaptureConfig cfg = new CaptureConfig(false, dir, 10, masks);
        assertFalse(cfg.enabled());
        assertEquals(dir, cfg.captureDir());
        assertEquals(10, cfg.maxSamplesPerMethod());
        assertEquals(masks, cfg.maskFields());
    }

    @Test
    @DisplayName("record 相等性：相同字段值相等，hashCode 一致")
    void testEqualsAndHashCode() {
        Path dir = Path.of("dji-capture");
        Set<String> masks = Set.of("sn");
        CaptureConfig a = new CaptureConfig(true, dir, 5, masks);
        CaptureConfig b = new CaptureConfig(true, dir, 5, masks);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ==================== 序列化结构验证 ====================

    @Test
    @DisplayName("序列化：record → snake_case JSON（capture_dir 为纯路径字符串，非 URI）")
    void testSerializeSnakeCase() throws Exception {
        CaptureConfig cfg = new CaptureConfig(true, Path.of("dji-capture"), 5, Set.of("sn"));
        String json = MessageCodec.toJson(cfg);
        JsonNode node = new ObjectMapper().readTree(json);
        assertTrue(node.path("enabled").asBoolean(), "enabled 字段应为 true");
        assertEquals(5, node.path("max_samples_per_method").asInt(),
                "maxSamplesPerMethod 应序列化为 max_samples_per_method");
        assertTrue(node.has("mask_fields"), "maskFields 应序列化为 mask_fields");
        assertTrue(node.path("mask_fields").isArray(), "mask_fields 应为数组");
        assertEquals("sn", node.path("mask_fields").get(0).asText());
        assertTrue(node.has("capture_dir"), "captureDir 应序列化为 capture_dir");
        assertEquals("dji-capture", node.path("capture_dir").asText(),
                "capture_dir 应为纯路径字符串，非 URI 形式");
    }

    @Test
    @DisplayName("序列化：disabled 配置 enabled=false")
    void testSerializeDisabled() throws Exception {
        CaptureConfig cfg = new CaptureConfig(false, Path.of("out"), 1, Set.of());
        String json = MessageCodec.toJson(cfg);
        JsonNode node = new ObjectMapper().readTree(json);
        assertFalse(node.path("enabled").asBoolean());
        assertEquals(1, node.path("max_samples_per_method").asInt());
        assertEquals(0, node.path("mask_fields").size());
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变（含 Path 字段）")
    void testRoundTrip() {
        CaptureConfig original = new CaptureConfig(true, Path.of("dji-capture"), 5, Set.of("sn", "token"));
        String json = MessageCodec.toJson(original);
        CaptureConfig back = MessageCodec.fromJson(json, CaptureConfig.class);
        assertEquals(original, back, "序列化→反序列化应保持 equals 不变");
        assertEquals(original.captureDir(), back.captureDir(), "Path 字段应往返一致");
    }
}
