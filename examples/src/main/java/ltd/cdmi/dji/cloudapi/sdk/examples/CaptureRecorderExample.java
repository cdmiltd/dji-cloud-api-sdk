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

import java.util.Set;

import ltd.cdmi.dji.cloudapi.sdk.capture.CaptureConfig;
import ltd.cdmi.dji.cloudapi.sdk.capture.CaptureRecorder;
import ltd.cdmi.dji.cloudapi.sdk.model.DockModel;
import ltd.cdmi.dji.cloudapi.sdk.model.DroneModel;

/**
 * 示例 7：真机消息采集。
 *
 * <p>演示 CaptureRecorder 的初始化（含自定义配置）、设备注册、消息采集流程。
 * 采集到的消息按 网关型号-飞行器型号/方向/method 自动分类存储为 JSON 文件。
 *
 * <p>关键点：
 * <ul>
 *   <li>direction 标签由调用方定义（作为目录名），推荐按 javadoc 约定：
 *       inbound=真机→平台，outbound=平台→真机</li>
 *   <li>文件异步写入（守护线程），常驻进程无需关心；短命进程退出前需等待落盘</li>
 *   <li>敏感字段（sn/license/token 等）自动脱敏为 ***，每方法每机型限量去重</li>
 * </ul>
 */
public class CaptureRecorderExample {

    public static void main(String[] args) throws InterruptedException {
        // 1. 开启采集：可用 CaptureConfig.defaults()（输出 dji-capture/，每方法每机型 5 份），
        //    或自定义配置演示完整配置面（本例用独立目录避免污染工作目录）
        CaptureConfig config = new CaptureConfig(
                true,                                    // enabled
                java.nio.file.Path.of("dji-capture-example"), // captureDir
                5,                                       // maxSamplesPerMethod
                Set.of("sn", "app_id", "app_license", "security_token")); // maskFields
        CaptureRecorder.enable(config);
        System.out.println("采集已开启: " + CaptureRecorder.isEnabled()
                + "，输出目录: " + config.captureDir().toAbsolutePath());

        // 2. 注册设备型号映射（SN → 网关 + 飞行器），未注册 SN 以 SN-xxx 目录兜底
        CaptureRecorder.registerDevice("7UUXN1Q00A008W", DockModel.DOCK3, DroneModel.M4D);
        System.out.println("已注册设备: SN=7UUXN1Q00A008W, Dock=" + DockModel.DOCK3.shortName()
                + ", Drone=" + DroneModel.M4D.shortName());

        // 3. 在 MQTT 消息处理点插入采集调用（实际嵌入 onMessage / onReply 回调）
        String topic = "thing/product/7UUXN1Q00A008W/services";

        // outbound：平台 → 真机（services 指令下发）
        String outboundPayload = """
                {"method":"fly_to_point","tid":"t001","bid":"b001","data":{
                  "fly_to_id":"FT001","max_speed":10,
                  "points":[{"longitude":113.98,"latitude":22.98,"height":50.0}]
                }}""";
        CaptureRecorder.capture(topic, "outbound", outboundPayload);

        // inbound：真机 → 平台（services_reply，method/tid/bid 与请求一致，data.result=0）
        String inboundPayload = """
                {"method":"fly_to_point","tid":"t001","bid":"b001",
                 "timestamp":1700000000000,"data":{"result":0}}""";
        CaptureRecorder.capture(topic, "inbound", inboundPayload);

        System.out.println("已采集 2 条消息（outbound 指令 + inbound 回复）");
        System.out.println("文件: dji-capture-example/Dock3-M4D/{outbound,inbound}/fly_to_point_*.json");

        // 4. 文件为异步写入，示例进程即将退出，稍等落盘（常驻进程无需此步）
        Thread.sleep(300);
        CaptureRecorder.disable();
        System.out.println("采集已关闭: " + !CaptureRecorder.isEnabled());
    }
}
