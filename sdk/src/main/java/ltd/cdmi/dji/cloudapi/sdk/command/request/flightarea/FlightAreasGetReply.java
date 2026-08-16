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

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flight_areas_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code flight_areas_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功，{@code output.file} 含限飞区配置文件信息。
 *
 * <p>字段依据：simulator {@code FlightAreaSimulator.requestFlightAreas} 解析 result + output.file
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator FlightAreaSimulator.requestFlightAreas 解析 result + output.file 已对接 hivemind 验证")
public record FlightAreasGetReply(
    Integer result,
    Output output
) {
    public FlightAreasGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** output 字段，含限飞区配置文件。 */
    public record Output(
        SyncFile file
    ) {}

    /** output.file 字段，限飞区配置文件。 */
    public record SyncFile(
        String name,
        String checksum
    ) {}
}
