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
 * flighttask_progress_get 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_progress_get} 指令（requests 通道）的请求 data。
 * 用于查询航线任务执行进度。
 *
 * <p>{@code sn} 可选，目标机场 SN；蛙跳场景查询另一机场的任务进度时填写。
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator} L1186-L1193 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator L1186-L1193 已对接 hivemind 验证")
public record FlighttaskProgressGetRequest(
    String flightId,
    String sn
) {
    public FlighttaskProgressGetRequest {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }
}
