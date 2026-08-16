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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * config 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code config} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功；{@code app_id}/{@code app_license} 用于 License 校验。
 *
 * <p>注意：config 回复的 data 直接含 {@code app_id}/{@code app_license}（非 output 包裹），
 * 与 services_reply 的 {@code {result, output}} 结构不同。
 *
 * <p>字段依据：simulator {@code DockOnlineService} L175/L183 读取 data.app_id/data.app_license
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService L175/L183 读取 data.app_id/data.app_license 已对接 hivemind 验证")
@Inferred(
    reason = "config 回复的 data 直接含 app_id/app_license（非 output 包裹），与 services_reply 的 {result, output} 结构不同",
    verifyPoint = "真机验证 config requests_reply 的完整字段集"
)
public record ConfigReply(
    Integer result,
    String appId,
    String appLicense,
    String url,
    String token
) {
    public ConfigReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }
}
