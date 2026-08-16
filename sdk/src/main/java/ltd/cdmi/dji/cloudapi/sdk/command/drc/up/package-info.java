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
 * DRC 上行（{@code drc/up}）推送数据 POJO。
 *
 * <p>承载 DJI Cloud API DRC 上行通道设备→云的推送数据，
 * 对应 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcUpMethod} 枚举的 11 个 method。
 * 数据来源依据为 simulator
 * {@code DeviceSimulator.publishDrcEvents/publishPsdkAndAiEvents} 已对接 hivemind 验证的实现。
 *
 * <p><b>信封差异</b>：DRC 上行信封为 {@code {method, data, seq}}（DRC 通道特有，无 tid/bid），
 * 调用方使用 {@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec#parse(String, Class)} 解析时
 * 需自行从 JSON 顶层取 {@code seq}（{@link ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage} 信封未含 seq 字段）。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.up;
