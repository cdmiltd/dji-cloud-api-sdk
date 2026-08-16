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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_execute 指令请求的图传连接拓扑。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 请求 data 中
 * {@code multi_dock_task.wireless_link_topo} 字段。
 *
 * <p><b>secretCode 字段</b>：DJI 文档定义为 {@code array<int>}，固定 28 元素，
 * 从飞行器的设备属性中获取。
 *
 * @see MultiDockTask#wirelessLinkTopo()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline 文档 — secret_code 为 array<int>，固定 28 元素")
public record WirelessLinkTopo(
    List<Integer> secretCode,
    CenterNode centerNode,
    List<LeafNode> leafNodes
) {}
