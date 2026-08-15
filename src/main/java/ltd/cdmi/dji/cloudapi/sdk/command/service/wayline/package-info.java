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

/**
 * services 通道航线任务类指令 POJO。
 *
 * <p>本包含航线准备/执行/暂停/恢复/撤销/停止/返航等指令的 Request record。
 * Reply 统一使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}。
 *
 * <p>嵌套 record（FlighttaskFile/ReadyConditions/ExecutableConditions/BreakPoint/
 * MultiDockTask/WirelessLinkTopo/CenterNode/LeafNode/DockInfo）随主 Request 同包。
 *
 * <p>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission} 为跨包共享 record，
 * 保留在 command/service 根目录。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;
