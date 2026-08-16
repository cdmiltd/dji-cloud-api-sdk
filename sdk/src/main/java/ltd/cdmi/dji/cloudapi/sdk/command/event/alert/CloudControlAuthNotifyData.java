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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * cloud_control_auth_notify 事件 data。
 *
 * <p>对应 DJI Cloud API {@code cloud_control_auth_notify} 事件（pilot-to-cloud，events 通道）的 data。
 * 用于请求授权结果通知，{@code need_reply=0} 单向通知，含执行结果与 output（status）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#CLOUD_CONTROL_AUTH_NOTIFY}
 *
 * <p>字段依据：simulator {@code AuthFlowHandler.publishAuthNotify}（L297-L316）
 * 已对接 hivemind 验证。
 *
 * <p>{@code output.status} 枚举值：{@code ok}/{@code failed}/{@code canceled}。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Output} — 输出对象（status）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html")
@Verified(basis = "simulator AuthFlowHandler.publishAuthNotify L297-L316 已对接 hivemind 验证")
public record CloudControlAuthNotifyData(
    Integer result,
    Output output
) {
    public CloudControlAuthNotifyData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** cloud_control_auth_notify 事件 output 字段。 */
    public record Output(
        String status
    ) {}
}
