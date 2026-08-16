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
 * {@code unlock_license_list} 指令请求 data：获取设备的解禁证书列表。
 *
 * <p>字段集依据 DJI Dock3 flysafe.html unlock_license_list Data 表。
 *
 * <p>回复结构较为复杂（含 licenses 数组，每项有 common_fields + 7 种解禁类型之一），
 * 此 POJO 仅封装请求参数；回复解析可按需扩展。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html unlock_license_list Data 表")
public record UnlockLicenseListRequest(
        /**
         * 指定证书拉取位置（enum_int：0=飞行器, 3=机场）。
         * <p>从飞行器拉取的是成功导入飞行器的解禁证书；
         * 从机场拉取的是 Flysafe 官网申请通过的解禁证书。
         */
        int deviceModelDomain
) {}
