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

package ltd.cdmi.dji.cloudapi.sdk.http;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API HTTP 响应信封结构（泛型化）。
 *
 * <p>DJI Pilot 上云 HTTP API 响应统一信封：
 * <pre>{@code
 * {
 *   "code": 0,
 *   "message": "success",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p>{@code code} 为 0 表示成功，非 0 代表错误（{@code message} 含错误描述）。
 * {@code data} 是业务数据，具体结构由接口决定。泛型参数 {@code <T>} 让调用方在解析时
 * 指定 {@code data} 的 POJO 类型，编译期类型安全——与 MQTT 通道的
 * {@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage} 和 WebSocket 通道的
 * {@link ltd.cdmi.dji.cloudapi.sdk.websocket.WsPushMessage} 对称。
 *
 * <p><b>使用方式</b>：用 {@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec#parseHttp}
 * 一步完成信封解析 + data 反序列化：
 * <pre>{@code
 * var resp = MessageCodec.parseHttp(jsonBody, StsCredentials.class);
 * if (resp.code() == 0) {
 *     resp.data().bucket();   // 类型安全，无 cast，无 instanceof
 * } else {
 *     log.error("HTTP 错误: code={}, message={}", resp.code(), resp.message());
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/basic-concept/https.html">
 * DJI HTTPS Response 规范</a>
 *
 * @param <T>     data 字段的 POJO 类型，由 parseHttp 调用方通过 Class&lt;T&gt; 指定
 * @param code    错误码，0 表示成功，非 0 代表错误
 * @param message 成功/错误描述
 * @param data    业务数据（反序列化后的 POJO，code 非 0 或 JSON 无 data 字段时为 null）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/basic-concept/https.html")
@Verified(basis = "DJI 官方文档 Response 规范明确 code/message/data 三字段信封")
public record HttpResponseEnvelope<T>(
        int code,
        String message,
        T data
) {
}
