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
 * DRC 通道 PSDK 控制指令 POJO。
 *
 * <p>DJI 文档在 services 与 DRC 两通道都定义了 PSDK 指令，SDK 早期仅登记了 services 侧
 * （{@code command/service/psdk/}）。本包补齐 DRC 通道（{@code drc/down}）的请求 data。
 *
 * <p>回包统一复用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}（{@code {result}}）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.drc.psdk;
