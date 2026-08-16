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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission;

/**
 * flighttask_prepare 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} 指令（services 通道）的请求 data。
 * 用于航线任务准备，下发航线文件与执行参数。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#FLIGHTTASK_PREPARE}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.handlePrepare} + {@code logFlightTaskPrepareParams}
 * 已对接 hivemind 验证。
 *
 * <p>{@code rth_altitude} 单位为相对起飞点 ALT（{@code min=20, max=1500}）。
 *
 * <p>{@code wayline_precision_type} 字段含义 DJI 文档未明确，待真机/文档确认。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.handlePrepare 已对接 hivemind 验证")
@Inferred(
    reason = "wayline_precision_type 字段含义 DJI 文档未明确",
    verifyPoint = "DJI 文档确认 wayline_precision_type 取值范围与含义"
)
public record FlighttaskPrepareRequest(
    String flightId,
    Integer taskType,
    Long executeTime,
    FlighttaskFile file,
    Integer rthAltitude,
    Integer rthMode,
    Integer outOfControlAction,
    Integer exitWaylineWhenRcLost,
    @Inferred(reason = "字段含义 DJI 文档未明确", verifyPoint = "DJI 文档确认取值范围")
    Integer waylinePrecisionType,
    ReadyConditions readyConditions,
    ExecutableConditions executableConditions,
    BreakPoint breakPoint,
    SimulateMission simulateMission,
    Integer flightSafetyAdvanceCheck
) {
    public FlighttaskPrepareRequest {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }
}
