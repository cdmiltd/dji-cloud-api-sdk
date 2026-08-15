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
 * requests 通道指令 POJO。
 *
 * <p>本包含 DJI Cloud API {@code requests} 通道指令的请求 data 与回复 data record。
 * 涵盖机场注册、存储配置、航线进度查询、限飞区获取等指令。
 *
 * <h3>子包结构</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.request.registration registration/} —
 *       机场注册流程（config/airport_bind_status/airport_organization_get/airport_organization_bind）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.request.config config/} —
 *       存储配置（storage_config_get）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.request.wayline wayline/} —
 *       航线任务（flighttask_progress_get）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea flightarea/} —
 *       限飞区（flight_areas_get）</li>
 * </ul>
 *
 * <p>DJI 文档：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.request.registration
 * @see ltd.cdmi.dji.cloudapi.sdk.command.request.config
 * @see ltd.cdmi.dji.cloudapi.sdk.command.request.wayline
 * @see ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea
 */
package ltd.cdmi.dji.cloudapi.sdk.command.request;
