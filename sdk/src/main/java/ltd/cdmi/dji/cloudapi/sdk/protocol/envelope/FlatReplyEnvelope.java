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
 * DJI Cloud API services_reply 通道「扁平 data」回复信封结构。
 *
 * <p>用于 services_reply（设备→云）中 data 为<b>扁平字段</b>（自带 {@code result}、无 {@code output} 包裹）
 * 的回复消息。与 {@link ReplyEnvelope}（data 固定为 {@link ReplyEnvelope.ReplyData}）的关键差异：
 * {@code data} 为 {@code Object}，由各 Reply record 自带 {@code result}，序列化后与文档 JSON 逐字段一致。
 *
 * <p>典型用例（DJI Dock3 解禁证书 services 回复，A21）：
 * <ul>
 *   <li>{@code unlock_license_list} 回复
 *       {@code data = {result, device_model_domain, consistence, licenses[]}}</li>
 *   <li>{@code unlock_license_switch} 回复
 *       {@code data = {result, license_id}}</li>
 * </ul>
 *
 * <p>JSON 形态：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "timestamp": 1700000000000,
 *   "method": "unlock_license_list",
 *   "data": {
 *     "result": 0,
 *     "device_model_domain": 3,
 *     "consistence": true,
 *     "licenses": [ ... ]
 *   },
 *   "gateway": ""
 * }
 * }</pre>
 *
 * <p>决策依据（2026-10-14 方案 B）：否决 {@link ReplyEnvelope} 泛型化（会破坏已发布 API、使
 * {@code fromJson(.., ReplyEnvelope.class)} 退化为 raw 类型），改用专用信封，与既有
 * {@link RequestReplyEnvelope}（requests 通道同形态）保持一致，零破坏性。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 消息协议</a>
 *
 * @param tid       事务 ID，与请求一致
 * @param bid       批次 ID，与请求一致
 * @param timestamp 时间戳（毫秒）
 * @param method    方法名，与请求一致
 * @param data      回复数据，具体结构由 method 决定（各 Reply record 自带 result）
 * @param gateway   网关设备 SN（可空，DJI 文档 services_reply 示例含此字段，与 {@link ReplyEnvelope} 对齐）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Dock3 解禁证书 services 页 services_reply 原文：data 为扁平 {result, ...}（unlock_license_list 含 gateway），信封字段 tid/bid/timestamp/method/data 与 ReplyEnvelope/RequestReplyEnvelope 一致")
public record FlatReplyEnvelope(
        String tid,
        String bid,
        long timestamp,
        String method,
        Object data,
        String gateway
) {

    /**
     * 兼容构造：省略 {@code gateway}（置 null）。
     *
     * @param tid       事务 ID，与请求一致
     * @param bid       批次 ID，与请求一致
     * @param timestamp 时间戳（毫秒）
     * @param method    方法名，与请求一致
     * @param data      回复数据（自带 result 的 Reply record）
     */
    public FlatReplyEnvelope(String tid, String bid, long timestamp, String method, Object data) {
        this(tid, bid, timestamp, method, data, null);
    }

    /**
     * 静态工厂：构造扁平 services_reply 信封（data 直接持有自带 {@code result} 的 Reply record）。
     *
     * @param tid       事务 ID，与请求一致
     * @param bid       批次 ID，与请求一致
     * @param method    方法名，与请求一致
     * @param replyData Reply record 实例（自带 result，扁平结构）
     * @return 完整信封，timestamp 取当前时间，gateway=null，data=replyData
     */
    public static FlatReplyEnvelope of(String tid, String bid, String method, Object replyData) {
        return new FlatReplyEnvelope(tid, bid, System.currentTimeMillis(), method, replyData, null);
    }
}
