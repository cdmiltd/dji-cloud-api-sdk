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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalRotateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Folder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.KmzContent;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ParsedKmz;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecutePlacemark;

/**
 * {@link WpmlCodec} 反序列化（解析）功能测试。
 *
 * <p>验证 KMZ 解包、XML → POJO 反序列化、多态 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam}
 * 反序列化、以及序列化/反序列化往返一致性。
 */
class WpmlParserTest {

    // ==================== 测试数据构造 ====================

    /** 构建一个包含 takePhoto + gimbalRotate 动作的完整 WaypointTemplate */
    private WaypointTemplate buildSampleTemplate() {
        return WaypointTemplate.builder()
            .author("TestPilot")
            .createTime(1637600807044L)
            .updateTime(1637600875837L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
            .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
            .addWaypoint(w -> w
                .longitude(113.98057).latitude(22.987663).height(100)
                .gimbalPitchAngle(0))
            .addWaypoint(w -> w
                .longitude(113.98060).latitude(22.98770).height(100)
                .gimbalPitchAngle(0)
                .addActionGroup(ag -> ag
                    .actionGroupId(0)
                    .actionGroupStartIndex(1)
                    .actionGroupEndIndex(1)
                    .actionTriggerType(ActionTriggerType.REACH_POINT)
                    .addAction(a -> a
                        .actionId(0)
                        .actionActuatorFunc(ActionActuatorFunc.GIMBAL_ROTATE)
                        .actionActuatorFuncParam(new GimbalRotateParam(
                            0, "north",
                            GimbalRotateMode.ABSOLUTE_ANGLE.code(),
                            0, 0, 0, 0, 1, 30, 0, 0)))
                    .addAction(a -> a
                        .actionId(1)
                        .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                        .actionActuatorFuncParam(new TakePhotoParam(0, "point1", "wide", 1)))));
    }

    // ==================== fromKmz 测试 ====================

    @Test
    @DisplayName("fromKmz 正确解包 KMZ 返回 XML 字符串")
    void shouldUnzipKmzToXmlStrings() {
        WaypointTemplate template = buildSampleTemplate();
        byte[] kmz = template.toKmz();

        KmzContent content = WpmlCodec.fromKmz(kmz);

        assertNotNull(content.templateKml(), "templateKml 不应为 null");
        assertNotNull(content.waylinesWpml(), "waylinesWpml 不应为 null");
        assertTrue(content.templateKml().contains("<kml"), "templateKml 应包含 <kml 元素");
        assertTrue(content.waylinesWpml().contains("<kml"), "waylinesWpml 应包含 <kml 元素");
    }

    @Test
    @DisplayName("fromKmz 对缺少 waylines.wpml 的 KMZ 抛 IllegalArgumentException")
    void shouldThrowWhenKmzMissingWaylines() {
        byte[] kmzWithOnlyTemplate = createKmzWithOnlyTemplate();

        assertThrows(IllegalArgumentException.class, () -> WpmlCodec.fromKmz(kmzWithOnlyTemplate));
    }

    @Test
    @DisplayName("fromKmz 对无效 ZIP 字节流抛 IllegalArgumentException")
    void shouldThrowForInvalidZip() {
        byte[] invalid = "not a zip file".getBytes();

        assertThrows(IllegalArgumentException.class, () -> WpmlCodec.fromKmz(invalid));
    }

    // ==================== parseTemplateKml 测试 ====================

    @Test
    @DisplayName("parseTemplateKml 正确解析 template.kml 为 Kml<Folder> POJO")
    void shouldParseTemplateKmlToPojo() {
        WaypointTemplate template = buildSampleTemplate();
        String kml = template.toXml();

        Kml<Folder> parsed = WpmlCodec.parseTemplateKml(kml);

        assertNotNull(parsed, "解析结果不应为 null");
        assertNotNull(parsed.document(), "document 不应为 null");
        Folder folder = parsed.document().folder();
        assertNotNull(folder, "folder 不应为 null");
        assertEquals(7.0, folder.autoFlightSpeed(), "autoFlightSpeed 应为 7");
        assertNotNull(folder.placemarks(), "placemarks 不应为 null");
        assertEquals(2, folder.placemarks().size(), "应有 2 个航点");
    }

    @Test
    @DisplayName("parseTemplateKml 正确解析航点坐标")
    void shouldParseWaypointCoordinates() {
        WaypointTemplate template = buildSampleTemplate();
        Kml<Folder> parsed = WpmlCodec.parseTemplateKml(template.toXml());

        Folder folder = parsed.document().folder();
        var firstWaypoint = folder.placemarks().get(0);
        assertNotNull(firstWaypoint.point(), "Point 不应为 null");
        // coordinates 格式为 "longitude,latitude" 或 "longitude,latitude,height"
        String coords = firstWaypoint.point().coordinates();
        assertTrue(coords.contains("113.98057"), "经度应包含 113.98057");
        assertTrue(coords.contains("22.987663"), "纬度应包含 22.987663");
    }

    // ==================== parseWaylinesWpml 测试 ====================

    @Test
    @DisplayName("parseWaylinesWpml 正确解析 waylines.wpml 为 Kml<ExecuteFolder> POJO")
    void shouldParseWaylinesWpmlToPojo() {
        WaypointTemplate template = buildSampleTemplate();
        String wpml = template.toWpml();

        Kml<ExecuteFolder> parsed = WpmlCodec.parseWaylinesWpml(wpml);

        assertNotNull(parsed, "解析结果不应为 null");
        ExecuteFolder folder = parsed.document().folder();
        assertNotNull(folder, "ExecuteFolder 不应为 null");
        assertEquals(7.0, folder.autoFlightSpeed(), "autoFlightSpeed 应为 7");
        assertNotNull(folder.placemarks(), "placemarks 不应为 null");
        assertEquals(2, folder.placemarks().size(), "应有 2 个航点");
    }

    @Test
    @DisplayName("parseWaylinesWpml 正确解析 executeHeight 和 waypointSpeed")
    void shouldParseExecuteFields() {
        WaypointTemplate template = buildSampleTemplate();
        Kml<ExecuteFolder> parsed = WpmlCodec.parseWaylinesWpml(template.toWpml());

        ExecutePlacemark firstWaypoint = parsed.document().folder().placemarks().get(0);
        assertEquals(100.0, firstWaypoint.executeHeight(), "executeHeight 应为 100");
        assertEquals(7.0, firstWaypoint.waypointSpeed(), "waypointSpeed 应为 7（全局展开）");
        assertNotNull(firstWaypoint.waypointHeadingParam(), "waypointHeadingParam 不应为 null");
        assertNotNull(firstWaypoint.waypointTurnParam(), "waypointTurnParam 不应为 null");
    }

    // ==================== 多态 Action 反序列化测试 ====================

    @Test
    @DisplayName("解析 takePhoto 动作时 actionActuatorFuncParam 为 TakePhotoParam 实例")
    void shouldDeserializeTakePhotoActionAsTakePhotoParam() {
        WaypointTemplate template = buildSampleTemplate();
        Kml<Folder> parsed = WpmlCodec.parseTemplateKml(template.toXml());

        var waypoint = parsed.document().folder().placemarks().get(1);
        var actionGroup = waypoint.actionGroups().get(0);
        var takePhotoAction = actionGroup.actions().stream()
            .filter(a -> "takePhoto".equals(a.actionActuatorFunc()))
            .findFirst()
            .orElseThrow();

        var param = takePhotoAction.actionActuatorFuncParam();
        assertNotNull(param, "actionActuatorFuncParam 不应为 null");
        assertInstanceOf(TakePhotoParam.class, param, "应为 TakePhotoParam 实例");
        TakePhotoParam takePhotoParam = (TakePhotoParam) param;
        assertEquals(0, takePhotoParam.payloadPositionIndex(), "payloadPositionIndex 应为 0");
        assertEquals("point1", takePhotoParam.fileSuffix(), "fileSuffix 应为 point1");
    }

    @Test
    @DisplayName("解析 gimbalRotate 动作时 actionActuatorFuncParam 为 GimbalRotateParam 实例")
    void shouldDeserializeGimbalRotateActionAsGimbalRotateParam() {
        WaypointTemplate template = buildSampleTemplate();
        Kml<Folder> parsed = WpmlCodec.parseTemplateKml(template.toXml());

        var waypoint = parsed.document().folder().placemarks().get(1);
        var actionGroup = waypoint.actionGroups().get(0);
        var gimbalRotateAction = actionGroup.actions().stream()
            .filter(a -> "gimbalRotate".equals(a.actionActuatorFunc()))
            .findFirst()
            .orElseThrow();

        var param = gimbalRotateAction.actionActuatorFuncParam();
        assertNotNull(param, "actionActuatorFuncParam 不应为 null");
        assertInstanceOf(GimbalRotateParam.class, param, "应为 GimbalRotateParam 实例");
        GimbalRotateParam gimbalParam = (GimbalRotateParam) param;
        assertEquals("north", gimbalParam.gimbalHeadingYawBase(), "gimbalHeadingYawBase 应为 north");
    }

    // ==================== parseKmz 端到端测试 ====================

    @Test
    @DisplayName("parseKmz 端到端解析完整 KMZ 为 ParsedKmz")
    void shouldParseKmzEndToEnd() {
        WaypointTemplate template = buildSampleTemplate();
        byte[] kmz = template.toKmz();

        ParsedKmz parsed = WpmlCodec.parseKmz(kmz);

        assertNotNull(parsed, "ParsedKmz 不应为 null");
        assertNotNull(parsed.template(), "template POJO 不应为 null");
        assertNotNull(parsed.waylines(), "waylines POJO 不应为 null");

        // 验证 template POJO
        Folder templateFolder = parsed.template().document().folder();
        assertEquals(2, templateFolder.placemarks().size(), "template 应有 2 个航点");

        // 验证 waylines POJO
        ExecuteFolder waylinesFolder = parsed.waylines().document().folder();
        assertEquals(2, waylinesFolder.placemarks().size(), "waylines 应有 2 个航点");
        assertEquals(7.0, waylinesFolder.autoFlightSpeed(), "autoFlightSpeed 应为 7");
    }

    // ==================== 往返一致性测试 ====================

    @Test
    @DisplayName("toXml → fromXml → toXml 往返一致（template.kml）")
    void shouldRoundTripTemplateKml() {
        WaypointTemplate template = buildSampleTemplate();
        String originalXml = template.toXml();

        Kml<Folder> parsed = WpmlCodec.parseTemplateKml(originalXml);
        String reSerializedXml = WpmlCodec.toXml(parsed);

        // 去除空白差异后比较核心内容
        assertEquals(
            normalizeXml(originalXml),
            normalizeXml(reSerializedXml),
            "template.kml 往返序列化/反序列化应一致"
        );
    }

    @Test
    @DisplayName("toWpml → parseWaylinesWpml → toXml 往返一致（waylines.wpml）")
    void shouldRoundTripWaylinesWpml() {
        WaypointTemplate template = buildSampleTemplate();
        String originalXml = template.toWpml();

        Kml<ExecuteFolder> parsed = WpmlCodec.parseWaylinesWpml(originalXml);
        String reSerializedXml = WpmlCodec.toXml(parsed);

        assertEquals(
            normalizeXml(originalXml),
            normalizeXml(reSerializedXml),
            "waylines.wpml 往返序列化/反序列化应一致"
        );
    }

    @Test
    @DisplayName("toKmz → parseKmz 往返一致")
    void shouldRoundTripKmz() {
        WaypointTemplate template = buildSampleTemplate();
        byte[] kmz = template.toKmz();

        ParsedKmz parsed = WpmlCodec.parseKmz(kmz);

        // 重新生成 KMZ 并验证
        String newKml = WpmlCodec.toXml(parsed.template());
        String newWpml = WpmlCodec.toXml(parsed.waylines());
        byte[] newKmz = WpmlCodec.toKmz(newKml, newWpml);

        // 再次解析新 KMZ，验证字段一致
        ParsedKmz reparsed = WpmlCodec.parseKmz(newKmz);
        Folder originalFolder = parsed.template().document().folder();
        Folder reparsedFolder = reparsed.template().document().folder();
        assertEquals(originalFolder.autoFlightSpeed(), reparsedFolder.autoFlightSpeed(),
            "往返后 autoFlightSpeed 应一致");
        assertEquals(originalFolder.placemarks().size(), reparsedFolder.placemarks().size(),
            "往返后航点数应一致");
    }

    // ==================== 辅助方法 ====================

    /** 规范化 XML 字符串（去除声明和空白差异）用于比较 */
    private String normalizeXml(String xml) {
        return xml.replaceAll("\\s+", " ")
                   .replaceAll("<\\?xml[^>]*\\?>", "")
                   .trim();
    }

    /** 构造只包含 template.kml 的 KMZ（缺少 waylines.wpml） */
    private byte[] createKmzWithOnlyTemplate() {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
            zos.putNextEntry(new java.util.zip.ZipEntry("wpmz/template.kml"));
            zos.write("<kml>only template</kml>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zos.closeEntry();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}
