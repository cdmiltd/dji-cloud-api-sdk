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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_progress} 事件（events 通道）的 data。
 * 用于航线任务执行进度上报，含执行结果、扩展信息、断点信息与步进进度。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FLIGHTTASK_PROGRESS}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.publishProgress}（L927-L940）
 * 已对接 hivemind 验证。
 *
 * <p>{@code output.status} 枚举值：{@code in_progress}/{@code sent}/{@code ok}/
 * {@code failed}/{@code paused}/{@code canceled} 等。
 *
 * <p>嵌套 record 结构（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Output} — 输出对象，含 ext/progress/status</li>
 *   <li>{@link Output.Ext} — 扩展信息（current_waypoint_index/flight_id/media_count/track_id/
 *       wayline_id/wayline_mission_state/break_point）</li>
 *   <li>{@link Output.Ext.BreakPoint} — 断点信息（index/state/progress/wayline_id/break_reason/
 *       latitude/longitude/height/attitude_head）</li>
 *   <li>{@link Output.Progress} — 步进进度（current_step/percent）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator.publishProgress L927-L940 已对接 hivemind 验证")
public record FlighttaskProgressData(
    Integer result,
    Output output
) {
    public FlighttaskProgressData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** flighttask_progress 事件 output 字段。 */
    public record Output(
        Ext ext,
        Progress progress,
        String status
    ) {}

    /** output.ext 字段，航线任务扩展信息。 */
    public record Ext(
        Integer currentWaypointIndex,
        String flightId,
        Integer mediaCount,
        String trackId,
        Integer waylineId,
        Integer waylineMissionState,
        BreakPoint breakPoint
    ) {}

    /** output.ext.break_point 字段，航线任务断点信息。 */
    public record BreakPoint(
        Integer index,
        Integer state,
        Double progress,
        Integer waylineId,
        Integer breakReason,
        Double latitude,
        Double longitude,
        Double height,
        Double attitudeHead
    ) {}

    /** output.progress 字段，步进进度。 */
    public record Progress(
        Integer currentStep,
        Integer percent
    ) {}
}
