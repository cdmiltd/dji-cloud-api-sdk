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

package ltd.cdmi.dji.cloudapi.sdk.wayline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;

/**
 * {@link WpmlCodec#toKmz(String, String)} 和 {@link WaypointTemplate#toKmz()} 端到端测试。
 *
 * <p>验证生成的 KMZ（ZIP）内部结构符合 DJI WPML 规范：
 * <pre>
 * *.kmz
 * └── wpmz
 *     ├── template.kml
 *     └── waylines.wpml
 * </pre>
 */
class KmzPackagingTest {

    @Test
    @DisplayName("WpmlCodec.toKmz 返回非空 ZIP 字节流")
    void shouldReturnNonEmptyKmz() {
        byte[] kmz = WpmlCodec.toKmz("<kml>template</kml>", "<kml>waylines</kml>");

        assertNotNull(kmz);
        assertTrue(kmz.length > 0, "KMZ 字节流应非空");
    }

    @Test
    @DisplayName("KMZ 包含 wpmz/template.kml 和 wpmz/waylines.wpml 两个条目")
    void shouldContainWpmzEntries() throws IOException {
        byte[] kmz = WpmlCodec.toKmz("<kml>template</kml>", "<kml>waylines</kml>");

        Map<String, String> entries = unzip(kmz);
        assertTrue(entries.containsKey("wpmz/template.kml"), "应包含 wpmz/template.kml");
        assertTrue(entries.containsKey("wpmz/waylines.wpml"), "应包含 wpmz/waylines.wpml");
        assertEquals(2, entries.size(), "应恰好包含 2 个条目");
    }

    @Test
    @DisplayName("KMZ 内 template.kml 内容与输入一致")
    void shouldPreserveTemplateKmlContent() throws IOException {
        String kml = "<?xml version=\"1.0\"?><kml>template content</kml>";
        byte[] kmz = WpmlCodec.toKmz(kml, "<kml>wpml</kml>");

        Map<String, String> entries = unzip(kmz);
        assertEquals(kml, entries.get("wpmz/template.kml"), "template.kml 内容应与输入一致");
    }

    @Test
    @DisplayName("KMZ 内 waylines.wpml 内容与输入一致")
    void shouldPreserveWaylinesWpmlContent() throws IOException {
        String wpml = "<?xml version=\"1.0\"?><kml>waylines content</kml>";
        byte[] kmz = WpmlCodec.toKmz("<kml>kml</kml>", wpml);

        Map<String, String> entries = unzip(kmz);
        assertEquals(wpml, entries.get("wpmz/waylines.wpml"), "waylines.wpml 内容应与输入一致");
    }

    @Test
    @DisplayName("KMZ 是有效的 ZIP 格式（可被 ZipInputStream 解析）")
    void shouldBeValidZipFormat() throws IOException {
        byte[] kmz = WpmlCodec.toKmz("<kml>k</kml>", "<kml>w</kml>");

        // 如果能成功解压且无异常，说明是有效 ZIP
        Map<String, String> entries = unzip(kmz);
        assertTrue(entries.size() == 2, "应能成功解压 2 个条目");
    }

    @Test
    @DisplayName("WaypointTemplate.toKmz 返回包含 toXml 和 toWpml 输出的 KMZ")
    void shouldWaypointTemplateToKmzContainBothOutputs() throws IOException {
        WaypointTemplate template = buildFullTemplate();
        String kml = template.toXml();
        String wpml = template.toWpml();

        byte[] kmz = template.toKmz();

        Map<String, String> entries = unzip(kmz);
        assertTrue(entries.containsKey("wpmz/template.kml"), "应包含 template.kml");
        assertTrue(entries.containsKey("wpmz/waylines.wpml"), "应包含 waylines.wpml");
        assertEquals(kml, entries.get("wpmz/template.kml"), "template.kml 应与 toXml() 输出一致");
        assertEquals(wpml, entries.get("wpmz/waylines.wpml"), "waylines.wpml 应与 toWpml() 输出一致");
    }

    @Test
    @DisplayName("WaypointTemplate.toKmz 的 template.kml 包含 templateType，waylines.wpml 包含 executeHeightMode")
    void shouldWaypointTemplateToKmzContainCorrectStructure() throws IOException {
        byte[] kmz = buildFullTemplate().toKmz();

        Map<String, String> entries = unzip(kmz);
        assertTrue(entries.get("wpmz/template.kml").contains("templateType"),
            "template.kml 应包含 templateType");
        assertTrue(entries.get("wpmz/waylines.wpml").contains("executeHeightMode"),
            "waylines.wpml 应包含 executeHeightMode");
    }

    // ════════════════════════════════════════════
    //  辅助方法
    // ════════════════════════════════════════════

    /** 构建完整模板（含所有必需字段 + 2 个航点） */
    private WaypointTemplate buildFullTemplate() {
        return WaypointTemplate.builder()
            .author("TestPilot")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .heightMode(HeightMode.EGM96)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
            .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
            .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100))
            .addWaypoint(w -> w.longitude(113.98060).latitude(22.98770).height(120));
    }

    /** 解压 KMZ 字节流，返回 文件路径 → 内容 的映射 */
    private Map<String, String> unzip(byte[] kmz) throws IOException {
        Map<String, String> entries = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(kmz))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                byte[] content = zis.readAllBytes();
                entries.put(entry.getName(), new String(content, StandardCharsets.UTF_8));
                zis.closeEntry();
            }
        }
        return entries;
    }
}
