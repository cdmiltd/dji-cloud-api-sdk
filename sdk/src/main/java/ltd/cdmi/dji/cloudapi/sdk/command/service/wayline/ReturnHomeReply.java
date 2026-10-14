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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code return_home} 指令回复 data。
 *
 * <p>Dock2/Dock3 两页确认：{@code {result, output{status}}}（status 为 text）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 services</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#RETURN_HOME
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock2/Dock3 wayline.html return_home services_reply data = {result, output{status}}")
public record ReturnHomeReply(
        /** 返回码，0=成功 */
        Integer result,
        /** 输出 */
        Output output
) {
    public ReturnHomeReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * 输出结构。
     *
     * @param status 执行状态（text，如 ok）
     */
    public record Output(
            String status
    ) {}
}
