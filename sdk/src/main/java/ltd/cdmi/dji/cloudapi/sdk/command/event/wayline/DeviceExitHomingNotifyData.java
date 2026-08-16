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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * device_exit_homing_notify 事件 data。
 *
 * <p>对应 DJI Cloud API {@code device_exit_homing_notify} 事件（events 通道）的 data。
 * 用于设备返航退出状态通知，{@code need_reply=1} 需平台回复，
 * 三 Dock（Dock1/Dock2/Dock3）通用。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#DEVICE_EXIT_HOMING_NOTIFY}
 *
 * <p>字段依据：simulator {@code WaylineTaskSimulator.publishDeviceExitHomingNotify}（L1052-L1071）
 * 已对接 hivemind 验证。
 *
 * <p>{@code reason} 字段类型：DJI 文档字段定义为 {@code enum_int}，但 DJI 示例中为字符串 {@code "0"}。
 * 本 POJO 按 {@code enum_int} 定义使用 {@code int}，标 @Inferred 待真机验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 wayline events</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator.publishDeviceExitHomingNotify L1052-L1071 已对接 hivemind 验证")
@Inferred(
    reason = "reason 字段类型 DJI 文档定义为 enum_int，但示例中为字符串 \"0\"，按 enum_int 使用 int 待真机验证",
    verifyPoint = "真机验证 reason 字段是否接受 int 类型（vs 字符串）"
)
public record DeviceExitHomingNotifyData(
    /** 设备序列号（simulator 自动填充 dockSn） */
    String sn,
    /** 动作类型：0=退出返航退出状态, 1=进入返航退出状态 */
    int action,
    /** 退出返航原因（0-10 枚举，按 enum_int 定义，待真机验证类型） */
    int reason
) {}
