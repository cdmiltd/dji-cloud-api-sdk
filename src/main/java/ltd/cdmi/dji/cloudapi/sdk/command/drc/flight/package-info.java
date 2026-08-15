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
 * DRC 飞行控制指令 POJO。
 *
 * <p>包含摇杆控制、飞行器控制（已废弃）、心跳三个指令的 Request/Reply record。
 *
 * <h3>指令清单（3 个，三 Dock 共有）</h3>
 * <ul>
 *   <li><b>stick_control</b> — 摇杆控制（杆量控制）。
 *       无回包机制，发送频率需保持 5-10Hz。</li>
 *   <li><b>drone_control</b> — 飞行器控制（综合控制）。
 *       已废弃（Dock1 有效，Dock2/Dock3 已废弃），成功不回包。
 *       建议使用 {@code stick_control} 替代。</li>
 *   <li><b>heart_beat</b> — DRC 心跳。回复回显 {@code timestamp}。</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#STICK_CONTROL
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRONE_CONTROL
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#HEART_BEAT
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;
