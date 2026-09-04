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
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.flight.FlyToPointRequest;
import ltd.cdmi.dji.cloudapi.sdk.command.service.flight.TakeoffToPointRequest;
import ltd.cdmi.dji.cloudapi.sdk.protocol.envelope.ReplyEnvelope;

/**
 * 示例 1：MQTT services 通道消息处理。
 *
 * <p>模拟收到三条 services 指令（fly_to_point / takeoff_to_point / cover_open），
 * 演示 extractMethod → switch → parse → ReplyEnvelope 回复的完整流程。
 * 无需 MQTT broker，使用硬编码 JSON 模拟真实消息。
 *
 * <p>关键点：
 * <ul>
 *   <li>services_reply 是<b>完整信封</b>：method 与请求一致，tid/bid 原样回显，
 *       data.result=0 表示成功（{@link ReplyEnvelope}）</li>
 *   <li>无参数指令（cover_open 等 27 个）的 data 为 {@code {}}，用
 *       {@link NoParameterRequest} 空记录承接</li>
 * </ul>
 */
public class MqttServiceExample {

    public static void main(String[] args) {
        // 模拟收到的三条 MQTT services 消息（平台下发指令 → 机场执行）
        String[] payloads = {
            """
            {"method":"fly_to_point","tid":"t001","bid":"b001","data":{
              "fly_to_id":"FT001","max_speed":10,
              "points":[{"longitude":113.98,"latitude":22.98,"height":50.0}]
            }}""",
            """
            {"method":"takeoff_to_point","tid":"t002","bid":"b002","data":{
              "flight_id":"TK001","target_latitude":22.98,"target_longitude":113.98,
              "target_height":50.0,"security_takeoff_height":30.0
            }}""",
            """
            {"method":"cover_open","tid":"t003","bid":"b003","data":{}}"""
        };

        for (String payload : payloads) {
            System.out.println("========== 收到 MQTT services 指令 ==========");
            // 1. 先 peek method（不解析 data），决定用哪个 POJO 反序列化
            String method = DjiMessage.extractMethod(payload);
            System.out.println("method = " + method);

            // 2. switch 路由，每个 case 一行 parse → 类型安全访问字段
            switch (method) {
                case "fly_to_point" -> {
                    var msg = DjiMessage.parse(payload, FlyToPointRequest.class);
                    System.out.println("  flyToId = " + msg.data().flyToId());
                    System.out.println("  maxSpeed = " + msg.data().maxSpeed());
                    System.out.println("  points[0].height = " + msg.data().points().get(0).height());
                    sendReply(msg, method);
                }
                case "takeoff_to_point" -> {
                    var msg = DjiMessage.parse(payload, TakeoffToPointRequest.class);
                    System.out.println("  flightId = " + msg.data().flightId());
                    System.out.println("  targetHeight = " + msg.data().targetHeight());
                    sendReply(msg, method);
                }
                case "cover_open" -> {
                    // 无参数指令：data 为 {}，用 NoParameterRequest 空记录承接
                    var msg = DjiMessage.parse(payload, NoParameterRequest.class);
                    System.out.println("  data = {}（NoParameterRequest 空记录）");
                    sendReply(msg, method);
                }
                default -> System.out.println("  未处理的 method: " + method);
            }
        }
    }

    /**
     * 构造 services_reply 完整信封并发送（示例中以打印代替 MQTT publish）。
     *
     * <p>DJI services_reply 协议形态（{@link ReplyEnvelope}，@Verified）：
     * method 与请求一致、tid/bid 原样回显、data.result=0 表示成功。
     * 注意：不能用 {@code new NoOutputReply()} 直接序列化当回复——
     * 空记录序列化为 {@code {}}，缺少 result 字段。
     */
    private static <T> void sendReply(DjiMessage<T> msg, String method) {
        String reply = MessageCodec.toJson(new ReplyEnvelope(
                msg.tid(), msg.bid(), System.currentTimeMillis(), method,
                new ReplyEnvelope.ReplyData(0, null)));
        System.out.println("  services_reply → thing/product/{sn}/services_reply");
        System.out.println("  reply = " + reply);
    }
}
