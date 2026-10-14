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

package ltd.cdmi.dji.cloudapi.sdk.command.request.wayline;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_progress_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_progress_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功；{@code output} 含 {@code flight_id} / {@code progress} / {@code status}。
 *
 * <p>字段依据：DJI Dock2/Dock3 wayline 页 {@code flighttask_progress_get} reply
 * {@code {result, output{flight_id, progress{current_step(int), percent(int)}, status(text)}}}。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline.html flighttask_progress_get reply：{result, output{flight_id, progress{current_step, percent}, status}}")
public record FlighttaskProgressGetReply(
    Integer result,
    Output output
) {
    public FlighttaskProgressGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * 输出结构。
     *
     * @param flightId 航线任务 ID
     * @param progress 执行进度
     * @param status   任务状态（text）
     */
    public record Output(
        String flightId,
        Progress progress,
        String status
    ) {}

    /**
     * 执行进度。
     *
     * @param currentStep 当前步骤（int）
     * @param percent     进度百分比（int，0-100）
     */
    public record Progress(
        Integer currentStep,
        Integer percent
    ) {}
}
