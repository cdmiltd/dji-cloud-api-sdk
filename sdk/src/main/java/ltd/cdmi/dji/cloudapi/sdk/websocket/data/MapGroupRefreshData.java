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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code map_group_refresh} 推送的 data 结构：图层刷新通知。
 *
 * <p>调用方收到此推送后，对每个 {@code group_id}（即 {@link #ids()} 元素）调用
 * HTTP {@code GET /map/api/v1/workspaces/{workspace_id}/element-groups/{group_id}/elements}
 * 拉取元素列表——见 {@link ltd.cdmi.dji.cloudapi.sdk.http.HttpApiPath#ELEMENT_GROUPS}。
 *
 * <p>字段结构来自 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/ws/handler/MapElementWsHandler.java">
 * MapElementWsHandler.recordEvent</a> 已对接 hivemind 验证的注释：
 * {@code data = {ids: [group_id1, group_id2]}}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html">
 * DJI 地图元素 WebSocket 消息发布</a>
 *
 * @param ids 需要刷新的图层 group_id 列表
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html")
@Verified(basis = "simulator MapElementWsHandler.recordEvent 注释明确 map_group_refresh.data = {ids: [group_id]}")
public record MapGroupRefreshData(
        List<String> ids
) {
}
