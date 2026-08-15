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
 * DJI Cloud API 遥测字段枚举类型。
 *
 * <p>本包包含 OSD/State 字段中使用的枚举类型（如 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode}、
 * {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.Gear}、
 * {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DockModeCode} 等）。
 *
 * <h3>错误处理风格：fromCode 抛异常</h3>
 * <p>本包所有枚举的 {@code fromCode(int)} 方法在遇到未知 code 时抛出
 * {@link IllegalArgumentException}，而非返回 null 或 Optional。
 *
 * <p><b>理由</b>：这些枚举的值范围由 DJI 官方文档明确定义，未知 code 通常表示
 * 协议异常或数据损坏，应快速失败以便调用者及时发现问题。
 *
 * <p><b>调用者注意</b>：如果输入来源不可信（如用户输入、未验证的外部数据），
 * 调用者应自行捕获 {@code IllegalArgumentException} 或预先校验 code 范围。
 */
package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;
