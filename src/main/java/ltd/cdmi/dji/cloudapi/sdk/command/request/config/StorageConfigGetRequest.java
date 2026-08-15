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

package ltd.cdmi.dji.cloudapi.sdk.command.request.config;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * storage_config_get 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code storage_config_get} 指令（requests 通道）的请求 data。
 * 用于获取对象存储配置（媒体/日志上传）。
 *
 * <p>{@code module} 取值：{@code 0=媒体}、{@code 1=日志}。
 *
 * <p>字段依据：simulator {@code MediaUploadSimulator.fetchStorageConfig} L243 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/media.html")
@Verified(basis = "simulator MediaUploadSimulator.fetchStorageConfig L243 已对接 hivemind 验证")
public record StorageConfigGetRequest(
    Integer module
) {
    public StorageConfigGetRequest {
        Objects.requireNonNull(module, "module 必填，DJI JSON 缺失 module 字段");
    }
}
