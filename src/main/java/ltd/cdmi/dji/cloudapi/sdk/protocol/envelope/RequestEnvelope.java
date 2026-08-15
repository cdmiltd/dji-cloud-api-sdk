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
 * DJI Cloud API 请求消息信封结构（设备→云 或 云→设备）。
 *
 * <p>用于 services（云→设备）、requests（设备→云）、property/set（云→设备）等通道的请求消息。
 * 通用字段为 {@code tid}、{@code bid}、{@code timestamp}、{@code method}、{@code data}。
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "flighttask_execute",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 消息协议</a>
 *
 * @param tid       事务 ID（transaction id），标识一次完整交互
 * @param bid       批次 ID（batch id），标识一次业务批次
 * @param timestamp 时间戳（毫秒）
 * @param method    方法名，见各 method 枚举
 * @param data      数据负载，具体结构由 method 决定
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档请求消息信封字段定义")
public record RequestEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        Object data
) {
}
