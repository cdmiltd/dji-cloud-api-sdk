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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * stick_control 指令请求 data。
 *
 * <p>摇杆控制（杆量控制），实时推送四通道控制量。发送频率需保持 5-10Hz。
 *
 * <p><b>无回包机制</b>：成功不回复，仅异常时可能回复。本指令无 Reply record。
 *
 * <p>字段说明（四个通道值）：
 * <ul>
 *   <li>{@code roll} — 横滚通道值</li>
 *   <li>{@code pitch} — 俯仰通道值</li>
 *   <li>{@code throttle} — 油门通道值</li>
 *   <li>{@code yaw} — 偏航通道值</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#STICK_CONTROL
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerFlightControlHandlers 已对接 hivemind 验证")
public record StickControlRequest(
    Integer roll,
    Integer pitch,
    Integer throttle,
    Integer yaw
) {
    public StickControlRequest {
        Objects.requireNonNull(roll, "roll 必填，DJI JSON 缺失 roll 字段");
        Objects.requireNonNull(pitch, "pitch 必填，DJI JSON 缺失 pitch 字段");
        Objects.requireNonNull(throttle, "throttle 必填，DJI JSON 缺失 throttle 字段");
        Objects.requireNonNull(yaw, "yaw 必填，DJI JSON 缺失 yaw 字段");
    }
}
