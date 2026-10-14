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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flysafe;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 解禁证书通用字段（{@code unlock_license_list.licenses[].common_fields}）。
 *
 * <p>{@code begin_time} / {@code end_time} 为<b>秒级</b> UNIX 时间戳。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html unlock_license_list common_fields = {license_id, name, type(0–6), group_id, user_id, device_sn, begin_time(秒级), end_time(秒级), user_only, enabled}")
public record CommonFields(
        /** 证书 ID */
        Integer licenseId,
        /** 证书名称 */
        String name,
        /** 解禁类型：0=区域, 1=圆形, 2=国家, 3=高度, 4=多边形, 5=动力, 6=RID */
        Integer type,
        /** 分组 ID */
        Integer groupId,
        /** 用户 ID */
        String userId,
        /** 设备 SN */
        String deviceSn,
        /** 生效时间（秒级 UNIX 时间戳） */
        Integer beginTime,
        /** 失效时间（秒级 UNIX 时间戳） */
        Integer endTime,
        /** 是否仅用户可用 */
        Boolean userOnly,
        /** 是否启用 */
        Boolean enabled
) {}
