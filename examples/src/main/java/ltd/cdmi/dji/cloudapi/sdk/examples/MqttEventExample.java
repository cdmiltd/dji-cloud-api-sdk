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

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;
import ltd.cdmi.dji.cloudapi.sdk.command.event.flight.TakeoffToPointProgressData;
import ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.FlighttaskProgressData;

/**
 * 示例 2：MQTT events 通道事件解析。
 *
 * <p>模拟收到两条 events 事件（flighttask_progress / takeoff_to_point_progress），
 * 演示事件解析与 tid 回复。events 通道与 services 通道信封结构一致，
 * 差异仅在于回复用 events_reply。
 *
 * <p>关键点：
 * <ul>
 *   <li>{@code flighttask_progress} 的 data 必填 {@code result}，
 *       output 内含 status / progress（{current_step, percent} record）/ ext</li>
 *   <li>{@code takeoff_to_point_progress}（need_reply=1）data 顶层平铺
 *       status/result/flight_id/track_id 等字段</li>
 *   <li>events_reply 的 tid 必须与原始 event 一致</li>
 * </ul>
 */
public class MqttEventExample {

    public static void main(String[] args) {
        String[] payloads = {
            """
            {"method":"flighttask_progress","tid":"t101","bid":"b101","data":{
              "result":0,
              "output":{
                "status":"in_progress",
                "progress":{"current_step":1,"percent":50},
                "ext":{"current_waypoint_index":3,"flight_id":"f101","track_id":"tr101"}
              }
            }}""",
            """
            {"method":"takeoff_to_point_progress","tid":"t102","data":{
              "result":0,
              "status":"task_finish",
              "flight_id":"f102",
              "track_id":"tr102",
              "way_point_index":1,
              "remaining_distance":25.5,
              "remaining_time":8.0
            }}"""
        };

        for (String payload : payloads) {
            System.out.println("========== 收到 Event ==========");
            String method = DjiMessage.extractMethod(payload);
            System.out.println("method = " + method);

            switch (method) {
                case "flighttask_progress" -> {
                    var msg = DjiMessage.parse(payload, FlighttaskProgressData.class);
                    System.out.println("  result = " + msg.data().result());
                    System.out.println("  status = " + msg.data().output().status());
                    // progress 是 record（current_step/percent），非数字
                    if (msg.data().output().progress() != null) {
                        System.out.println("  currentStep = "
                                + msg.data().output().progress().currentStep());
                        System.out.println("  percent = "
                                + msg.data().output().progress().percent());
                    }
                    // ext 可能为 null
                    if (msg.data().output().ext() != null) {
                        System.out.println("  currentWaypointIndex = "
                                + msg.data().output().ext().currentWaypointIndex());
                        System.out.println("  flightId = "
                                + msg.data().output().ext().flightId());
                    }
                }
                case "takeoff_to_point_progress" -> {
                    var msg = DjiMessage.parse(payload, TakeoffToPointProgressData.class);
                    System.out.println("  result = " + msg.data().result());
                    System.out.println("  status = " + msg.data().status());
                    System.out.println("  flightId = " + msg.data().flightId());
                    System.out.println("  trackId = " + msg.data().trackId());
                    System.out.println("  remainingDistance = "
                            + msg.data().remainingDistance());
                }
                default -> System.out.println("  未处理的 event: " + method);
            }
            // events_reply：tid 与原始 event 一致（need_reply=1 的事件必须回复）
            System.out.println("  events_reply tid = " + DjiMessage.extractTid(payload) + ", code = 0");
        }
    }
}
