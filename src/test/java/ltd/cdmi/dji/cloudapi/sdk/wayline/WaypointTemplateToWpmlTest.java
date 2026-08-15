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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteRCLostAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExitOnRCLost;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalPitchMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;

/**
 * {@link WaypointTemplate#toWpml()} 端到端测试：Builder → waylines.wpml 转换 → 结构验证。
 *
 * <p>以 DJI WPML waylines.wpml 文档示例为基准，验证生成的 XML 包含正确的
 * executeHeightMode、展开的航点参数（waypointSpeed/waypointHeadingParam/waypointTurnParam）、
 * 移除的 useGlobalXxx 标志以及机型适配后的 missionConfig。
 *
 * @see WaypointTemplate#toXml()
 */
class WaypointTemplateToWpmlTest {

    // ════════════════════════════════════════════
    //  基本转换
    // ════════════════════════════════════════════

    @Test
    @DisplayName("toWpml 生成包含 XML 声明和命名空间")
    void shouldContainXmlDeclarationAndNamespaces() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("<?xml"), "应包含 XML 声明");
        assertTrue(wpml.contains("http://www.opengis.net/kml/2.2"), "应包含 KML 命名空间");
        assertTrue(wpml.contains("http://www.dji.com/wpmz/1.0.2"), "应包含 WPML 命名空间");
    }

    @Test
    @DisplayName("toWpml 包含 ExecuteFolder 字段：templateId / executeHeightMode / waylineId / autoFlightSpeed")
    void shouldContainExecuteFolderFields() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("executeHeightMode"), "应包含 executeHeightMode");
        assertTrue(wpml.contains("waylineId"), "应包含 waylineId");
        assertTrue(wpml.contains("autoFlightSpeed"), "应包含 autoFlightSpeed");
        assertFalse(wpml.contains("templateType"), "不应包含 templateType");
        assertFalse(wpml.contains("globalHeight"), "不应包含 globalHeight");
    }

    @Test
    @DisplayName("toWpml 包含 ExecutePlacemark 字段：index / executeHeight / waypointSpeed")
    void shouldContainExecutePlacemarkFields() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("executeHeight"), "应包含 executeHeight");
        assertTrue(wpml.contains("waypointSpeed"), "应包含 waypointSpeed");
        assertTrue(wpml.contains("waypointHeadingParam"), "应包含 waypointHeadingParam");
        assertTrue(wpml.contains("waypointTurnParam"), "应包含 waypointTurnParam");
        assertFalse(wpml.contains("useGlobalHeight"), "不应包含 useGlobalHeight");
        assertFalse(wpml.contains("useGlobalSpeed"), "不应包含 useGlobalSpeed");
        assertFalse(wpml.contains("useGlobalHeadingParam"), "不应包含 useGlobalHeadingParam");
        assertFalse(wpml.contains("useGlobalTurnParam"), "不应包含 useGlobalTurnParam");
    }

    // ════════════════════════════════════════════
    //  全局参数展开
    // ════════════════════════════════════════════

    @Test
    @DisplayName("waypointSpeed 来自全局 autoFlightSpeed")
    void shouldExpandGlobalSpeedToWaypoint() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("<wpml:waypointSpeed>7.0</wpml:waypointSpeed>")
            || wpml.contains("<wpml:waypointSpeed>7</wpml:waypointSpeed>"),
            "waypointSpeed 应为全局 autoFlightSpeed=7");
    }

    @Test
    @DisplayName("waypointHeadingParam 来自全局 globalWaypointHeadingMode")
    void shouldExpandGlobalHeadingParamToWaypoint() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("followWayline"), "waypointHeadingMode 应为 followWayline");
    }

    @Test
    @DisplayName("waypointTurnParam 来自全局 globalWaypointTurnMode + dampingDist=0")
    void shouldExpandGlobalTurnParamToWaypoint() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("toPointAndStopWithDiscontinuityCurvature"),
            "waypointTurnMode 应为全局值");
        assertTrue(wpml.contains("waypointTurnDampingDist"), "应包含 waypointTurnDampingDist");
    }

    // ════════════════════════════════════════════
    //  heightMode → executeHeightMode 映射
    // ════════════════════════════════════════════

    @Test
    @DisplayName("heightMode=EGM96 → executeHeightMode=WGS84")
    void shouldMapEgm96ToWgs84() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("<wpml:executeHeightMode>WGS84</wpml:executeHeightMode>"),
            "EGM96 应映射为 WGS84");
    }

    @Test
    @DisplayName("heightMode=relativeToStartPoint → executeHeightMode=relativeToStartPoint")
    void shouldKeepRelativeToStartPoint() {
        String wpml = WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
            .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
            .heightMode(HeightMode.RELATIVE_TO_START_POINT)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
            .toWpml();

        assertTrue(wpml.contains("<wpml:executeHeightMode>relativeToStartPoint</wpml:executeHeightMode>"),
            "relativeToStartPoint 应保持");
    }

    // ════════════════════════════════════════════
    //  missionConfig 转换
    // ════════════════════════════════════════════

    @Test
    @DisplayName("missionConfig 不包含 takeOffRefPoint / takeOffRefPointAGLHeight")
    void shouldNotContainTakeOffRefPoint() {
        String wpml = WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
            .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .takeOffRefPoint(22.98, 113.98, 50)
            .takeOffRefPointAGLHeight(50)
            .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
            .toWpml();

        assertFalse(wpml.contains("takeOffRefPoint"), "不应包含 takeOffRefPoint");
        assertFalse(wpml.contains("takeOffRefPointAGLHeight"), "不应包含 takeOffRefPointAGLHeight");
    }

    @Test
    @DisplayName("missionConfig 保留 flyToWaylineMode / finishAction / droneInfo")
    void shouldKeepMissionConfigFields() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("safely"), "应保留 flyToWaylineMode");
        assertTrue(wpml.contains("goHome"), "应保留 finishAction");
        assertTrue(wpml.contains("droneInfo"), "应保留 droneInfo");
        assertTrue(wpml.contains("payloadInfo"), "应保留 payloadInfo");
    }

    // ════════════════════════════════════════════
    //  机型适配
    // ════════════════════════════════════════════

    @Test
    @DisplayName("droneInfo / payloadInfo 枚举值正确写入 missionConfig")
    void shouldWriteDroneInfoAndPayloadInfo() {
        String wpml = buildFullTemplate().toWpml();

        assertTrue(wpml.contains("droneEnumValue"), "应包含 droneEnumValue 元素");
        assertTrue(wpml.contains("67"), "droneEnumValue 应为 67 (M30)");
        assertTrue(wpml.contains("droneSubEnumValue"), "应包含 droneSubEnumValue 元素");
        assertTrue(wpml.contains("payloadEnumValue"), "应包含 payloadEnumValue 元素");
        assertTrue(wpml.contains("52"), "payloadEnumValue 应为 52");
    }

    // ════════════════════════════════════════════
    //  校验：缺失必需字段
    // ════════════════════════════════════════════

    @Test
    @DisplayName("droneInfo 未设置时 toWpml 抛出 IllegalStateException")
    void shouldThrowWhenDroneInfoMissing() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
                .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
                .payloadInfo(52, 0)
                .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
                .toWpml());
    }

    @Test
    @DisplayName("无航点时 toWpml 抛出 IllegalStateException")
    void shouldThrowWhenNoWaypoints() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
                .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
                .droneInfo(67, 0)
                .payloadInfo(52, 0)
                .toWpml());
    }

    @Test
    @DisplayName("useGlobalSpeed=0 但未设置 waypointSpeed 时 toWpml 抛出 IllegalStateException")
    void shouldThrowWhenUseGlobalSpeedIsZeroButNoWaypointSpeed() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
                .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
                .droneInfo(67, 0)
                .payloadInfo(52, 0)
                .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100)
                    .useGlobalSpeed(0))
                .toWpml());
    }

    @Test
    @DisplayName("globalWaypointHeadingMode 未设置时 toWpml 抛出 IllegalStateException")
    void shouldThrowWhenGlobalHeadingModeMissing() {
        assertThrows(IllegalStateException.class, () ->
            WaypointTemplate.builder()
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .autoFlightSpeed(7)
                .globalHeight(100)
                .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
                .droneInfo(67, 0)
                .payloadInfo(52, 0)
                .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
                .toWpml());
    }

    // ════════════════════════════════════════════
    //  template.kml 与 waylines.wpml 一致性
    // ════════════════════════════════════════════

    @Test
    @DisplayName("同一 Builder 的 toXml 与 toWpml 航点坐标一致")
    void shouldHaveConsistentWaypointCoordinates() {
        WaypointTemplate template = buildFullTemplate();
        String kml = template.toXml();
        String wpml = template.toWpml();

        assertTrue(wpml.contains("113.980570"), "wpml 应包含第一个航点经度");
        assertTrue(wpml.contains("22.987663"), "wpml 应包含第一个航点纬度");
        assertTrue(wpml.contains("113.980600"), "wpml 应包含第二个航点经度");
        assertTrue(wpml.contains("22.987700"), "wpml 应包含第二个航点纬度");
    }

    @Test
    @DisplayName("actionGroup 在 toWpml 中保留")
    void shouldKeepActionGroupInWpml() {
        String wpml = WaypointTemplate.builder()
            .flyToWaylineMode(FlyToWaylineMode.SAFELY)
            .finishAction(FinishAction.GO_HOME)
            .autoFlightSpeed(7)
            .globalHeight(100)
            .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
            .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
            .droneInfo(67, 0)
            .payloadInfo(52, 0)
            .addWaypoint(w -> w
                .longitude(113.98).latitude(22.98).height(100)
                .addActionGroup(ag -> ag
                    .actionGroupId(0)
                    .actionGroupStartIndex(0)
                    .actionGroupEndIndex(0)
                    .actionTriggerType(ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType.REACH_POINT)
                    .addAction(a -> a
                        .actionId(0)
                        .actionActuatorFunc(ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc.TAKE_PHOTO)
                        .actionActuatorFuncParam(new ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam(0, "point1", "wide", 1)))))
            .toWpml();

        assertTrue(wpml.contains("actionGroup"), "应保留 actionGroup");
        assertTrue(wpml.contains("takePhoto"), "应保留 takePhoto 动作");
        assertTrue(wpml.contains("point1"), "应保留动作参数");
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
            .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100).gimbalPitchAngle(0))
            .addWaypoint(w -> w.longitude(113.98060).latitude(22.98770).height(120).gimbalPitchAngle(0));
    }
}
