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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_execute 指令请求的图传连接拓扑。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 请求 data 中
 * {@code multi_dock_task.wireless_link_topo} 字段。
 *
 * <p><b>secretCode 字段</b>：simulator 仅记录 {@code secret_code.size()}，
 * 未明确字段类型（可能是 String 密钥或 Object 结构），本 POJO 用 String 兜底。
 *
 * @see MultiDockTask#wirelessLinkTopo()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.parseMultiDockTask 已对接 hivemind 验证")
@Inferred(
    reason = "secret_code 字段类型 simulator 仅记录 size() 未明确类型，用 String 兜底",
    verifyPoint = "DJI 文档确认 secret_code 是 String 密钥还是 Object 结构"
)
public record WirelessLinkTopo(
    String secretCode,
    CenterNode centerNode,
    List<LeafNode> leafNodes
) {}
