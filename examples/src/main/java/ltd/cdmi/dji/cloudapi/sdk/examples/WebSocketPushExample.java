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

import ltd.cdmi.dji.cloudapi.sdk.websocket.WsBizCode;
import ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.DeviceOsdPushData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.MapElementPushData;
import ltd.cdmi.dji.cloudapi.sdk.websocket.data.WsEmptyData;

/**
 * 示例 3：WebSocket 推送消息解析。
 *
 * <p>模拟收到三条 WebSocket 推送（device_osd / map_element_create / device_online），
 * 演示 extractBizCode → switch → parse 流程。
 * 与 MQTT 通道的 extractMethod + parse 模式完全对称。
 *
 * <p>关键点：
 * <ul>
 *   <li>device_osd 的 data = {sn, host:{latitude, longitude, height, ...}}（态势感知）</li>
 *   <li>map_element_* 的 data = {id, group_id, name, resource}，resource 子结构
 *       SDK 未固化（Object 持有），由调用方按业务解析</li>
 *   <li>device_online / device_offline / device_update_topo 的 data 为空对象 {}，
 *       用 {@link WsEmptyData} 空记录承接，仅作触发信号（刷新设备拓扑）</li>
 * </ul>
 */
public class WebSocketPushExample {

    public static void main(String[] args) {
        String[] payloads = {
            // 态势感知：设备 OSD 定频推送，data = {sn, host:{遥测字段}}
            """
            {"biz_code":"device_osd","version":"1.0","timestamp":1700000000000,"data":{
              "sn":"7UUXN1Q00A008W",
              "host":{"latitude":22.98,"longitude":113.98,"height":50.0,
                      "attitude_head":90.0,"elevation":120.0,
                      "horizontal_speed":3.5,"vertical_speed":0.5}
            }}""",
            // 地图元素：新增通知，data = {id, group_id, name, resource}
            """
            {"biz_code":"map_element_create","version":"1.0","timestamp":1700000002000,"data":{
              "id":"el-001","group_id":"grp-001","name":"作业区 A",
              "resource":{"type":"polygon"}
            }}""",
            // 态势感知：设备上线通知，data 为空对象 {}（仅触发信号）
            """
            {"biz_code":"device_online","version":"1.0","timestamp":1700000001000,"data":{}}"""
        };

        for (String payload : payloads) {
            System.out.println("========== 收到 WS 推送 ==========");
            String bizCode = WsPushMessage.extractBizCode(payload);
            System.out.println("biz_code = " + bizCode);

            switch (WsBizCode.fromCode(bizCode).orElse(null)) {
                case DEVICE_OSD -> {
                    var msg = WsPushMessage.parse(payload, DeviceOsdPushData.class);
                    System.out.println("  sn = " + msg.data().sn());
                    System.out.println("  host.latitude = " + msg.data().host().latitude());
                    System.out.println("  host.longitude = " + msg.data().host().longitude());
                    System.out.println("  host.horizontalSpeed = " + msg.data().host().horizontalSpeed());
                }
                case MAP_ELEMENT_CREATE, MAP_ELEMENT_UPDATE, MAP_ELEMENT_DELETE -> {
                    var msg = WsPushMessage.parse(payload, MapElementPushData.class);
                    System.out.println("  id = " + msg.data().id());
                    System.out.println("  groupId = " + msg.data().groupId());
                    System.out.println("  name = " + msg.data().name());
                    // resource 子结构 SDK 未固化（Object 持有，实际为 Map），按业务自行解析
                    System.out.println("  resource = " + msg.data().resource());
                }
                case DEVICE_ONLINE, DEVICE_OFFLINE, DEVICE_UPDATE_TOPO -> {
                    var msg = WsPushMessage.parse(payload, WsEmptyData.class);
                    System.out.println("  data = {}（WsEmptyData 空记录），触发 HTTP 拓扑刷新");
                }
                case null -> System.out.println("  未知 biz_code: " + bizCode);
                default -> System.out.println("  未处理: " + bizCode);
            }
        }
    }
}
