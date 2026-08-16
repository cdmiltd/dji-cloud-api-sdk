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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * heart_beat 指令回复 data。
 *
 * <p>回复回显请求中的 {@code timestamp}（不含 {@code result} 字段）。
 *
 * <p>回复格式：
 * <pre>{@code
 * {
 *   "method": "heart_beat",
 *   "data": { "timestamp": 1700000000000 },
 *   "seq": 1
 * }
 * }</pre>
 *
 * @see HeartBeatRequest
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#HEART_BEAT
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerFlightControlHandlers 已对接 hivemind 验证，回复回显 timestamp")
@Inferred(
    reason = "heart_beat 协议格式与发起方未在 DJI 文档明确，回复结构基于 simulator 实现",
    verifyPoint = "待真机/文档确认 heart_beat 回复是否含 result 字段"
)
public record HeartBeatReply(
    Long timestamp
) {
    public HeartBeatReply {
        Objects.requireNonNull(timestamp, "timestamp 必填，DJI DRC heart_beat 回复缺失 timestamp 字段");
    }
}
