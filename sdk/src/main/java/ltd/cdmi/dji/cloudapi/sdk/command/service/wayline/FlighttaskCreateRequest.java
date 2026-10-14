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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code flighttask_create} 指令请求 data。<b>已废弃</b>（DJI 文档标注），仅为协议完整性补入。
 *
 * <p>请求 data：{@code {flight_id, type, file{url, sign}}}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/wayline.html">
 * DJI Dock1 航线管理 services</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#FLIGHTTASK_CREATE
 */
@Deprecated
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/wayline.html")
@Verified(basis = "DJI Dock1 wayline.html flighttask_create（已废弃）请求 data = {flight_id, type, file{url, sign}}")
public record FlighttaskCreateRequest(
        /** 航线任务 ID */
        String flightId,
        /** 任务类型 */
        Integer type,
        /** 航线文件 */
        File file
) {

    /**
     * 航线文件。
     *
     * @param url  文件 URL
     * @param sign 文件签名
     */
    public record File(
            String url,
            String sign
    ) {}
}
