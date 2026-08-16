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
 * {@code device_osd} 推送 data 的 {@code host} 子结构（设备位置/姿态/速度遥测）。
 *
 * <p>字段来自 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/ws/handler/SituationAwarenessWsHandler.java">
 * SituationAwarenessWsHandler.recordEvent</a> 已对接 hivemind 验证并访问的 7 个字段。
 * {@code host} 可能还含更多字段（如电池、云台姿态等），SDK 未列入——靠
 * {@code FAIL_ON_UNKNOWN_PROPERTIES=false} 容忍协议字段增量，不影响已定义字段的反序列化。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html">
 * DJI 态势感知 WebSocket 消息发布</a>
 *
 * @param latitude        纬度（JSON {@code latitude}）
 * @param longitude       经度（JSON {@code longitude}）
 * @param height          相对起飞点高度（JSON {@code height}，米）
 * @param attitudeHead    航向角（JSON {@code attitude_head}，度）
 * @param elevation       海拔高度（JSON {@code elevation}，米）
 * @param horizontalSpeed 水平速度（JSON {@code horizontal_speed}，米/秒）
 * @param verticalSpeed   垂直速度（JSON {@code vertical_speed}，米/秒）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html")
@Verified(basis = "simulator SituationAwarenessWsHandler.recordEvent 已访问的 device_osd.host 7 个字段（latitude/longitude/height/attitude_head/elevation/horizontal_speed/vertical_speed）")
public record DeviceOsdHost(
        double latitude,
        double longitude,
        double height,
        double attitudeHead,
        double elevation,
        double horizontalSpeed,
        double verticalSpeed
) {
}
