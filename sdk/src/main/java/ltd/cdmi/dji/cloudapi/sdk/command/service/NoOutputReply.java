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

package ltd.cdmi.dji.cloudapi.sdk.command.service;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * 无 output 指令通用 Reply。
 *
 * <p>DJI Cloud API services_reply 中，部分指令仅返回 {@code data.result=0}，
 * 不携带 output 字段。本 record 作为这些指令的通用 Reply。
 *
 * <p><b>output 差异说明</b>：
 * <ul>
 *   <li><b>Cmd 类指令</b>：services_reply 含 {@code output.status=ok}（如 debug_mode_open 等），
 *       本 record 不承载该字段，调用方应从 {@code ReplyEnvelope.data.result} 判断成功</li>
 *   <li><b>Job 类指令</b>：services_reply 含 {@code output.status=sent}（如 cover_open 等），
 *       本 record 不承载该字段，进度通过 events 通道上报</li>
 *   <li><b>特殊指令</b>：sdr_workmode_switch/sim_slot_switch/esim_operator_switch 的
 *       services_reply 仅有 result 无 output，本 record 适用</li>
 * </ul>
 *
 * <p><b>@Inferred 待验证</b>：部分指令（如 drone_open）的 output 结构在 simulator 中未明确，
 * 待真机验证后可能需要独立 Reply record。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 services 通道</a>
 */
@Inferred(
    reason = "27 个无参数指令中，部分指令的 services_reply output 结构（如 status=ok/sent）未在本 record 承载，调用方应从 ReplyEnvelope.data.result 判断成功",
    verifyPoint = "drone_open 等指令的 output 结构待真机验证，可能需要独立 Reply"
)
public record NoOutputReply() {}
