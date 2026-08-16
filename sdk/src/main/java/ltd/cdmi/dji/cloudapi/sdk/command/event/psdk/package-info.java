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
 * events 通道 PSDK（Payload SDK）类事件 POJO。
 *
 * <p>本包含 3 个 PSDK 相关事件的 data record（均 @Verified，simulator 已对接 hivemind 验证）：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.PsdkFloatingWindowTextData PsdkFloatingWindowTextData}
 *       — psdk_floating_window_text 浮窗文本推送（need_reply=0，data 直接平铺 psdk_index + value）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.PsdkUiResourceUploadResultData PsdkUiResourceUploadResultData}
 *       — psdk_ui_resource_upload_result UI 资源包上传结果（need_reply=0，data 直接平铺）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.psdk.CustomDataFromPsdkData CustomDataFromPsdkData}
 *       — custom_data_transmission_from_psdk PSDK 自定义消息接收（need_reply=0 @Inferred）</li>
 * </ul>
 *
 * <p><b>与 DRC uplink 区分</b>：DRC 通道的 {@code drc_psdk_floating_window_text} /
 * {@code drc_psdk_ui_resource} 推送 POJO 在 {@code command/drc/up/} 包，字段结构可能不同，
 * 本包 POJO 专属于 events 通道（method 名无 {@code drc_} 前缀）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html">
 * DJI Dock3 PSDK events</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.psdk;
