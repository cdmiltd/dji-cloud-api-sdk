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
 * DJI Cloud API 各通道的 method 名称枚举。
 *
 * <p>本包包含 5 个 method 枚举，对应 DJI Cloud API 的 5 类 MQTT 通道：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.StatusMethod} — status 通道（设备拓扑上下线通知）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.RequestsMethod} — requests 通道（设备主动向云请求）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod} — events 通道（设备事件上行）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod} — drc 通道（DRC 实时控制与状态推送）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod} — services 通道（云对设备的服务调用）</li>
 * </ul>
 *
 * <h3>错误处理风格：fromMethodName 返回 Optional</h3>
 * <p>本包所有枚举的 {@code fromMethodName(String)} 方法在遇到未知 method 名时返回
 * {@link java.util.Optional#empty()}，而非抛异常或返回 null。
 *
 * <p><b>理由</b>：method 名随固件升级可能扩展，未知 method 是正常情况（新固件新增 method，
 * 旧 SDK 未识别），调用者需灵活处理——记录日志后跳过，或上报"未知 method"诊断码。
 * 与 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype} 的 {@code fromCode(int)}
 * 抛异常风格形成对比，体现"场景驱动设计"原则（见 SDK design doc §3.5）。
 *
 * <p><b>调用者注意</b>：判断 method 是否已知后，应通过 {@code Optional.isPresent()}
 * 或 {@code Optional.orElseThrow()} 处理，避免在未知 method 时崩溃。
 *
 * <h3>例外：PropertySetMethod</h3>
 * <p>本包还包含 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.PropertySetMethod}，
 * 用于 property/set 通道的可设置属性名。其 {@code fromPropertyName(String)} 方法
 * 在未知属性名时抛出 {@link IllegalArgumentException}（property 名集合封闭稳定，
 * 非开放协议集），与上述 5 个 method 枚举的 Optional 风格形成对比。
 */
package ltd.cdmi.dji.cloudapi.sdk.protocol.method;
