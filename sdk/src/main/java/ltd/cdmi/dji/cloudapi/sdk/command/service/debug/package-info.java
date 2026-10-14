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
 * services 通道设备控制类请求 POJO（远程调试 Cmd 指令）。
 *
 * <p>本包含设备硬件控制服务的 Request/Reply record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.RtkCalibrationRequest RtkCalibrationRequest}
 *       — rtk_calibration 一键标定（@Verified，O4 cmd 页 devices[] 结构）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.LowPowerSwitchRequest LowPowerSwitchRequest}/{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.LowPowerSwitchReply LowPowerSwitchReply}
 *       — low_power_switch 低电量模式开关（@Verified，O4 cmd 页 action + {result, output{status}}）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryMaintenanceSwitchRequest BatteryMaintenanceSwitchRequest}
 *       — battery_maintenance_switch 电池保养开关（action）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryStoreModeSwitchRequest BatteryStoreModeSwitchRequest}
 *       — battery_store_mode_switch 电池存储模式切换（@Inferred）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.AlarmStateSwitchRequest AlarmStateSwitchRequest}
 *       — alarm_state_switch 报警状态切换（@Inferred）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.AirConditionerModeSwitchRequest AirConditionerModeSwitchRequest}
 *       — air_conditioner_mode_switch 空调模式切换（@Inferred）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.SdrWorkmodeSwitchRequest SdrWorkmodeSwitchRequest}
 *       — sdr_workmode_switch SDR 工作模式切换（@Inferred）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;
