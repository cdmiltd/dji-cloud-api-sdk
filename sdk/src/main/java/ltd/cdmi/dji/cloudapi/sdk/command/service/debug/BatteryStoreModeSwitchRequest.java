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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.BatteryStoreMode;

/**
 * battery_store_mode_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code battery_store_mode_switch} 服务（services 通道，云→设备）的 data。
 * 用于切换电池存储模式，三 Dock 共有，Cmd 类指令（仅 services_reply，无进度事件）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#BATTERY_STORE_MODE_SWITCH}
 *
 * <p>字段依据：DJI Dock3 cmd 文档 services 请求字段被截断，字段名 {@code mode} 来自
 * coverage-review.html §1.3 描述（推断）。simulator 未解析请求参数。
 *
 * <p>枚举值依据：DJI Dock3 properties 文档明确 battery_store_mode 枚举定义
 * （1=计划模式, 2=待命模式），见 {@link BatteryStoreMode}。
 * 请求参数与 OSD 上报共用同一枚举值集。
 *
 * <p><b>类型化字段</b>：{@code mode} 字段使用类型化枚举 {@link BatteryStoreMode}，
 * 通过 Jackson {@code @JsonValue}/{@code @JsonCreator}（见枚举类）实现 DJI 协议 int 值
 * 与枚举的双向绑定。这是 SDK 中首个使用类型化枚举字段的 services POJO。
 *
 * <p>标 @Inferred：仅 mode 字段名为推断（cmd 文档 services 字段被截断）；
 * 枚举值已通过 properties 文档核实，非推断。
 *
 * <p>参考：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">DJI Dock3 远程调试（cmd）</a></li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">DJI Dock3 机场设备属性</a>（battery_store_mode 枚举定义）</li>
 * </ul>
 *
 * @see BatteryStoreMode
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#BATTERY_STORE_MODE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Inferred(
    reason = "DJI Dock3 cmd 文档 services 请求字段被截断，字段名 mode 来自 coverage-review.html §1.3 描述；枚举值已通过 properties 文档核实（1=计划模式, 2=待命模式），非推断",
    verifyPoint = "真机验证 services 请求的字段名 mode 是否正确（值域已核实）"
)
public record BatteryStoreModeSwitchRequest(
    /** 电池存储模式（类型化枚举，1=计划模式/2=待命模式，Jackson 自动绑定 int↔enum，见 {@link BatteryStoreMode}） */
    BatteryStoreMode mode
) {}
