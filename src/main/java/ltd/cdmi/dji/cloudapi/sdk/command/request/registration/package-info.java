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
 * requests 通道机场注册流程指令 POJO。
 *
 * <p>本包含机场上云注册四步流程的 Request/Reply record：
 * <ul>
 *   <li>{@link ConfigRequest}/{@link ConfigReply} — config（@Verified；Reply data 结构 @Inferred 待真机验证）</li>
 *   <li>{@link AirportBindStatusRequest}/{@link AirportBindStatusReply} — airport_bind_status（@Verified）</li>
 *   <li>{@link AirportOrganizationGetRequest}/{@link AirportOrganizationGetReply} — airport_organization_get（@Verified；Reply output 字段 @Inferred 待真机验证）</li>
 *   <li>{@link AirportOrganizationBindRequest}/{@link AirportOrganizationBindReply} — airport_organization_bind（@Verified）</li>
 * </ul>
 *
 * <p>注意：{@link ConfigReply} 的 data 直接含 {@code app_id}/{@code app_license}
 * （非 output 包裹），与 services_reply 的 {@code {result, output}} 结构不同。
 *
 * <p>DJI 文档：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html
 */
package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;
