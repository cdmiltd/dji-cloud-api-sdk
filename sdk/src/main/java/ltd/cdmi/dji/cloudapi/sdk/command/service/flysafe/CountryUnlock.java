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
 * 国家解禁子结构（{@code licenses[].country_unlock}，{@link CommonFields#type()}=2）。
 *
 * <p>{@code country_number} 为 ISO-3166-1 数字码（如 156=中国），非协议枚举，按 {@code Integer} 承载。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html country_unlock = {country_number(ISO-3166-1 数字码), height}")
public record CountryUnlock(
        /** 国家数字码（ISO-3166-1，如 156=中国） */
        Integer countryNumber,
        /** 高度（米） */
        Integer height
) {}
