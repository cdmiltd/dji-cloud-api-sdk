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
 * {@code cloud_control_auth_request} 指令请求 data：请求授权云端控制（Pilot 上云）。
 *
 * <p>云用户向遥控器请求云端控制权授权。请求后，遥控器会出现"请求授权"的弹窗，
 * 由遥控器端用户确认是否授权。{@code user_id} 和 {@code user_callsign} 标识请求方，
 * {@code control_keys} 指定请求的控制权类型（目前仅支持 {@code "flight"} 飞行控制权）。
 *
 * <p>字段集依据 DJI Pilot-to-Cloud drc.html {@code cloud_control_auth_request} Data 表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 *
 * @see CloudControlReleaseRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html")
@Verified(basis = "DJI Pilot-to-Cloud drc.html cloud_control_auth_request Data 表")
public record CloudControlAuthRequest(
        /** 请求授权的云端用户 ID */
        String userId,
        /** 请求授权的云端用户昵称 */
        String userCallsign,
        /**
         * 控制权的键。
         * <p>请求飞行控制权，请填写 {@code "flight"}。
         * <p>约束：{@code {"size": 1, "item_type": "text"}}
         */
        List<String> controlKeys
) {}
