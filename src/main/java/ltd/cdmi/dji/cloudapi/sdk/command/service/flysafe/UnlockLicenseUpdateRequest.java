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
 * {@code unlock_license_update} 指令请求 data：更新设备的解禁证书。
 *
 * <p>字段集依据 DJI Dock3 flysafe.html unlock_license_update Data 表。
 *
 * <p>{@code file} 可缺省，缺省时按照 Flysafe 服务器最新证书进行更新。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html unlock_license_update Data 表")
public record UnlockLicenseUpdateRequest(
        /** 离线证书文件（可缺省，缺省时按 Flysafe 服务器最新证书更新） */
        LicenseFile file
) {
    /**
     * 离线证书文件信息。
     *
     * @param url        文件 URL
     * @param fingerprint 文件签名（MD5）
     */
    public record LicenseFile(
            String url,
            String fingerprint
    ) {}
}
