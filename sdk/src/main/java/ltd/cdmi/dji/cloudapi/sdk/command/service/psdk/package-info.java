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
 * PSDK（Payload SDK）外设控制 services 指令 POJO。
 *
 * <p>承载 DJI Cloud API services 通道对 PSDK 外设（喊话器/输入框/控件等）的控制指令。
 * 与 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply DRC 通道} 的 drc_speaker_* 不同：
 * services 通道的 speaker_* 方法经 thing/product/{sn}/services 主题下发，回复含 output 字段；
 * DRC 通道的 drc_speaker_* 方法经 thing/product/{sn}/drc/down 主题下发，回复仅含 result 字段。
 *
 * <p>数据来源依据为 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/PsdkSimulator.java">
 * PsdkSimulator</a> 已对接 hivemind 验证的字段集。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.psdk;
