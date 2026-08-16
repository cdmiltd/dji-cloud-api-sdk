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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_execute 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_execute} 指令（services 通道）的请求 data。
 * 用于航线任务执行，启动已准备好的航线。异步指令，进度通过
 * {@code flight_task_progress} 事件上报。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#FLIGHTTASK_EXECUTE}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.handleExecute} +
 * {@code parseMultiDockTask} 已对接 hivemind 验证。
 *
 * <p>{@code multi_dock_task} 用于蛙跳任务（多机场协同航线执行），
 * 非蛙跳任务时为 {@code null}。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.handleExecute + parseMultiDockTask 已对接 hivemind 验证")
public record FlighttaskExecuteRequest(
    String flightId,
    MultiDockTask multiDockTask
) {
    public FlighttaskExecuteRequest {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }
}
