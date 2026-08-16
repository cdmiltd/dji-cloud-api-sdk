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
 * 地图元素变更推送的 data 结构（{@code map_element_create} / {@code map_element_update} /
 * {@code map_element_delete} 共用同一 POJO）。
 *
 * <p>simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/ws/handler/MapElementWsHandler.java">
 * MapElementWsHandler.recordEvent</a> 已对接 hivemind 验证并访问 {@code id} / {@code group_id}
 * / {@code name} 字段；{@code resource} 字段未访问具体子字段，SDK 用 {@link Object} 持有，
 * 由调用方按业务需要自行解析（与 {@link ltd.cdmi.dji.cloudapi.sdk.http.StsCredentials#credentials}
 * 同样的设计约定——不固化解构未明确的子结构）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html">
 * DJI 地图元素 WebSocket 消息发布</a>
 *
 * @param id       地图元素 ID
 * @param groupId  所属图层 group_id（JSON 字段 {@code group_id}）
 * @param name     元素名称
 * @param resource 元素资源（子结构待真机/文档确认，SDK 用 Object 持有）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html")
@Verified(basis = "simulator MapElementWsHandler.recordEvent 注释明确 map_element_*.data = {id, group_id, name, resource}；resource 子结构未访问")
public record MapElementPushData(
        String id,
        String groupId,
        String name,
        Object resource
) {
}
