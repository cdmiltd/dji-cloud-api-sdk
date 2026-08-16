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
 * DJI Cloud API 错误码描述条目（用于运行时查表）。
 *
 * <p>每个条目包含错误码（{@code code}）与官方描述（{@code desc}），
 * 由 {@link DjiErrorCode#describe(int)} 在运行时查询返回。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/error-code.html">
 * DJI Cloud API 错误码</a>
 *
 * @param code        错误码（6 位 ABCDEF 格式，或 0/1 通用码，或 210xxx HTTP API 注册绑定码）
 * @param description DJI 官方错误码描述（中文）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/error-code.html")
@Verified(basis = "DJI Cloud API 错误码文档：错误码 + 描述对照表")
public record DjiErrorInfo(
        int code,
        String description
) {
}
