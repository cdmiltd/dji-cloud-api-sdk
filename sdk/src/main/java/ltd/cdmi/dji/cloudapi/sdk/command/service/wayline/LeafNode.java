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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_execute 指令请求的叶子节点信息。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 请求 data 中
 * {@code multi_dock_task.wireless_link_topo.leaf_nodes} 数组元素。
 *
 * <p><b>本 record 为空</b>：simulator {@code WaylineTaskSimulator.parseMultiDockTask}
 * 仅记录 {@code leaf_nodes.size()}，未解析具体字段。待 DJI 文档确认字段后扩展。
 *
 * @see WirelessLinkTopo#leafNodes()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.parseMultiDockTask 已对接 hivemind 验证：仅记录 leaf_nodes.size()")
@Inferred(
    reason = "simulator 仅记录 leaf_nodes.size() 未解析具体字段",
    verifyPoint = "DJI 文档确认 leaf_nodes 数组元素的字段结构"
)
public record LeafNode() {}
