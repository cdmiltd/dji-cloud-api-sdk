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
 * FTS（Flight Termination System，飞行终止系统）指令 POJO。
 *
 * <p>覆盖 {@code fts_config}（配置 FTS 服务器）与 {@code drone_propeller_stop_v1}（桨叶停止）两条
 * services 指令。两者回复均仅 {@code {result}}，复用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/fts.html">
 * DJI Dock3 FTS services</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.fts;
