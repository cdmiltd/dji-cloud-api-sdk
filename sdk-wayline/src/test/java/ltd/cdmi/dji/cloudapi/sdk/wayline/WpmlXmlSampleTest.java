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

import org.junit.jupiter.api.Test;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
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
 * 打印生成的 template.kml XML 样例（人工验证用）。
 */
class WpmlXmlSampleTest {

    @Test
    void printSampleXml() {
        String xml = WaypointTemplate.builder()
            .author("TestPilot")
            .createTime(1637600807044L)
            .updateTime(1637600875837L)
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

        System.out.println("========== Generated template.kml ==========");
        System.out.println(xml);
        System.out.println("============================================");
    }
}
