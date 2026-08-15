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

package ltd.cdmi.dji.cloudapi.sdk.http;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * STS 上传临时凭证（DJI Pilot 上云文件上传共用凭证）。
 *
 * <p>媒体管理（{@link HttpApiPath#FAST_UPLOAD} / {@link HttpApiPath#MEDIA_UPLOAD_CALLBACK} 等）
 * 与航线管理（{@link HttpApiPath#WAYLINES} / {@link HttpApiPath#WAYLINE_UPLOAD_CALLBACK} 等）
 * 上传文件前，均通过 {@link HttpApiPath#STS POST /storage/api/v1/workspaces/{workspace_id}/sts}
 * 获取此凭证，再用凭证向对象存储上传文件。
 *
 * <p>顶层字段（bucket/endpoint/region/provider/credentials/object_key_prefix）依据
 * simulator [StorageApi.java](file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/http/api/StorageApi.java#L61)
 * 注释（已对接 hivemind 验证），但 DJI 官方文档因 Cookie 拦截未直接核实，标 {@link Inferred}。
 *
 * <p>{@link #credentials} 子结构（access_key_id / access_key_secret / security_token / expire / expiration）
 * 在 DJI 文档中未明确字段名（疑似 snake_case），SDK 不固化具体字段，统一用 {@code Object} 持有，
 * 由调用方按对象存储服务商（aws/aliyun/tencent/minio）SDK 自行解析。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/https/media-management/generate-upload-credentials.html">
 * DJI 生成上传文件临时凭证</a>
 *
 * @param bucket          对象存储桶名
 * @param endpoint        对象存储接入点
 * @param region          对象存储区域
 * @param provider        对象存储服务商（aws/aliyun/tencent/minio）
 * @param credentials     STS 临时凭证（子结构由调用方按服务商 SDK 解析）
 * @param objectKeyPrefix 对象存储 key 前缀（上传时拼接到文件名前，格式 {@code {prefix}/{flight_id}/{file_name}}）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/https/media-management/generate-upload-credentials.html")
@Inferred(
        reason = "顶层字段依据 simulator StorageApi.java 已对接 hivemind 验证的注释提取",
        verifyPoint = "DJI 官方文档因 Cookie 拦截未直接核实；credentials 子结构字段名（snake_case 或 camelCase）待真机/文档确认"
)
public record StsCredentials(
        String bucket,
        String endpoint,
        String region,
        String provider,
        Object credentials,
        String objectKeyPrefix
) {
}
