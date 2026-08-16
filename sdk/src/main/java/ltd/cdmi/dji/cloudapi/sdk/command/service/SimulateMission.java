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

package ltd.cdmi.dji.cloudapi.sdk.command.service;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 模拟器任务参数（flighttask_prepare 与 takeoff_to_point 共用）。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} / {@code takeoff_to_point} 请求 data 中
 * {@code simulate_mission} 字段。
 *
 * <p><b>altitude 字段</b>：simulator {@code flighttask_prepare} 解析 4 字段（含 altitude），
 * {@code takeoff_to_point} 仅解析 3 字段（无 altitude）。本 record 统一含 4 字段，
 * altitude 缺失时为 {@code null}。
 *
 * @see FlighttaskPrepareRequest#simulateMission()
 * @see TakeoffToPointRequest#simulateMission()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator WaylineTaskSimulator.handlePrepare 已对接 hivemind 验证")
@Inferred(
    reason = "takeoff_to_point 在 simulator 未解析 altitude，统一 record 含 4 字段待 DJI 文档确认是否协议级一致",
    verifyPoint = "DJI 文档是否规定 simulate_mission 含 altitude 字段"
)
public record SimulateMission(
    Integer isEnable,
    Double latitude,
    Double longitude,
    Double altitude
) {}
