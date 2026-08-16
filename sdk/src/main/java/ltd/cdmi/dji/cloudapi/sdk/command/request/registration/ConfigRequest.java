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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * config 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code config} 指令（requests 通道）的请求 data。
 * 用于机场注册流程首步：获取平台 {@code app_id}/{@code app_license} 等配置信息。
 *
 * <p>{@code config_type} 固定 {@code "json"}，{@code config_scope} 固定 {@code "product"}。
 *
 * <p>字段依据：simulator {@code DockOnlineService.online()} L153-L156 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService.online() L153-L156 已对接 hivemind 验证")
public record ConfigRequest(
    String configType,
    String configScope
) {
    public ConfigRequest {
        Objects.requireNonNull(configType, "configType 必填，DJI JSON 缺失 config_type 字段");
        Objects.requireNonNull(configScope, "configScope 必填，DJI JSON 缺失 config_scope 字段");
    }
}
