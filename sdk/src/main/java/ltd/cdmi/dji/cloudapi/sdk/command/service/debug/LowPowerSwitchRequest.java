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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * low_power_switch services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code low_power_switch} 服务（services 通道，云→设备）的 data。
 * 用于开关 O4 地面站低电量模式，Job 类指令（进度经 events 通道同名 method 上报）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#LOW_POWER_SWITCH}
 *
 * <p>字段依据：DJI O4 地面站 cmd 文档 services 段 Data 表——
 * {@code {action}}（enum_int：0=关闭, 1=打开）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html">
 * DJI O4 地面站 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html")
@Verified(basis = "DJI O4 地面站 cmd.html low_power_switch services 请求 data = {action(enum_int 0关闭/1打开)}")
public record LowPowerSwitchRequest(
    /** 开关动作（enum_int：0=关闭, 1=打开） */
    Integer action
) {
    public LowPowerSwitchRequest {
        Objects.requireNonNull(action, "action 必填，DJI JSON 缺失 action 字段");
    }
}
