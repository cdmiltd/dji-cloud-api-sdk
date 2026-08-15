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

package ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WPML 枚举类型验证：code 值与 DJI 文档一致性、fromCode 反查。
 */
class WaylineEnumTest {

    @Test
    @DisplayName("FlyToWaylineMode: safely / pointToPoint")
    void testFlyToWaylineMode() {
        assertEquals("safely", FlyToWaylineMode.SAFELY.code());
        assertEquals("pointToPoint", FlyToWaylineMode.POINT_TO_POINT.code());
        assertEquals(FlyToWaylineMode.SAFELY, FlyToWaylineMode.fromCode("safely"));
        assertThrows(IllegalArgumentException.class, () -> FlyToWaylineMode.fromCode("unknown"));
    }

    @Test
    @DisplayName("FinishAction: goHome / noAction / autoLand / gotoFirstWaypoint")
    void testFinishAction() {
        assertEquals("goHome", FinishAction.GO_HOME.code());
        assertEquals("noAction", FinishAction.NO_ACTION.code());
        assertEquals("autoLand", FinishAction.AUTO_LAND.code());
        assertEquals("gotoFirstWaypoint", FinishAction.GO_TO_FIRST_WAYPOINT.code());
        assertEquals(4, FinishAction.values().length);
    }

    @Test
    @DisplayName("ExitOnRCLost: goContinue / executeLostAction")
    void testExitOnRCLost() {
        assertEquals("goContinue", ExitOnRCLost.GO_CONTINUE.code());
        assertEquals("executeLostAction", ExitOnRCLost.EXECUTE_LOST_ACTION.code());
    }

    @Test
    @DisplayName("ExecuteRCLostAction: goBack / landing / hover")
    void testExecuteRCLostAction() {
        assertEquals("goBack", ExecuteRCLostAction.GO_BACK.code());
        assertEquals("landing", ExecuteRCLostAction.LANDING.code());
        assertEquals("hover", ExecuteRCLostAction.HOVER.code());
    }

    @Test
    @DisplayName("CoordinateMode: WGS84")
    void testCoordinateMode() {
        assertEquals("WGS84", CoordinateMode.WGS84.code());
    }

    @Test
    @DisplayName("HeightMode: EGM96 / relativeToStartPoint / aboveGroundLevel / realTimeFollowSurface")
    void testHeightMode() {
        assertEquals("EGM96", HeightMode.EGM96.code());
        assertEquals("relativeToStartPoint", HeightMode.RELATIVE_TO_START_POINT.code());
        assertEquals("aboveGroundLevel", HeightMode.ABOVE_GROUND_LEVEL.code());
        assertEquals("realTimeFollowSurface", HeightMode.REAL_TIME_FOLLOW_SURFACE.code());
        assertEquals(4, HeightMode.values().length);
    }

    @Test
    @DisplayName("PositioningType: GPS / RTKBaseStation / QianXun / Custom")
    void testPositioningType() {
        assertEquals("GPS", PositioningType.GPS.code());
        assertEquals("RTKBaseStation", PositioningType.RTK_BASE_STATION.code());
        assertEquals("QianXun", PositioningType.QIANXUN.code());
        assertEquals("Custom", PositioningType.CUSTOM.code());
        assertEquals(4, PositioningType.values().length);
    }

    @Test
    @DisplayName("ShootType: time / distance")
    void testShootType() {
        assertEquals("time", ShootType.TIME.code());
        assertEquals("distance", ShootType.DISTANCE.code());
    }

    @Test
    @DisplayName("MappingHeadingMode: fixed / followWayline")
    void testMappingHeadingMode() {
        assertEquals("fixed", MappingHeadingMode.FIXED.code());
        assertEquals("followWayline", MappingHeadingMode.FOLLOW_WAYLINE.code());
    }

    @Test
    @DisplayName("ReturnMode: singleReturnStrongest / dualReturn / tripleReturn（M300 激光雷达）")
    void testReturnMode() {
        assertEquals("singleReturnStrongest", ReturnMode.SINGLE_RETURN_STRONGEST.code());
        assertEquals("dualReturn", ReturnMode.DUAL_RETURN.code());
        assertEquals("tripleReturn", ReturnMode.TRIPLE_RETURN.code());
        assertEquals(3, ReturnMode.values().length);
        assertEquals(ReturnMode.DUAL_RETURN, ReturnMode.fromCode("dualReturn"));
        assertThrows(IllegalArgumentException.class, () -> ReturnMode.fromCode("quadReturn"));
    }

    @Test
    @DisplayName("ScanningMode: repetitive / nonRepetitive（M300 激光雷达）")
    void testScanningMode() {
        assertEquals("repetitive", ScanningMode.REPETITIVE.code());
        assertEquals("nonRepetitive", ScanningMode.NON_REPETITIVE.code());
        assertEquals(2, ScanningMode.values().length);
        assertEquals(ScanningMode.NON_REPETITIVE, ScanningMode.fromCode("nonRepetitive"));
    }

    @Test
    @DisplayName("FocusMode: firstPoint / custom（M300 可见光）")
    void testFocusMode() {
        assertEquals("firstPoint", FocusMode.FIRST_POINT.code());
        assertEquals("custom", FocusMode.CUSTOM.code());
        assertEquals(2, FocusMode.values().length);
    }

    @Test
    @DisplayName("MeteringMode: average / spot（M300 可见光）")
    void testMeteringMode() {
        assertEquals("average", MeteringMode.AVERAGE.code());
        assertEquals("spot", MeteringMode.SPOT.code());
        assertEquals(2, MeteringMode.values().length);
    }

    @Test
    @DisplayName("GimbalPitchMode: manual / usePointSetting")
    void testGimbalPitchMode() {
        assertEquals("manual", GimbalPitchMode.MANUAL.code());
        assertEquals("usePointSetting", GimbalPitchMode.USE_POINT_SETTING.code());
    }

    @Test
    @DisplayName("WaypointHeadingMode: 5 个值")
    void testWaypointHeadingMode() {
        assertEquals("followWayline", WaypointHeadingMode.FOLLOW_WAYLINE.code());
        assertEquals("manually", WaypointHeadingMode.MANUALLY.code());
        assertEquals("fixed", WaypointHeadingMode.FIXED.code());
        assertEquals("smoothTransition", WaypointHeadingMode.SMOOTH_TRANSITION.code());
        assertEquals("towardPOI", WaypointHeadingMode.TOWARD_POI.code());
        assertEquals(5, WaypointHeadingMode.values().length);
    }

    @Test
    @DisplayName("WaypointTurnMode: 4 个值")
    void testWaypointTurnMode() {
        assertEquals("coordinateTurn", WaypointTurnMode.COORDINATE_TURN.code());
        assertEquals("toPointAndStopWithDiscontinuityCurvature",
            WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE.code());
        assertEquals("toPointAndStopWithContinuityCurvature",
            WaypointTurnMode.TO_POINT_AND_STOP_WITH_CONTINUITY_CURVATURE.code());
        assertEquals("toPointAndPassWithContinuityCurvature",
            WaypointTurnMode.TO_POINT_AND_PASS_WITH_CONTINUITY_CURVATURE.code());
        assertEquals(4, WaypointTurnMode.values().length);
    }

    @Test
    @DisplayName("ActionTriggerType: 4 个值")
    void testActionTriggerType() {
        assertEquals("reachPoint", ActionTriggerType.REACH_POINT.code());
        assertEquals("betweenAdjacentPoints", ActionTriggerType.BETWEEN_ADJACENT_POINTS.code());
        assertEquals("multipleTiming", ActionTriggerType.MULTIPLE_TIMING.code());
        assertEquals("multipleDistance", ActionTriggerType.MULTIPLE_DISTANCE.code());
    }

    @Test
    @DisplayName("ActionActuatorFunc: 16 个值")
    void testActionActuatorFunc() {
        assertEquals(16, ActionActuatorFunc.values().length);
        assertEquals("takePhoto", ActionActuatorFunc.TAKE_PHOTO.code());
        assertEquals("startRecord", ActionActuatorFunc.START_RECORD.code());
        assertEquals("stopRecord", ActionActuatorFunc.STOP_RECORD.code());
        assertEquals("focus", ActionActuatorFunc.FOCUS.code());
        assertEquals("zoom", ActionActuatorFunc.ZOOM.code());
        assertEquals("customDirName", ActionActuatorFunc.CUSTOM_DIR_NAME.code());
        assertEquals("gimbalRotate", ActionActuatorFunc.GIMBAL_ROTATE.code());
        assertEquals("rotateYaw", ActionActuatorFunc.ROTATE_YAW.code());
        assertEquals("hover", ActionActuatorFunc.HOVER.code());
        assertEquals("gimbalEvenlyRotate", ActionActuatorFunc.GIMBAL_EVENLY_ROTATE.code());
        assertEquals("orientedShoot", ActionActuatorFunc.ORIENTED_SHOOT.code());
        assertEquals("accurateShoot", ActionActuatorFunc.ACCURATE_SHOOT.code());
        assertEquals("panoShot", ActionActuatorFunc.PANO_SHOT.code());
        assertEquals("recordPointCloud", ActionActuatorFunc.RECORD_POINT_CLOUD.code());
        assertEquals("megaphone", ActionActuatorFunc.MEGAPHONE.code());
        assertEquals("searchlight", ActionActuatorFunc.SEARCHLIGHT.code());
    }

    @Test
    @DisplayName("GimbalRotateMode: absoluteAngle")
    void testGimbalRotateMode() {
        assertEquals("absoluteAngle", GimbalRotateMode.ABSOLUTE_ANGLE.code());
    }

    @Test
    @DisplayName("ActionGroupMode: sequence / parallel")
    void testActionGroupMode() {
        assertEquals("sequence", ActionGroupMode.SEQUENCE.code());
        assertEquals("parallel", ActionGroupMode.PARALLEL.code());
    }

    @Test
    @DisplayName("所有枚举实现 WpmlEnum 接口")
    void testAllEnumsImplementWpmlEnum() {
        assertTrue(FlyToWaylineMode.SAFELY instanceof WpmlEnum);
        assertTrue(FinishAction.GO_HOME instanceof WpmlEnum);
        assertTrue(ActionActuatorFunc.TAKE_PHOTO instanceof WpmlEnum);
        assertTrue(GimbalRotateMode.ABSOLUTE_ANGLE instanceof WpmlEnum);
    }
}
