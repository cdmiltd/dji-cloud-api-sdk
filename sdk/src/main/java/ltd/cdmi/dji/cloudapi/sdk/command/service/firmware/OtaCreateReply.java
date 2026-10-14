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

package ltd.cdmi.dji.cloudapi.sdk.command.service.firmware;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code ota_create} 指令回复 data（services_reply）。
 *
 * <p>DJI O4 地面站 firmware.html 明确 {@code ota_create} 回复为
 * {@code {result, output{status}}}，其中 {@code status} 为 8 值 {@code enum_string}
 * （{@code canceled/failed/in_progress/ok/paused/rejected/sent/timeout}）。
 *
 * <p>因 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply} 不承载
 * {@code output.status}，参照 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.ReturnHomeReply}
 * 的一致性惯例，新建本专属 Reply。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/firmware.html">
 * DJI O4 地面站 固件升级 services</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#OTA_CREATE
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/firmware.html")
@Verified(basis = "DJI O4 地面站 firmware.html ota_create services_reply data = {result, output{status}}（status 8 值 enum_string）")
public record OtaCreateReply(
        /** 返回码，0=成功 */
        Integer result,
        /** 输出 */
        Output output
) {
    public OtaCreateReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * 输出结构。
     *
     * @param status 任务状态（enum_string：canceled/failed/in_progress/ok/paused/rejected/sent/timeout）
     */
    public record Output(
            String status
    ) {}
}
