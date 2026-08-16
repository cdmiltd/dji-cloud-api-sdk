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

package ltd.cdmi.dji.cloudapi.sdk.command.service;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 无参数指令通用 Request。
 *
 * <p>DJI Cloud API services 通道中，部分指令（如 cover_open/drone_close/device_reboot 等）
 * 的请求 data 为空对象 {@code {}}，不携带任何业务字段。本 record 作为这些指令的通用 Request，
 * 避免为每个无参数指令创建空 record 文件。
 *
 * <p><b>适用指令清单（27 个，含 1 个 Pilot @Verified）</b>：
 * <ul>
 *   <li><b>机场硬件控制 — Job（12 个，异步含进度，@Verified）</b>：cover_open/cover_close/cover_force_close/
 *       drone_open/drone_close/charge_open/charge_close/device_reboot/device_format/drone_format/
 *       putter_open/putter_close</li>
 *   <li><b>机场硬件控制 — Cmd（5 个，同步，@Verified）</b>：debug_mode_open/debug_mode_close/
 *       supplement_light_open/supplement_light_close/battery_maintenance_switch</li>
 *   <li><b>飞行控制（2 个，@Verified）</b>：fly_to_point_stop/flight_authority_grab</li>
 *   <li><b>航线任务（6 个，@Verified）</b>：flighttask_pause/flighttask_recovery/return_home/
 *       return_home_cancel/flight_setup_abort/in_flight_wayline_cancel</li>
 *   <li><b>DRC 模式（1 个，@Verified）</b>：drc_mode_exit</li>
 *   <li><b>Pilot 上云 — 指令飞行（1 个，@Verified）</b>：poi_mode_exit（退出 POI 环绕模式，data=null）</li>
 * </ul>
 *
 * <p><b>不适用（已有专用 POJO，8 个）</b>：esim_activate/esim_operator_switch/sim_slot_switch/
 *       rtk_calibration/battery_store_mode_switch/alarm_state_switch/
 *       air_conditioner_mode_switch/sdr_workmode_switch
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 services 通道</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 */
@Verified(basis = "simulator RemoteDebugSimulator/FlightCommandSimulator/WaylineTaskSimulator/AuthFlowHandler 已对接 hivemind 验证 26 个无参数指令 + DJI 文档验证 poi_mode_exit Data=null")
public record NoParameterRequest() {}
