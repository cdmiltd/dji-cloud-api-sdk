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

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flighttask_ready 事件 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_ready} 事件（events 通道）的 data。
 * 用于任务就绪通知，{@code need_reply=0} 单向通知，data 含就绪的 flight_id 数组。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FLIGHTTASK_READY}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.publishFlighttaskReady}（L1044-L1048）
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator.publishFlighttaskReady L1044-L1048 已对接 hivemind 验证")
public record FlighttaskReadyData(
    List<String> flightIds
) {
    public FlighttaskReadyData {
        Objects.requireNonNull(flightIds, "flightIds 必填，DJI JSON 缺失 flight_ids 字段");
    }
}
