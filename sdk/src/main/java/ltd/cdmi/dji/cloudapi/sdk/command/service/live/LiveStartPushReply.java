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

package ltd.cdmi.dji.cloudapi.sdk.command.service.live;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * live_start_push 指令回复 output。
 *
 * <p><b>本指令无 output 字段</b>：services_reply 仅返回 {@code data.result=0} 表示成功，
 * 本 record 为占位以保证 Request/Reply 对称性，无业务字段。
 *
 * <p>若 DJI 协议后续为本指令补充 output 字段，将在此 record 扩展。
 *
 * @see LiveStartPushRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator LiveStreamSimulator.handleStartPush 已对接 hivemind 验证：返回 result=0 无 output")
public record LiveStartPushReply() {}
