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
 * requests 通道航线任务类指令 POJO。
 *
 * <p>本包含航线任务进度查询指令的 Request/Reply record：
 * <ul>
 *   <li>{@link FlighttaskProgressGetRequest}/{@link FlighttaskProgressGetReply} — flighttask_progress_get
 *       （Request @Verified；Reply output 结构 @Inferred 待真机验证，暂用 Object 类型承接）</li>
 * </ul>
 *
 * <p>DJI 文档：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html
 */
package ltd.cdmi.dji.cloudapi.sdk.command.request.wayline;
