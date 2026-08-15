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
 * 远程日志上传 services 指令 POJO。
 *
 * <p>承载 DJI Cloud API services 通道对远程日志上传（fileupload_*）的控制指令与回复。
 * 数据来源依据为 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/RemoteLogSimulator.java">
 * RemoteLogSimulator</a> 已对接 hivemind 验证的字段集。
 *
 * <p><b>M-2 诊断日志</b>：simulator 在 {@code fileupload_list} 中记录两条推断：
 * <ul>
 *   <li>{@code end_time} 字段拼写差异（DJI Example 误写为 {@code end_ime}）— SDK 按 Column 表正确拼写 {@code end_time}</li>
 *   <li>{@code start_time}/{@code end_time} 单位差异（DJI Column 表标注秒/s，simulator 推断为毫秒）— 待真机验证</li>
 * </ul>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.log;
