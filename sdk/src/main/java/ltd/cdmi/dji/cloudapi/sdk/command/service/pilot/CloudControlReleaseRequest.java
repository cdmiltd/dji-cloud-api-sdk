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

package ltd.cdmi.dji.cloudapi.sdk.command.service.pilot;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code cloud_control_release} 指令请求 data：释放云端控制权（Pilot 上云）。
 *
 * <p>云用户释放已获取的云端控制权。{@code control_keys} 指定需要释放的控制权列表，
 * {@code "flight"} 代表飞行控制权。
 *
 * <p>字段集依据 DJI Pilot-to-Cloud drc.html {@code cloud_control_release} Data 表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 *
 * @see CloudControlAuthRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html")
@Verified(basis = "DJI Pilot-to-Cloud drc.html cloud_control_release Data 表")
public record CloudControlReleaseRequest(
        /**
         * 控制权列表。
         * <p>需要释放的控制权列表。{@code "flight"} 代表飞行控制权。
         * <p>约束：{@code {"size": 1, "item_type": "text"}}
         */
        List<String> controlKeys
) {}
