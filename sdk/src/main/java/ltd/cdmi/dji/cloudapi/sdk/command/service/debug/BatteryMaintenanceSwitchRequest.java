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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * battery_maintenance_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code battery_maintenance_switch} 服务（services 通道，云→设备）的 data。
 * 用于切换电池保养开关，三 Dock 共有，Cmd 类指令（仅 services_reply，无进度事件）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#BATTERY_MAINTENANCE_SWITCH}
 *
 * <p>字段依据：DJI Dock1「远程调试（cmd）」页 services 请求 data 字段名 {@code action}
 * （enum_int {0:关闭, 1:开启}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Verified(basis = "DJI Dock1 远程调试（cmd）页 battery_maintenance_switch services 请求 data：字段名 action（enum_int {0:关闭,1:开启}）")
public record BatteryMaintenanceSwitchRequest(
    /** 电池保养动作（enum_int，0=关闭, 1=开启） */
    Integer action
) {}
