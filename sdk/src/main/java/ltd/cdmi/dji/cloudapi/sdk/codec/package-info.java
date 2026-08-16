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

/**
 * DJI Cloud API 消息编解码工具。
 *
 * <p>本包包含两类工具：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec} — JSON 编解码 + 信封字段提取 +
 *       {@code parse(payload, Class<T>) } 便捷方法</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage} — {@code parse} 返回的类型安全信封，
 *       泛型 {@code <T>} 由调用方通过 {@code Class<T>} 指定</li>
 * </ul>
 *
 * <p><b>topic 解析</b>已移至 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.topic.TopicResolver}，
 * 与 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.topic.TopicBuilder} 同包。
 *
 * <h3>错误处理风格：extractResult 返回 -1</h3>
 * <p>{@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec#extractResult(String)}
 * 在 JSON 解析失败或字段不存在时返回 {@code -1}，而非抛异常或返回 Optional。
 *
 * <p><b>理由</b>：{@code result} 是 DJI 协议中表示操作结果的整数字段，
 * {@code 0} 表示成功，非零表示错误码。{@code -1} 不是 DJI 定义的合法 result 值，
 * 因此作为"解析失败"的哨兵值不会与合法值冲突。
 *
 * <p><b>调用者注意</b>：调用者应检查返回值是否为 {@code -1} 以区分
 * "解析失败"和"合法的失败错误码"。
 */
package ltd.cdmi.dji.cloudapi.sdk.codec;
