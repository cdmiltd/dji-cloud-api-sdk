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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_execute 指令请求的蛙跳机场信息。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 请求 data 中
 * {@code multi_dock_task.dock_infos} 数组元素。
 *
 * <p><b>dockType 字段</b>：simulator 用 {@code asText()} 解析，类型为 String。
 *
 * @see MultiDockTask#dockInfos()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.parseMultiDockTask 已对接 hivemind 验证")
public record DockInfo(
    String sn,
    String dockType,
    Integer index,
    Double latitude,
    Double longitude,
    Double height
) {}
