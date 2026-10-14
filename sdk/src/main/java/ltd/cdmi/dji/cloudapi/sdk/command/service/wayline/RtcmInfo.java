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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * RTK 基站信息（{@code flighttask_execute.multi_dock_task.dock_infos[].rtcm_info}）。
 *
 * <p>用于多机场蛙跳任务的 RTCM 差分数据源描述。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline.html rtcm_info = {mount_point(text), port(text), host(text), rtcm_device_type(int), source_type(int)}")
public record RtcmInfo(
        /** 挂载点 */
        String mountPoint,
        /** 端口（文档 Type=text，示例 "8002"） */
        String port,
        /** 主机地址 */
        String host,
        /** RTCM 设备类型 */
        Integer rtcmDeviceType,
        /** 数据源类型 */
        Integer sourceType
) {}
