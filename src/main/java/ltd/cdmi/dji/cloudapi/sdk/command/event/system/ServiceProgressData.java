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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 设备管理类 service 进度上报通用 event data。
 *
 * <p>DJI Cloud API 中，设备管理类 service 指令（如 {@code device_reboot}、{@code cover_open}、
 * {@code cover_close}、{@code charge_open}、{@code charge_close}、{@code power_on}、
 * {@code power_off}、{@code format_storage}、{@code esim_activate}、{@code rtk_calibration}
 * 等）的异步进度上报使用与 service 同名 method 发送至 events 通道，data 结构统一为
 * {@code output{status, progress{percent, step_key?}}}。
 *
 * <p>本 POJO 覆盖上述 13 个方法的通用进度结构。部分方法可能有额外扩展字段（如
 * {@code esim_activate} 的 {@code output.ext}），以 {@link Object} 承接，按需解析。
 *
 * <p>字段依据：simulator {@code RemoteDebugSimulator} 第 267 行注明通用结构
 * {@code data.{result, output:{status, progress:{percent, step_key?}}}}，
 * 已对接 hivemind 验证。
 *
 * <p>{@code output.status} 枚举值：{@code in_progress}/{@code ok}/{@code failed}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 设备管理 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Verified(basis = "simulator RemoteDebugSimulator 通用进度结构已对接 hivemind 验证")
@Inferred(reason = "13 个方法共用通用结构，部分方法可能有额外 ext 字段，待真机验证")
public record ServiceProgressData(
        /** 返回码（0=成功，非 0=错误） */
        Integer result,
        /** 输出结构（含 status/progress） */
        Output output
) {
    /**
     * 输出结构。
     *
     * @param status   执行状态（in_progress=执行中, ok=成功, failed=失败）
     * @param progress 进度信息
     */
    public record Output(
            String status,
            Progress progress
    ) {}

    /**
     * 进度信息。
     *
     * @param percent  进度百分比（0-100）
     * @param stepKey  步骤标识（可选，如 download_firmware/upgrade_firmware）
     */
    public record Progress(
            Integer percent,
            String stepKey
    ) {}
}
