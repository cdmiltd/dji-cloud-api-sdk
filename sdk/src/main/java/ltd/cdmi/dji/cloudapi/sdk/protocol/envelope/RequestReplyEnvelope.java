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
 * DJI Cloud API requests_reply 通道回复信封结构。
 *
 * <p>用于 requests 通道（设备→云）的回复消息（云→设备）。与 {@link ReplyEnvelope}（services_reply）
 * 的关键差异：{@code data} 为 {@code Object} 而非 {@link ReplyEnvelope.ReplyData}——
 * requests_reply 的 data 结构因指令而异：
 * <ul>
 *   <li>扁平结构（无 output 包裹）：如 {@code config} 回复
 *       {@code data = {result, app_id, app_license, url, token}}（见
 *       {@code ConfigReply} javadoc："非 output 包裹"）</li>
 *   <li>{@code {result, output}} 结构：如 {@code storage_config_get} 回复
 *       {@code data = {result, output:{bucket, endpoint, ...}}}（见
 *       {@code StorageConfigGetReply}）</li>
 *   <li>仅 {@code result}：如 {@code airport_bind_status} 回复
 *       {@code data = {result}}（见 {@code AirportBindStatusReply}）</li>
 * </ul>
 *
 * <p>各 Reply record 自带 {@code result} 字段，序列化时直接作为 data 整体，
 * 不再像 {@link ReplyEnvelope} 那样拆分到 {@code ReplyData{result, output}}。
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "config",
 *   "data": {
 *     "result": 0,
 *     "app_id": "...",
 *     "app_license": "...",
 *     "url": "...",
 *     "token": "..."
 *   }
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 消息协议</a>
 *
 * @param tid       事务 ID，与请求一致
 * @param bid       批次 ID，与请求一致
 * @param timestamp 时间戳（毫秒）
 * @param method    方法名，与请求一致
 * @param data       回复数据，具体结构由 method 决定（各 Reply record 自带 result）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档 requests_reply 信封字段定义：tid/bid/timestamp/method/data，data 结构因指令而异（ConfigReply javadoc 确认 config 回复 data 扁平非 output 包裹）")
public record RequestReplyEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        Object data
) {

    /**
     * 静态工厂：构造 requests_reply 信封（data 直接持有 Reply record，非 ReplyData 包裹）。
     *
     * <p>用于 requests 通道回复（如 {@code config} / {@code airport_bind_status} /
     * {@code storage_config_get} / {@code flight_areas_get}），调用方传入自带 {@code result}
     * 的 Reply record，SDK 包装为完整信封。
     *
     * @param tid       事务 ID，与请求一致
     * @param bid       批次 ID，与请求一致
     * @param method    方法名，与请求一致
     * @param replyData Reply record 实例（自带 result，结构因指令而异）
     * @return 完整信封，timestamp 取当前时间，data=replyData
     */
    public static RequestReplyEnvelope of(String tid, String bid, String method, Object replyData) {
        return new RequestReplyEnvelope(tid, bid, System.currentTimeMillis(), method, replyData);
    }
}
