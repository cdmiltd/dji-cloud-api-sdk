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
import ltd.cdmi.dji.cloudapi.sdk.model.DockModel;
import ltd.cdmi.dji.cloudapi.sdk.model.DroneModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CaptureRecorder 采集机制验证测试。
 */
class CaptureRecorderTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Path tempDir;

    @AfterEach
    void cleanup() {
        CaptureRecorder.disable();
        if (tempDir != null) {
            try (Stream<Path> walk = Files.walk(tempDir)) {
                walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignored) {} });
            } catch (Exception ignored) {}
        }
    }

    @Test
    void testDisabledCaptureIsNoOp() {
        CaptureRecorder.disable();
        // 未启用时调用 capture 不产生任何副作用
        CaptureRecorder.capture("thing/product/SN001/services", "inbound",
                "{\"method\":\"fly_to_point\",\"data\":{\"fly_to_id\":\"FT001\"}}");
        assertFalse(CaptureRecorder.isEnabled());
    }

    @Test
    void testCaptureCreatesCategorizedFile() throws Exception {
        tempDir = Files.createTempDirectory("dji-capture-test");
        CaptureRecorder.enable(new CaptureConfig(true, tempDir, 5, java.util.Set.of("sn")));
        CaptureRecorder.registerDevice("SN001", DockModel.DOCK3, DroneModel.M4D);

        String payload = "{\"tid\":\"t1\",\"method\":\"fly_to_point\",\"data\":{\"fly_to_id\":\"FT001\"}}";
        CaptureRecorder.capture("thing/product/SN001/services", "inbound", payload);

        // 等待异步写入完成
        Thread.sleep(500);

        // 验证文件创建在正确目录
        Path expectedDir = tempDir.resolve("Dock3-M4D").resolve("inbound");
        assertTrue(Files.exists(expectedDir), "分类目录应存在: " + expectedDir);

        try (Stream<Path> files = Files.list(expectedDir)) {
            List<Path> jsonFiles = files.filter(p -> p.toString().endsWith(".json")).toList();
            assertEquals(1, jsonFiles.size(), "应生成 1 个采集文件");

            // 解析 JSON 验证元数据和原始数据（pretty-printer 输出含空格，不能用字符串匹配）
            JsonNode content = MAPPER.readTree(Files.readString(jsonFiles.get(0)));
            assertTrue(content.has("_capture"), "应包含采集元数据");
            assertEquals("inbound", content.path("_capture").path("direction").asText());
            assertEquals("Dock3", content.path("_capture").path("gateway").asText());
            assertEquals("M4D", content.path("_capture").path("aircraft").asText());
            assertEquals("fly_to_point", content.path("_capture").path("method").asText());
            assertEquals("fly_to_point", content.path("method").asText());
            assertEquals("FT001", content.path("data").path("fly_to_id").asText());
        }
    }

    @Test
    void testDeduplicationLimitsSamples() throws Exception {
        tempDir = Files.createTempDirectory("dji-capture-dedup");
        CaptureRecorder.enable(new CaptureConfig(true, tempDir, 2, java.util.Set.of()));
        CaptureRecorder.registerDevice("SN002", DockModel.DOCK2, DroneModel.M3D);

        String payload = "{\"method\":\"cover_open\",\"data\":{}}";
        // 采集 5 次，但 maxSamplesPerMethod=2
        for (int i = 0; i < 5; i++) {
            CaptureRecorder.capture("thing/product/SN002/services", "inbound", payload);
        }

        Thread.sleep(500);

        Path dir = tempDir.resolve("Dock2-M3D").resolve("inbound");
        try (Stream<Path> files = Files.list(dir)) {
            long count = files.filter(p -> p.toString().endsWith(".json")).count();
            assertEquals(2, count, "去重后应只生成 2 个文件（maxSamplesPerMethod=2）");
        }
    }

    @Test
    void testMaskingSensitiveFields() throws Exception {
        tempDir = Files.createTempDirectory("dji-capture-mask");
        CaptureRecorder.enable(new CaptureConfig(true, tempDir, 5, java.util.Set.of("sn", "app_license")));
        CaptureRecorder.registerDevice("SN003", DockModel.DOCK1, DroneModel.M30T);

        String payload = "{\"method\":\"config\",\"data\":{\"sn\":\"SECRET_SN_123\",\"app_license\":\"LICENSE_XYZ\"}}";
        CaptureRecorder.capture("sys/product/SN003/requests", "inbound", payload);

        Thread.sleep(500);

        Path dir = tempDir.resolve("Dock1-M30T").resolve("inbound");
        try (Stream<Path> files = Files.list(dir)) {
            String raw = Files.readString(files.findFirst().orElseThrow());
            // 解析 JSON 验证脱敏值（pretty-printer 输出含空格，不能用字符串匹配）
            JsonNode content = MAPPER.readTree(raw);
            assertEquals("***", content.path("data").path("sn").asText(), "sn 字段应脱敏为 ***");
            assertEquals("***", content.path("data").path("app_license").asText(), "app_license 字段应脱敏为 ***");
            // 原始值不应出现（原始值是字符串字面量，用 contains 检查原始文本）
            assertFalse(raw.contains("SECRET_SN_123"), "原始 SN 值不应出现");
            assertFalse(raw.contains("LICENSE_XYZ"), "原始 license 值不应出现");
        }
    }

    @Test
    void testUnregisteredDeviceUsesSnFallback() throws Exception {
        tempDir = Files.createTempDirectory("dji-capture-unreg");
        CaptureRecorder.enable(new CaptureConfig(true, tempDir, 5, java.util.Set.of()));
        // 不注册设备

        String payload = "{\"method\":\"update_topo\",\"data\":{}}";
        CaptureRecorder.capture("sys/product/UNKNOWN_SN/status", "inbound", payload);

        Thread.sleep(500);

        // 未注册设备用 SN- 前缀作为目录名
        Path dir = tempDir.resolve("SN-UNKNOWN_SN").resolve("inbound");
        assertTrue(Files.exists(dir), "未注册设备应用 SN- 前缀: " + dir);
    }

    @Test
    void testOutboundDirectionCategorizedSeparately() throws Exception {
        tempDir = Files.createTempDirectory("dji-capture-out");
        CaptureRecorder.enable(new CaptureConfig(true, tempDir, 5, java.util.Set.of()));
        CaptureRecorder.registerDevice("SN004", DockModel.DOCK3, DroneModel.M4D);

        String payload = "{\"method\":\"fly_to_point\",\"data\":{}}";
        CaptureRecorder.capture("thing/product/SN004/services", "inbound", payload);
        CaptureRecorder.capture("thing/product/SN004/services_reply", "outbound", payload);

        Thread.sleep(500);

        // inbound 和 outbound 应分到不同目录
        assertTrue(Files.exists(tempDir.resolve("Dock3-M4D").resolve("inbound")), "inbound 目录应存在");
        assertTrue(Files.exists(tempDir.resolve("Dock3-M4D").resolve("outbound")), "outbound 目录应存在");
    }
}
