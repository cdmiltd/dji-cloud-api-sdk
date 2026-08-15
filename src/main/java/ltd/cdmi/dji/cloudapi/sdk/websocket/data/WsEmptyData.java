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
 * WebSocket 推送的空 data 承接 record（device_online / device_offline / device_update_topo 共用）。
 *
 * <p>这三个 biz_code 的 {@code data} 字段为空对象 {@code {}}——simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/ws/handler/SituationAwarenessWsHandler.java">
 * SituationAwarenessWsHandler</a> 仅据此触发"获取设备拓扑列表"HTTP 调用，不访问 data 任何字段。
 *
 * <p>与 MQTT 通道的 {@code NoParameterRequest} 对称：空 record 承接无字段 data，
 * 调用方通过 {@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec#parseWs} 拿到类型安全
 * 的 {@code WsPushMessage<WsEmptyData>}，无需以 {@code Object} 持有 data。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html">
 * DJI 态势感知 WebSocket 消息发布</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/situation-awareness/message-push.html")
@Verified(basis = "simulator SituationAwarenessWsHandler.recordEvent 注释明确 device_online/offline/update_topo 的 data 为空对象")
public record WsEmptyData() {
}
