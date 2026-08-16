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
 * in_flight_wayline_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code in_flight_wayline_progress} 事件（events 通道）的 data。
 * 用于空中下发航线状态上报，{@code need_reply=1} 需平台回复。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#IN_FLIGHT_WAYLINE_PROGRESS}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.publishInFlightWaylineProgress}（L1126-L1136）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Progress} — 进度（percent）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator.publishInFlightWaylineProgress L1126-L1136 已对接 hivemind 验证")
public record InFlightWaylineProgressData(
    String inFlightWaylineId,
    Progress progress,
    Integer status,
    Integer result,
    Integer wayPointIndex
) {
    public InFlightWaylineProgressData {
        Objects.requireNonNull(inFlightWaylineId, "inFlightWaylineId 必填，DJI JSON 缺失 in_flight_wayline_id 字段");
    }

    /** 进度对象。 */
    public record Progress(
        Integer percent
    ) {}
}
