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
 * events 通道航线任务类事件 POJO。
 *
 * <p>本包含 5 个航线任务相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.FlighttaskProgressData FlighttaskProgressData}
 *       — flighttask_progress 航线任务进度上报（@Verified，含嵌套 Output/Ext/Progress/BreakPoint）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.FlighttaskReadyData FlighttaskReadyData}
 *       — flighttask_ready 任务就绪通知（@Verified，data 含 flight_ids 数组）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.InFlightWaylineProgressData InFlightWaylineProgressData}
 *       — in_flight_wayline_progress 空中下发航线状态上报（@Verified，含嵌套 Progress）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.ReturnHomeInfoData ReturnHomeInfoData}
 *       — return_home_info 返航信息（@Verified + @Inferred，home_dock_sn/multi_dock_home_info
 *       仅 Dock2/3 支持待真机验证蛙跳场景）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.DeviceExitHomingNotifyData DeviceExitHomingNotifyData}
 *       — device_exit_homing_notify 设备返航退出状态通知（@Verified + @Inferred，reason 字段类型
 *       按 enum_int 使用 int 待真机验证）</li>
 * </ul>
 *
 * <p>跨包共享 record：{@link ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint}（返航轨迹点），
 * 定义在 event 根包。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线任务 events</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;
