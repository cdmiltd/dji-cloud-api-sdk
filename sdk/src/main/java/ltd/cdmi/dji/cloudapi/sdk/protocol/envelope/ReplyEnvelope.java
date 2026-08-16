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

package ltd.cdmi.dji.cloudapi.sdk.protocol.envelope;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API 回复消息信封结构。
 *
 * <p>用于 services_reply（设备→云）、requests_reply（云→设备）、property/set_reply（设备→云）等
 * 通道的回复消息。{@code data} 固定为 {@link ReplyData}，包含 {@code result} 与 {@code output}。
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "flighttask_execute",
 *   "data": {
 *     "result": 0,
 *     "output": { ... }
 *   }
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 消息协议</a>
 *
 * @param tid       事务 ID
 * @param bid       批次 ID
 * @param timestamp 时间戳（毫秒）
 * @param method    方法名，与请求 method 一致
 * @param data      回复数据，含 result 与 output
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档回复消息信封字段定义：data.result 表示执行结果，data.output 为输出")
public record ReplyEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        ReplyData data
) {

    /**
     * 回复数据体。
     *
     * @param result 执行结果，{@code 0} 表示成功，非 0 见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.error.DjiErrorCode}
     * @param output 输出负载，具体结构由 method 决定
     */
    @Verified(basis = "DJI Cloud API 官方文档回复 data 结构：{result, output}")
    public record ReplyData(int result, Object output) {
    }
}
