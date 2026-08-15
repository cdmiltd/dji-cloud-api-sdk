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
 * events 通道系统类事件 POJO。
 *
 * <p>本包含 2 个系统相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.system.OtaProgressData OtaProgressData}
 *       — ota_progress 固件升级进度上报（@Verified，含嵌套 Output/Progress）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.system.FileuploadProgressData FileuploadProgressData}
 *       — fileupload_progress 日志文件上传进度（@Verified，含嵌套 Output/Ext/FileItem/FileProgress，
 *       4 层嵌套保留在类体内以保持高内聚）</li>
 * </ul>
 *
 * <p>参考：
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/config.html">
 * DJI Dock3 配置类 events（ota_progress）</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log-upload.html">
 * DJI Dock3 日志上传 events（fileupload_progress）</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.system;
