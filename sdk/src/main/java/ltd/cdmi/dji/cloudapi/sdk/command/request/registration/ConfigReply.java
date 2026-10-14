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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * config 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code config} 指令（requests 通道）的回复 data。
 * 文档回复 data 字段集：{@code ntp_server_host}/{@code app_id}/{@code app_key}/{@code app_license}/{@code ntp_server_port}，
 * <b>无 {@code result}</b>。
 *
 * <p>注意：config 回复的 data 直接含扁平字段（非 output 包裹），
 * 与 services_reply 的 {@code {result, output}} 结构不同。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/config.html">
 * DJI Dock1 配置更新（config）</a>
 *
 * @param appId          应用 ID
 * @param appKey         应用 Key
 * @param appLicense     应用 License
 * @param ntpServerHost  NTP 服务器地址
 * @param ntpServerPort  NTP 服务器端口（缺省 123）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/config.html")
@Verified(basis = "DJI Dock1 配置更新（config）页 & O4 config 页 requests_reply data：{ntp_server_host,app_id,app_key,app_license,ntp_server_port}，无 result")
public record ConfigReply(
    String appId,
    String appKey,
    String appLicense,
    String ntpServerHost,
    Integer ntpServerPort
) {}
