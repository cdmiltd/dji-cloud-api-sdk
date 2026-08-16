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
 * events 通道媒体管理类事件 POJO。
 *
 * <p>本包含 2 个媒体管理相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.media.HighestPriorityUploadFlighttaskMediaData HighestPriorityUploadFlighttaskMediaData}
 *       — highest_priority_upload_flighttask_media 媒体上传优先级上报（@Verified，
 *       data 仅含 flight_id）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.media.FileUploadCallbackData FileUploadCallbackData}
 *       — file_upload_callback 文件上传结果回调（@Verified，含嵌套 MediaFile/MediaFileExt/
 *       MediaFileMetadata/ShootPosition，4 层嵌套保留在类体内）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/media.html">
 * DJI Dock3 媒体管理 events</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.media;
