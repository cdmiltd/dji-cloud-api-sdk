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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code flight_setup_exception_notify} 事件 data（need_reply=1）：飞行准备阶段异常通知。
 *
 * <p>结构：{@code {sn, timeout_time, timestamp, flight_type}}；
 * {@code flight_type} 为 enum_int（1=航线任务, 2=指令飞行）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 events</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FLIGHT_SETUP_EXCEPTION_NOTIFY
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock 航线管理 events flight_setup_exception_notify data = {sn, timeout_time, timestamp, flight_type(enum_int 1/2)}")
public record FlightSetupExceptionNotifyData(
        /** 设备 SN */
        String sn,
        /** 超时时间 */
        Integer timeoutTime,
        /** 时间戳（毫秒） */
        Long timestamp,
        /** 飞行类型：1=航线任务, 2=指令飞行 */
        Integer flightType
) {}
