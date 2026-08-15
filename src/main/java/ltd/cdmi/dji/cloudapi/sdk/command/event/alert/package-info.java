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
 * events 通道授权/AirSense/HMS 告警类事件 POJO。
 *
 * <p>本包含 3 个告警相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.alert.CloudControlAuthNotifyData CloudControlAuthNotifyData}
 *       — cloud_control_auth_notify 请求授权结果通知（@Verified，pilot-to-cloud，
 *       含嵌套 Output，status 枚举 ok/failed/canceled）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.alert.AirSenseWarningData AirSenseWarningData}
 *       — airsense_warning AirSense 告警（@Verified，特殊：data 直接是 List，
 *       含嵌套 Alert）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.alert.HmsData HmsData}
 *       — hms HMS 告警（@Verified，含嵌套 Item/Args）</li>
 * </ul>
 *
 * <p>参考：
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI RC Plus 2 DRC events（cloud_control_auth_notify）</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/wayline.html">
 * DJI Dock1 航线 events（airsense_warning）</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/hms.html">
 * DJI Dock3 HMS events</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;
