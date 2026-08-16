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

package ltd.cdmi.dji.cloudapi.sdk.command.service.esim;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * esim_operator_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code esim_operator_switch} 服务（services 通道，云→设备）的 data。
 * 用于切换 eSIM 运营商，Dock2/Dock3 共有，Job 类指令（有进度事件）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#ESIM_OPERATOR_SWITCH}
 *
 * <p>字段依据：DJI Dock3 cmd 文档 services 请求部分（L525-L539）确认字段：
 * <ul>
 *   <li>{@code imei} (text) — dongle imei，标识要操作的 Dongle</li>
 *   <li>{@code device_type} (enum_string) — 目标设备类型 {"dock":"设置机场 Dongle","drone":"设置飞行器 Dongle"}</li>
 *   <li>{@code esim_operator} (enum_int) — 切换的目标运营商 {1:"移动", 2:"联通", 3:"电信"}</li>
 * </ul>
 *
 * <p>services_reply 仅有 result（无 output），events 进度含 output.status + output.progress.percent。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Verified(basis = "DJI Dock3 cmd 文档 L525-L539 services 请求字段确认")
public record EsimOperatorSwitchRequest(
    /** dongle imei，标识要操作的 Dongle */
    String imei,
    /** 目标设备类型（enum_string: "dock"=设置机场 Dongle, "drone"=设置飞行器 Dongle） */
    String deviceType,
    /** 切换的目标运营商（enum_int: 1=移动, 2=联通, 3=电信） */
    int esimOperator
) {}
