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

package ltd.cdmi.dji.cloudapi.sdk.command.event.system;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * ota_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code ota_progress} 事件（events 通道）的 data。
 * 用于固件升级进度上报，{@code need_reply=1} 需平台回复，含执行结果与 output（status/progress）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#OTA_PROGRESS}
 *
 * <p>字段依据：simulator {@code OtaSimulator.publishOtaProgress}（L203-L217）
 * 已对接 hivemind 验证。
 *
 * <p>{@code output.status} 枚举值：{@code in_progress}/{@code ok}/{@code failed}/
 * {@code canceled}/{@code paused}/{@code rejected}/{@code sent}/{@code timeout}。
 * {@code output.progress.current_step} 枚举值：{@code download_firmware}/{@code upgrade_firmware}。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Output} — 输出对象（status/progress）</li>
 *   <li>{@link Output.Progress} — 步进进度（percent/current_step）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/config.html")
@Verified(basis = "simulator OtaSimulator.publishOtaProgress L203-L217 已对接 hivemind 验证")
public record OtaProgressData(
    Integer result,
    Output output
) {
    public OtaProgressData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** ota_progress 事件 output 字段。 */
    public record Output(
        String status,
        Progress progress
    ) {}

    /** output.progress 字段，步进进度。 */
    public record Progress(
        Integer percent,
        String currentStep
    ) {}
}
