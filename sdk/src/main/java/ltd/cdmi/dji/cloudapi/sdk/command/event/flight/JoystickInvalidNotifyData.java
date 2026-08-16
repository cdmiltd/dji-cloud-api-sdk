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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * joystick_invalid_notify 事件 data。
 *
 * <p>对应 DJI Cloud API {@code joystick_invalid_notify} 事件（events 通道）的 data。
 * 用于飞行控制无效原因通知，{@code need_reply=1} 需平台回复，
 * 三 Dock（Dock1/Dock2/Dock3）共有。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#JOYSTICK_INVALID_NOTIFY}
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.triggerJoystickInvalidNotify}（L507-L519）
 * 已对接 hivemind 验证。
 *
 * <p>{@code reason} 枚举值（依据 simulator REST API 文档）：
 * <ul>
 *   <li>0 = 遥控器失联</li>
 *   <li>1 = 低电量返航</li>
 *   <li>2 = 低电量降落</li>
 *   <li>3 = 靠近限飞区</li>
 *   <li>4 = 遥控器夺权</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html">
 * DJI Dock3 DRC events</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.triggerJoystickInvalidNotify L507-L519 已对接 hivemind 验证")
public record JoystickInvalidNotifyData(
    /** 飞行控制无效原因（0=遥控器失联, 1=低电量返航, 2=低电量降落, 3=靠近限飞区, 4=遥控器夺权） */
    int reason
) {}
