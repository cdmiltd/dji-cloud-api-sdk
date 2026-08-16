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

package ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flight_areas_get 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code flight_areas_get} 指令（requests 通道）的请求 data。
 * 用于获取限飞区配置文件。本指令无请求参数（空 data）。
 *
 * <p>字段依据：simulator {@code FlightAreaSimulator.requestFlightAreas} 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator FlightAreaSimulator.requestFlightAreas 已对接 hivemind 验证")
public record FlightAreasGetRequest() {}
