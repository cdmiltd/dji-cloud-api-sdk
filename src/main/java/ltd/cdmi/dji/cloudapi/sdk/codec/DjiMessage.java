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

package ltd.cdmi.dji.cloudapi.sdk.codec;

/**
 * DJI Cloud API 消息的解析结果（类型安全信封）。
 *
 * <p>由 {@link MessageCodec#parse(String, Class)} 生成，将原始 JSON 的
 * {@code method}/{@code tid}/{@code bid}/{@code data} 封装为类型安全的记录。
 * 调用方通过 {@code msg.data()} 直接获取反序列化后的 POJO，无需手动提取信封
 * 字段再调用 {@code treeToValue}。
 *
 * <p><b>设计理念</b>：SDK 提供便捷方法减少样板代码（信封解析 + 反序列化合并为
 * 一次调用），但不接管路由——调用方仍用 switch 按方法分发业务逻辑。
 * 每个 switch case 只需 1 行 {@code parse} 调用，其余是类型安全的业务代码。
 *
 * <p><b>使用示例</b>：
 * <pre>{@code
 * String method = MessageCodec.extractMethod(payload);
 * switch (ServiceMethod.fromMethodName(method).orElseThrow()) {
 *     case FLY_TO_POINT -> {
 *         var msg = MessageCodec.parse(payload, FlyToPointRequest.class);
 *         msg.data().flyToId();               // 类型安全，无需 cast
 *         sendReply(msg.tid(), new NoOutputReply());
 *     }
 *     case COVER_OPEN -> {
 *         var msg = MessageCodec.parse(payload, NoParameterRequest.class);
 *         sendReply(msg.tid(), new NoOutputReply());
 *     }
 *     default -> log.warn("未处理: {}", method);
 * }
 * }</pre>
 *
 * <p>适用于全部 5 个 MQTT 通道（services / drc / events / requests / status），
 * 信封结构一致：{@code {method, tid, bid?, data}}。{@code bid} 仅 service/event
 * 通道携带，其余通道为 null。
 *
 * @param method DJI 方法名（如 {@code "fly_to_point"}），JSON 中无此字段时为 null
 * @param tid    事务 ID，JSON 中无此字段时为 null
 * @param bid    业务 ID，仅 service/event 通道携带，其余为 null
 * @param data   反序列化后的 POJO，JSON 中无 data 字段时为 null
 * @param <T>    POJO 类型，由 {@code parse} 调用方通过 {@code Class<T>} 指定
 */
public record DjiMessage<T>(String method, String tid, String bid, T data) {
}
