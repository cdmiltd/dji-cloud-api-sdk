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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteRCLostAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExitOnRCLost;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FocusMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.MappingHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.MeteringMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.PositioningType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ReturnMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ScanningMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ShootType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.Overlap;

/**
 * 建图航拍/倾斜摄影/航带飞行三种模板的端到端 Builder → XML 测试。
 */
class MappingTemplateTest {

    private static final String POLYGON_COORDS =
        "113.98057,22.987663,0 113.990000,22.987663,0 113.990000,22.977663,0 113.98057,22.977663,0";

    private static final String LINESTRING_COORDS =
        "113.98057,22.987663,100 113.990000,22.987663,100 113.990000,22.977663,100";

    private static final Overlap OVERLAP = new Overlap(
        null, null, 80, 70, null, null, 80, 70);

    // ════════════════════════════════════════════
    //  mapping2d (建图航拍)
    // ════════════════════════════════════════════

    @Test
    @DisplayName("mapping2d: 生成有效 XML")
    void shouldGenerateMapping2dKml() {
        String xml = Mapping2dTemplate.builder()
            .author("test")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .autoFlightSpeed(7)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .globalShootHeight(50)
            .positioningType(PositioningType.GPS)
            .payloadParam(0, "wide")
            .caliFlightEnable(0)
            .elevationOptimizeEnable(1)
            .shootType(ShootType.TIME)
            .direction(0)
            .margin(10)
            .overlap(OVERLAP)
            .ellipsoidHeight(90.2)
            .height(100)
            .polygon(POLYGON_COORDS)
            .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
            .toXml();

        assertTrue(xml.contains("<wpml:templateType>mapping2d</wpml:templateType>"), "应包含 templateType=mapping2d");
        assertTrue(xml.contains("<wpml:elevationOptimizeEnable>1</wpml:elevationOptimizeEnable>"), "应包含 elevationOptimizeEnable");
        assertTrue(xml.contains("<wpml:shootType>time</wpml:shootType>"), "应包含 shootType");
        assertTrue(xml.contains("<wpml:direction>0</wpml:direction>"), "应包含 direction");
        assertTrue(xml.contains("<wpml:margin>10</wpml:margin>"), "应包含 margin");
        assertTrue(xml.contains("<wpml:orthoCameraOverlapH>80</wpml:orthoCameraOverlapH>"), "应包含 overlap");
        assertTrue(xml.contains("<Polygon>"), "应包含 Polygon");
        assertTrue(xml.contains("<LinearRing>"), "应包含 LinearRing");
        assertTrue(xml.contains(POLYGON_COORDS), "应包含测区坐标");
        assertTrue(xml.contains("<wpml:mappingHeadingMode>followWayline</wpml:mappingHeadingMode>"), "应包含 mappingHeadingMode");
    }

    @Test
    @DisplayName("mapping2d: direction 超出 [0,360] 抛出异常")
    void shouldRejectMapping2dDirectionOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            Mapping2dTemplate.builder().direction(361));
    }

    // ════════════════════════════════════════════
    //  mapping3d (倾斜摄影)
    // ════════════════════════════════════════════

    @Test
    @DisplayName("mapping3d: 生成有效 XML")
    void shouldGenerateMapping3dKml() {
        String xml = Mapping3dTemplate.builder()
            .author("test")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .autoFlightSpeed(7)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .globalShootHeight(50)
            .positioningType(PositioningType.GPS)
            .payloadParam(0, "wide")
            .caliFlightEnable(0)
            .inclinedGimbalPitch(-45)
            .inclinedFlightSpeed(5)
            .shootType(ShootType.TIME)
            .direction(0)
            .margin(10)
            .overlap(OVERLAP)
            .ellipsoidHeight(90.2)
            .height(100)
            .polygon(POLYGON_COORDS)
            .toXml();

        assertTrue(xml.contains("<wpml:templateType>mapping3d</wpml:templateType>"), "应包含 templateType=mapping3d");
        assertTrue(xml.contains("<wpml:inclinedGimbalPitch>-45</wpml:inclinedGimbalPitch>"), "应包含 inclinedGimbalPitch");
        assertTrue(xml.contains("<wpml:inclinedFlightSpeed>5.0</wpml:inclinedFlightSpeed>"), "应包含 inclinedFlightSpeed");
        assertTrue(xml.contains("<wpml:shootType>time</wpml:shootType>"), "应包含 shootType");
        assertTrue(xml.contains("<Polygon>"), "应包含 Polygon");
    }

    @Test
    @DisplayName("mapping3d: inclinedFlightSpeed 超出 [1,15] 抛出异常")
    void shouldRejectMapping3dSpeedOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            Mapping3dTemplate.builder().inclinedFlightSpeed(20));
    }

    // ════════════════════════════════════════════
    //  mappingStrip (航带飞行)
    // ════════════════════════════════════════════

    @Test
    @DisplayName("mappingStrip: 生成有效 XML")
    void shouldGenerateMappingStripKml() {
        String xml = MappingStripTemplate.builder()
            .author("test")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .autoFlightSpeed(7)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .globalShootHeight(50)
            .positioningType(PositioningType.GPS)
            .payloadParam(0, "wide")
            .caliFlightEnable(0)
            .shootType(ShootType.DISTANCE)
            .direction(90)
            .margin(5)
            .singleLineEnable(0)
            .cuttingDistance(100.0)
            .boundaryOptimEnable(1)
            .leftExtend(10)
            .rightExtend(10)
            .includeCenterEnable(1)
            .overlap(OVERLAP)
            .ellipsoidHeight(90.2)
            .height(100)
            .stripUseTemplateAltitude(0)
            .lineString(LINESTRING_COORDS)
            .toXml();

        assertTrue(xml.contains("<wpml:templateType>mappingStrip</wpml:templateType>"), "应包含 templateType=mappingStrip");
        assertTrue(xml.contains("<wpml:singleLineEnable>0</wpml:singleLineEnable>"), "应包含 singleLineEnable");
        assertTrue(xml.contains("<wpml:cuttingDistance>100.0</wpml:cuttingDistance>"), "应包含 cuttingDistance");
        assertTrue(xml.contains("<wpml:boundaryOptimEnable>1</wpml:boundaryOptimEnable>"), "应包含 boundaryOptimEnable");
        assertTrue(xml.contains("<wpml:leftExtend>10</wpml:leftExtend>"), "应包含 leftExtend");
        assertTrue(xml.contains("<wpml:rightExtend>10</wpml:rightExtend>"), "应包含 rightExtend");
        assertTrue(xml.contains("<wpml:includeCenterEnable>1</wpml:includeCenterEnable>"), "应包含 includeCenterEnable");
        assertTrue(xml.contains("<wpml:stripUseTemplateAltitude>0</wpml:stripUseTemplateAltitude>"), "应包含 stripUseTemplateAltitude");
        assertTrue(xml.contains("<LineString>"), "应包含 LineString");
        assertTrue(xml.contains(LINESTRING_COORDS), "应包含航带坐标");
    }

    @Test
    @DisplayName("mappingStrip: direction 超出 [0,360] 抛出异常")
    void shouldRejectMappingStripDirectionOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            MappingStripTemplate.builder().direction(-1));
    }

    // ════════════════════════════════════════════
    //  M300/M350 激光雷达 payloadParam（完整配置）
    // ════════════════════════════════════════════

    @Test
    @DisplayName("mapping2d: M300 激光雷达 payloadParam 生成完整 XML")
    void shouldGenerateLidarPayloadParam() {
        String xml = Mapping2dTemplate.builder()
            .author("test")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .autoFlightSpeed(7)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .globalShootHeight(50)
            .positioningType(PositioningType.RTK_BASE_STATION)
            .payloadParam(p -> p
                .payloadPositionIndex(0)
                .returnMode(ReturnMode.TRIPLE_RETURN)
                .samplingRate(240000)
                .scanningMode(ScanningMode.NON_REPETITIVE)
                .modelColoringEnable(1)
                .imageFormat("JPEG"))
            .shootType(ShootType.TIME)
            .direction(0)
            .overlap(OVERLAP)
            .height(100)
            .polygon(POLYGON_COORDS)
            .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
            .toXml();

        assertTrue(xml.contains("<wpml:returnMode>tripleReturn</wpml:returnMode>"), "应包含 returnMode=tripleReturn");
        assertTrue(xml.contains("<wpml:samplingRate>240000</wpml:samplingRate>"), "应包含 samplingRate=240000");
        assertTrue(xml.contains("<wpml:scanningMode>nonRepetitive</wpml:scanningMode>"), "应包含 scanningMode=nonRepetitive");
        assertTrue(xml.contains("<wpml:modelColoringEnable>1</wpml:modelColoringEnable>"), "应包含 modelColoringEnable=1");
        assertTrue(xml.contains("<wpml:imageFormat>JPEG</wpml:imageFormat>"), "应包含 imageFormat=JPEG");
        assertTrue(xml.contains("<wpml:positioningType>RTKBaseStation</wpml:positioningType>"), "应包含 RTK 定位");
    }

    @Test
    @DisplayName("mapping2d: M300 可见光相机 payloadParam 生成完整 XML")
    void shouldGenerateCameraPayloadParam() {
        String xml = Mapping2dTemplate.builder()
            .author("test")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .takeOffSecurityHeight(20)
            .globalTransitionalSpeed(8)
            .globalRTHHeight(100)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .templateId(0)
            .autoFlightSpeed(7)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .globalShootHeight(50)
            .positioningType(PositioningType.GPS)
            .payloadParam(p -> p
                .payloadPositionIndex(0)
                .focusMode(FocusMode.FIRST_POINT)
                .meteringMode(MeteringMode.AVERAGE)
                .dewarpingEnable(1)
                .imageFormat("wide"))
            .shootType(ShootType.TIME)
            .direction(0)
            .overlap(OVERLAP)
            .height(100)
            .polygon(POLYGON_COORDS)
            .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
            .toXml();

        assertTrue(xml.contains("<wpml:focusMode>firstPoint</wpml:focusMode>"), "应包含 focusMode=firstPoint");
        assertTrue(xml.contains("<wpml:meteringMode>average</wpml:meteringMode>"), "应包含 meteringMode=average");
        assertTrue(xml.contains("<wpml:dewarpingEnable>1</wpml:dewarpingEnable>"), "应包含 dewarpingEnable=1");
    }

    @Test
    @DisplayName("PayloadParamBuilder: samplingRate 非法值抛出异常")
    void shouldRejectInvalidSamplingRate() {
        assertThrows(IllegalArgumentException.class, () ->
            Mapping2dTemplate.builder()
                .payloadParam(p -> p
                    .payloadPositionIndex(0)
                    .samplingRate(50000)));
    }

    @Test
    @DisplayName("PayloadParamBuilder: payloadPositionIndex 未设置抛出异常")
    void shouldRejectMissingPayloadPositionIndex() {
        assertThrows(IllegalStateException.class, () ->
            Mapping2dTemplate.builder()
                .payloadParam(p -> p.returnMode(ReturnMode.DUAL_RETURN))
                .toXml());
    }
}
