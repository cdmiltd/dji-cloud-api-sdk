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

package ltd.cdmi.dji.cloudapi.sdk.protocol.error;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API {@code err_infos} 结构（逐设备错误码）。
 *
 * <p>当一次批量操作涉及多个设备时，{@code output.err_infos} 数组按设备 SN 逐条给出错误码，
 * 便于平台定位具体设备的失败原因。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/error.html">Dock3 错误码</a>
 *
 * @param sn       设备 SN
 * @param err_code 错误码，见 {@link DjiErrorCode}
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/error.html")
@Verified(basis = "DJI Cloud API 官方文档 err_infos 结构：{sn, err_code}")
public record ErrorInfo(
        String sn,
        int err_code
) {
}
