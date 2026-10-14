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

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code unlock_license_list} 指令回复 data（扁平结构）。
 *
 * <p>用于 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.envelope.FlatReplyEnvelope}（data 为扁平字段，
 * 自带 {@code result}，非 output 包裹）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#UNLOCK_LICENSE_LIST
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html unlock_license_list 回复 data = {result, device_model_domain, consistence, licenses[]}")
public record UnlockLicenseListReply(
        /** 返回码，0=成功 */
        Integer result,
        /** 设备域（0=飞行器, 3=机场） */
        Integer deviceModelDomain,
        /** 证书一致性（对比飞行器/机场证书是否一致） */
        Boolean consistence,
        /** 解禁证书列表 */
        List<License> licenses
) {
    public UnlockLicenseListReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
