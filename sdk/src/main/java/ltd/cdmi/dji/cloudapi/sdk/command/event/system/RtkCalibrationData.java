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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * rtk_calibration 事件 data（events 通道，设备→云）。
 *
 * <p>对应 DJI Cloud API {@code rtk_calibration} 事件：services 指令 {@code rtk_calibration}
 * （见 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.RtkCalibrationRequest}）发起后，
 * 设备经 events 通道用同名 method 异步上报标定进度与结果。
 *
 * <p><b>为何独立 Data</b>：其结构（{@code output.ext.devices[]} + {@code output.ext.version} +
 * {@code output.progress.current_step}）比通用进度结构 {@link ServiceProgressData} 更丰富，
 * 通用 POJO 不足以覆盖，故独立建模。
 *
 * <p>结构依据：DJI O4 地面站 cmd 文档 events 段——
 * {@code result(int 固定 0)；output{ status(enum_string failed/ok),
 * progress{ percent(int 0-100), current_step(int 固定 1) },
 * ext{ devices[]{ sn, type(enum_int 1=手动标定), module(enum_string 3=机场,6=中继),
 * result(int), status(enum_string 8 值) }, version(int) } }}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html">
 * DJI O4 地面站 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html")
@Verified(basis = "DJI O4 地面站 cmd.html rtk_calibration 事件 Data 表（output{status,progress{percent,current_step},ext{devices[],version}}）")
public record RtkCalibrationData(
    /** 返回码（0=成功，非 0=错误） */
    Integer result,
    /** 输出结构（status/progress/ext） */
    Output output
) {
    /**
     * rtk_calibration 事件 output。
     *
     * @param status   执行状态（enum_string：failed=失败, ok=成功）
     * @param progress 标定进度
     * @param ext      标定结果扩展信息
     */
    public record Output(
        String status,
        Progress progress,
        Ext ext
    ) {}

    /**
     * output.progress 标定进度。
     *
     * @param percent     进度百分比（0-100）
     * @param currentStep 当前步骤（int，固定 1）
     */
    public record Progress(
        Integer percent,
        Integer currentStep
    ) {}

    /**
     * output.ext 标定结果。
     *
     * @param devices 各标定设备结果
     * @param version 版本号
     */
    public record Ext(
        List<Device> devices,
        Integer version
    ) {}

    /**
     * output.ext.devices[] 单个标定设备结果。
     *
     * @param sn     设备序列号
     * @param type   标定类型（enum_int：1=手动标定）
     * @param module 设备模块（enum_string：3=机场, 6=中继）
     * @param result 设备标定返回码
     * @param status 设备标定状态（enum_string 8 值：canceled/failed/in_progress/ok/paused/rejected/sent/timeout）
     */
    public record Device(
        String sn,
        Integer type,
        String module,
        Integer result,
        String status
    ) {}
}
