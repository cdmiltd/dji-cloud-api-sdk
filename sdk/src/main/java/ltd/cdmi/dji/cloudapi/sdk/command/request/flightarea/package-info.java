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

/**
 * requests 通道限飞区类指令 POJO。
 *
 * <p>本包含限飞区配置获取指令的 Request/Reply record：
 * <ul>
 *   <li>{@link FlightAreasGetRequest}/{@link FlightAreasGetReply} — flight_areas_get（@Verified）</li>
 * </ul>
 *
 * <p>{@link FlightAreasGetRequest} 为空 record（无请求参数）。
 *
 * <p>DJI 文档：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html
 */
package ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea;
