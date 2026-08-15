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
 * DJI Cloud API 指令 POJO 定义层。
 *
 * <p>本包定义 DJI services/drc/events 通道指令的请求 data 字段与回复 output 字段的
 * 强类型 record。只定义「字段是什么」，不实现「如何处理」。
 *
 * <h3>子包结构</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service service/} — services 通道指令 POJO</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.drc drc/} — drc 通道指令 POJO（19 个 DRC 指令，含 safety/flight/camera/light/speaker 五个子包）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event event/} — events 通道事件 POJO（后续批次）</li>
 * </ul>
 *
 * <h3>与现有模块的关系</h3>
 * <ul>
 *   <li>与 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.envelope} 组合使用：
 *       {@code XxxRequest} 作为 {@code RequestEnvelope.data}，
 *       {@code XxxReply} 作为 {@code ReplyEnvelope.data.output}</li>
 *   <li>与 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method} 对齐：
 *       每个 POJO 对应一个 {@code ServiceMethod}/{@code DrcMethod}/{@code EventMethod} 枚举值</li>
 *   <li>不引入接口抽象（不定义 {@code ServiceRequest} marker interface），保持纯 record 风格</li>
 * </ul>
 *
 * <h3>错误处理风格：字段缺失返回 null</h3>
 * <p>所有 record 字段使用包装类型（{@code Integer}/{@code Double}/{@code Long}/{@code String}），
 * 允许 {@code null} 表示「字段缺失」。POJO 不做字段校验，校验是调用方职责。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.service
 */
package ltd.cdmi.dji.cloudapi.sdk.command;
