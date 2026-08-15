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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionGroupMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteRCLostAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExitOnRCLost;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalPitchMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalRotateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;

/**
 * {@link WaypointTemplate} 端到端测试：Builder → XML 序列化 → 结构验证。
 *
 * <p>以 DJI WPML template.kml 文档示例为基准，验证生成的 XML 包含正确的
 * 命名空间、元素层级、枚举码值以及 actionGroup 结构。
 */
class WaypointTemplateTest {

    @Test
    @DisplayName("生成的 XML 包含 XML 声明和 KML 根元素命名空间")
    void shouldContainXmlDeclarationAndNamespaces() {
        String xml = buildMinimalTemplate();

        assertTrue(xml.contains("<?xml"), "应包含 XML 声明");
        assertTrue(xml.contains("http://www.opengis.net/kml/2.2"), "应包含 KML 命名空间");
        assertTrue(xml.contains("http://www.dji.com/wpmz/1.0.2"), "应包含 WPML 命名空间");
    }

    @Test
    @DisplayName("生成的 XML 包含 Document 创建信息：author / createTime")
    void shouldContainDocumentCreationInfo() {
        String xml = WaypointTemplate.builder()
            .author("TestPilot")
            .createTime(1637600807044L)
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
            .toXml();

        assertTrue(xml.contains("TestPilot"), "应包含 author");
        assertTrue(xml.contains("1637600807044"), "应包含 createTime");
    }

    @Test
    @DisplayName("生成的 XML 包含 missionConfig 枚举码值")
    void shouldContainMissionConfigEnumCodes() {
        String xml = buildMinimalTemplate();

        assertTrue(xml.contains("safely"), "flyToWaylineMode 应为 safely");
        assertTrue(xml.contains("goHome"), "finishAction 应为 goHome");
        assertTrue(xml.contains("goContinue"), "exitOnRCLost 应为 goContinue");
        assertTrue(xml.contains("hover"), "executeRCLostAction 应为 hover");
    }

    @Test
    @DisplayName("生成的 XML 包含 Folder 模板配置")
    void shouldContainFolderTemplateConfig() {
        String xml = buildMinimalTemplate();

        assertTrue(xml.contains("waypoint"), "templateType 应为 waypoint");
        assertTrue(xml.contains("WGS84"), "coordinateMode 应为 WGS84");
        assertTrue(xml.contains("EGM96"), "heightMode 应为 EGM96");
    }

    @Test
    @DisplayName("生成的 XML 包含航点坐标和索引")
    void shouldContainWaypointCoordinatesAndIndex() {
        String xml = WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100))
            .addWaypoint(w -> w.longitude(113.98060).latitude(22.987700).height(120))
            .toXml();

        assertTrue(xml.contains("113.980570"), "应包含第一个航点经度");
        assertTrue(xml.contains("22.987663"), "应包含第一个航点纬度");
        assertTrue(xml.contains("113.980600"), "应包含第二个航点经度");
    }

    @Test
    @DisplayName("生成的 XML 包含 actionGroup 及动作")
    void shouldContainActionGroupAndActions() {
        String xml = WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .addWaypoint(w -> w
                .longitude(113.98).latitude(22.98).height(100)
                .addActionGroup(ag -> ag
                    .actionGroupId(0)
                    .actionGroupStartIndex(0)
                    .actionGroupEndIndex(0)
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
                        .actionActuatorFuncParam(new TakePhotoParam(0, "point1", "wide", 1)))))
            .toXml();

        assertTrue(xml.contains("gimbalRotate"), "应包含 gimbalRotate 动作");
        assertTrue(xml.contains("takePhoto"), "应包含 takePhoto 动作");
        assertTrue(xml.contains("absoluteAngle"), "应包含 gimbalRotateMode");
        assertTrue(xml.contains("point1"), "应包含 fileSuffix");
        assertTrue(xml.contains("actionGroupId"), "应包含 actionGroupId");
        assertTrue(xml.contains("actionGroupStartIndex"), "应包含 actionGroupStartIndex");
    }

    @Test
    @DisplayName("无航点时 toXml 抛出 IllegalStateException")
    void shouldThrowWhenNoWaypoints() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .toXml());
    }

    @Test
    @DisplayName("缺少 flyToWaylineMode 时抛出 IllegalStateException")
    void shouldThrowWhenFlyToWaylineModeMissing() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
                .toXml());
    }

    @Test
    @DisplayName("autoFlightSpeed 超出 [1,15] 抛出 IllegalArgumentException")
    void shouldRejectSpeedOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            WaypointTemplate.builder().autoFlightSpeed(20));
        assertThrows(IllegalArgumentException.class, () ->
            WaypointTemplate.builder().autoFlightSpeed(0));
    }

    @Test
    @DisplayName("globalTransitionalSpeed 超出 [1,15] 抛出 IllegalArgumentException")
    void shouldRejectTransitionalSpeedOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            WaypointTemplate.builder().globalTransitionalSpeed(20));
    }

    @Test
    @DisplayName("globalRTHHeight 超出 [2,1500] 抛出 IllegalArgumentException")
    void shouldRejectRTHHeightOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            WaypointTemplate.builder().globalRTHHeight(1));
    }

    @Test
    @DisplayName("经度超出 [-180,180] 抛出 IllegalArgumentException")
    void shouldRejectLongitudeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            WaypointTemplate.builder()
                .addWaypoint(w -> w.longitude(200).latitude(22).height(100)));
    }

    @Test
    @DisplayName("完整模板生成不抛异常")
    void shouldGenerateFullTemplateWithoutError() {
        assertDoesNotThrow(() -> {
            String xml = WaypointTemplate.builder()
                .author("John")
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
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
                .coordinateMode(CoordinateMode.WGS84)
                .heightMode(HeightMode.EGM96)
                .autoFlightSpeed(7)
                .gimbalPitchMode(GimbalPitchMode.USE_POINT_SETTING)
                .globalHeight(100)
                .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
                .globalWaypointHeadingPathMode(WaypointHeadingPathMode.CLOCKWISE)
                .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
                .globalUseStraightLine(0)
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
                            .actionActuatorFuncParam(new TakePhotoParam(0, "point1", "wide", 1)))))
                .toXml();
        });
    }

    /** 构建最小合法模板（仅必需字段） */
    private String buildMinimalTemplate() {
        return WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
            .executeRCLostAction(ExecuteRCLostAction.HOVER)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .coordinateMode(CoordinateMode.WGS84)
            .heightMode(HeightMode.EGM96)
            .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
            .toXml();
    }
}
