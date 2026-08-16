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
 * events 通道自定义飞行区类事件 POJO。
 *
 * <p>本包含 2 个自定义飞行区相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea.FlightAreasDroneLocationData FlightAreasDroneLocationData}
 *       — flight_areas_drone_location 飞行器位置告警推送（@Verified，need_reply=0 单向通知，
 *       含嵌套 DroneLocationItem）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea.FlightAreasSyncProgressData FlightAreasSyncProgressData}
 *       — flight_areas_sync_progress 文件同步进度上报（@Verified，含嵌套 SyncFile，
 *       status 枚举 fail/switch_fail/synchronized/synchronizing/wait_sync）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线任务 events（含自定义飞行区）</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea;
