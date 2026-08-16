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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * flighttask_progress_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code flighttask_progress_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功；{@code output} 结构待 DJI 文档确认，暂用 {@code Object} 类型承接。
 *
 * <p>字段依据：simulator 返回原始 JsonNode 未解析 output 结构，回复字段待真机验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Inferred(
    reason = "simulator 返回原始 JsonNode 未解析 output 结构，回复字段待真机验证",
    verifyPoint = "真机验证 flighttask_progress_get 回复 output 字段结构"
)
public record FlighttaskProgressGetReply(
    Integer result,
    Object output
) {
    public FlighttaskProgressGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
