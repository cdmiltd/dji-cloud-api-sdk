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
 * {@code flighttask_resource_get} 指令请求 data：获取任务航线文件资源。
 *
 * <p>对应 DJI Cloud API {@code flighttask_resource_get} 指令（requests 通道）的请求 data。
 * 设备主动向云请求获取指定 {@code flight_id} 对应的航线文件资源，
 * 云通过 {@code requests_reply} 回复航线文件 URL 和指纹。
 *
 * <p>字段依据：simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/WaylineTaskSimulator.java#L1203-L1207">
 * WaylineTaskSimulator.publishFlighttaskResourceGet</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 Requests flighttask_resource_get</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator.publishFlighttaskResourceGet 已对接 hivemind 验证 + Dock3 wayline.html Requests flighttask_resource_get")
public record FlighttaskResourceGetRequest(
    /** 计划 ID（航线任务唯一标识） */
    String flightId
) {
    public FlighttaskResourceGetRequest {
        Objects.requireNonNull(flightId, "flightId 必填，DJI JSON 缺失 flight_id 字段");
    }
}
