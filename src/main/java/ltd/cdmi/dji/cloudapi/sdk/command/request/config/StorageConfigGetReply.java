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
 * storage_config_get 指令回复 data。
 *
 * <p>对应 DJI Cloud API {@code storage_config_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功，{@code output} 含对象存储配置（bucket/endpoint/credentials 等）。
 *
 * <p>字段依据：simulator {@code StorageConfig.fromReply} L72-L86 读取 data.output.*
 * 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/media.html")
@Verified(basis = "simulator StorageConfig.fromReply L72-L86 读取 data.output.* 已对接 hivemind 验证")
public record StorageConfigGetReply(
    Integer result,
    Output output
) {
    public StorageConfigGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** output 字段，对象存储配置。 */
    public record Output(
        String bucket,
        String endpoint,
        String region,
        String provider,
        String objectKeyPrefix,
        Credentials credentials
    ) {}

    /** output.credentials 字段，临时访问凭证。 */
    public record Credentials(
        String accessKeyId,
        String accessKeySecret,
        String securityToken,
        Long expireTime
    ) {}
}
