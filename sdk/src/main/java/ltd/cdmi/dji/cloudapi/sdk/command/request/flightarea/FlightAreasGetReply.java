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

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flight_areas_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code flight_areas_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功，{@code output.files} 为限飞区配置文件<b>数组</b>（空数组=无自定义飞行区）。
 *
 * <p>字段依据：DJI 自定义飞行区页官方文档 flight_areas_get reply：
 * {@code output.files[]} 元素含 {@code name}/{@code url}/{@code checksum(SHA256)}/{@code size}。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/custom-flight-area.html")
@Verified(basis = "DJI 自定义飞行区（custom-flight-area）页官方文档 flight_areas_get reply（requests 通道）：output.files 为数组，含 url/size")
public record FlightAreasGetReply(
    Integer result,
    Output output
) {
    public FlightAreasGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** output 字段，含限飞区配置文件数组（空数组=无自定义飞行区）。 */
    public record Output(
        List<File> files
    ) {}

    /** output.files 数组元素，单个限飞区配置文件。 */
    public record File(
        String name,
        String url,
        String checksum,
        Integer size
    ) {}
}
