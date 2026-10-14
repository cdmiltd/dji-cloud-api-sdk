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
 * low_power_switch 指令回复 data（services_reply）。
 *
 * <p>对应 DJI Cloud API {@code low_power_switch} 服务（services 通道）的回复 data。
 * 结构为 {@code {result, output{status}}}，其中 {@code status} 为文本（如 {@code "ok"}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html">
 * DJI O4 地面站 远程调试（cmd）</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#LOW_POWER_SWITCH
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html")
@Verified(basis = "DJI O4 地面站 cmd.html low_power_switch services_reply data = {result, output{status(text)} }")
public record LowPowerSwitchReply(
    /** 返回码，0=成功 */
    Integer result,
    /** 输出 */
    Output output
) {
    public LowPowerSwitchReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * 输出结构。
     *
     * @param status 执行状态（text，如 "ok"）
     */
    public record Output(
        String status
    ) {}
}
