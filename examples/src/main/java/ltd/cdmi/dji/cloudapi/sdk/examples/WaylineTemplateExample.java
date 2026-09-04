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

package ltd.cdmi.dji.cloudapi.sdk.examples;

import ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate;
import ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.KmzContent;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;

/**
 * 示例 6：WPML 航线模板生成。
 *
 * <p>演示 WaypointTemplate.builder 构建航点模板（含拍照动作组），
 * 同一 Builder 派生三种产物：template.kml / waylines.wpml / KMZ，
 * 最后解包 KMZ 验证往返闭环。
 *
 * <p>关键点：
 * <ul>
 *   <li>Builder 状态在 toXml()/toWpml()/toKmz() 后保留，构建一次即可连续派生，
 *       无需重复 Builder 链</li>
 *   <li>droneInfo/payloadInfo 传入 DJI (type, subType) 数值，与 DroneModel 等
 *       设备型号枚举同一编码空间</li>
 *   <li>KMZ = ZIP(template.kml + waylines.wpml)，WpmlCodec.fromKmz 可解包验证</li>
 * </ul>
 */
public class WaylineTemplateExample {

    public static void main(String[] args) {
        // 1. 构建航点模板（2 个航点，第 1 个航点带 REACH_POINT 触发的拍照动作）
        WaypointTemplate template = WaypointTemplate.builder()
                .author("SDK Example")
                .createTime(System.currentTimeMillis())
                .flyToWaylineMode(FlyToWaylineMode.SAFELY)
                .finishAction(FinishAction.GO_HOME)
                .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
                .executeRCLostAction(ExecuteRCLostAction.HOVER)
                .takeOffSecurityHeight(20)
                .globalTransitionalSpeed(8)
                .globalRTHHeight(100)
                .droneInfo(67, 0)       // M30（DJI type=67, subType=0）
                .payloadInfo(52, 0)     // M30 相机（DJI payload type=52）
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
                .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100)
                        .addActionGroup(ag -> ag
                                .actionGroupId(0)
                                .actionGroupStartIndex(0)
                                .actionGroupEndIndex(0)
                                .actionTriggerType(ActionTriggerType.REACH_POINT)
                                .addAction(a -> a.actionId(0)
                                        .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                                        .actionActuatorFuncParam(new TakePhotoParam(
                                                0, "photo1", "wide", 1)))))
                .addWaypoint(w -> w.longitude(113.99000).latitude(22.987663).height(100));

        // 2. 同一 Builder 派生三种产物
        String templateKml = template.toXml();
        String waylinesWpml = template.toWpml();
        byte[] kmz = template.toKmz();

        System.out.println("===== template.kml (前 500 字符) =====");
        System.out.println(templateKml.substring(0, Math.min(500, templateKml.length())) + "...");

        System.out.println("\n===== 产物 =====");
        System.out.println("template.kml 长度: " + templateKml.length());
        System.out.println("waylines.wpml 长度: " + waylinesWpml.length());
        System.out.println("KMZ 大小: " + kmz.length + " bytes");

        // 3. 解包 KMZ 验证往返闭环（生成 → 打包 → 解包）
        KmzContent content = WpmlCodec.fromKmz(kmz);
        System.out.println("template.kml 存在: " + (content.templateKml() != null));
        System.out.println("waylines.wpml 存在: " + (content.waylinesWpml() != null));
    }
}
