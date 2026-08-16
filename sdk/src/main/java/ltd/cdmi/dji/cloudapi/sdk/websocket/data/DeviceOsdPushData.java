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

package ltd.cdmi.dji.cloudapi.sdk.websocket.data;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code device_osd} 推送的 data 结构：设备遥感遥测（定频推送）。
 *
 * <p>字段结构来自 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/ws/handler/SituationAwarenessWsHandler.java">
 * SituationAwarenessWsHandler.recordEvent</a> 已对接 hivemind 验证的注释：
 * {@code data = {sn, host: {latitude, longitude, height, ...}}}。
 *
 * <p>与 MQTT 通道的 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd}/{@code DockOsd}
 * 不同：device_osd 是 Pilot 上云 WebSocket 推送，{@code host} 子结构嵌套位置/姿态/速度遥测。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html">
 * DJI 态势感知 WebSocket 消息发布</a>
 *
 * @param sn   设备序列号
 * @param host 位置/姿态/速度遥测子结构（见 {@link DeviceOsdHost}）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html")
@Verified(basis = "simulator SituationAwarenessWsHandler.recordEvent 注释明确 device_osd.data = {sn, host: {...}}")
public record DeviceOsdPushData(
        String sn,
        DeviceOsdHost host
) {
}
