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

package ltd.cdmi.dji.cloudapi.sdk.examples;

import ltd.cdmi.dji.cloudapi.sdk.http.HttpApiPath;
import ltd.cdmi.dji.cloudapi.sdk.http.HttpResponseEnvelope;
import ltd.cdmi.dji.cloudapi.sdk.http.StsCredentials;

/**
 * 示例 4：HTTP API 调用。
 *
 * <p>演示 HttpApiPath 路径常量拼接 + HttpResponseEnvelope.parse 响应解析。
 * 使用硬编码 JSON 模拟 HTTP 响应，无需 HTTP 客户端。
 *
 * <p>关键点：
 * <ul>
 *   <li>路径常量中的 {@code {workspace_id}} 等占位符由调用方替换</li>
 *   <li>{@code parse(body, StsCredentials.class)} 一步提取 code/message/data，
 *       避免直接 {@code fromJson} 把信封字段当 POJO 顶层字段的 Bug</li>
 *   <li>code != 0 时 data 为 null，先判 code 再取 data</li>
 * </ul>
 */
public class HttpApiExample {

    public static void main(String[] args) {
        String workspaceId = "ws-001";
        String waylineId = "wl-001";

        // 1. 路径拼接：获取航线文件下载地址
        String waylineUrlPath = HttpApiPath.WAYLINE_URL
                .replace("{workspace_id}", workspaceId)
                .replace("{wayline_id}", waylineId);
        System.out.println("航线下载路径: " + waylineUrlPath);

        // 2. STS 凭证路径（媒体/航线上传文件前共用）
        String stsPath = HttpApiPath.STS.replace("{workspace_id}", workspaceId);
        System.out.println("STS 凭证路径: " + stsPath);

        // 3. 模拟 STS 响应（实际由 HTTP 客户端返回）
        String stsResponse = """
                {"code":0,"message":"success","data":{
                  "bucket":"dji-upload","endpoint":"oss-cn-hangzhou.aliyuncs.com",
                  "region":"cn-hangzhou","provider":"aliyun",
                  "credentials":{"access_key_id":"AK-DEMO","access_key_secret":"SK-DEMO",
                                 "security_token":"***","expiration":"2026-09-04T12:00:00Z"},
                  "object_key_prefix":"ws-001/flight-001"
                }}""";

        // 4. 用 HttpResponseEnvelope.parse 一步提取 code + message + data
        var envelope = HttpResponseEnvelope.parse(stsResponse, StsCredentials.class);
        if (envelope.code() == 0) {
            System.out.println("STS 获取成功:");
            System.out.println("  bucket = " + envelope.data().bucket());
            System.out.println("  endpoint = " + envelope.data().endpoint());
            System.out.println("  provider = " + envelope.data().provider());
            // credentials 子结构 SDK 不固化（Object 持有），由调用方按服务商 SDK 解析
            System.out.println("  credentials(raw) = " + envelope.data().credentials());
            System.out.println("  objectKeyPrefix = " + envelope.data().objectKeyPrefix());
        } else {
            System.out.println("STS 获取失败: code=" + envelope.code() + ", message=" + envelope.message());
        }

        // 5. 模拟错误响应：code != 0 时 data 为 null，先判 code 再取 data
        String errorResponse = """
                {"code":314001,"message":"飞行任务下发失败，请稍后重试","data":null}""";
        var errEnvelope = HttpResponseEnvelope.parse(errorResponse, StsCredentials.class);
        if (errEnvelope.code() != 0) {
            System.out.println("HTTP API 错误: code=" + errEnvelope.code()
                    + ", message=" + errEnvelope.message()
                    + ", data=" + errEnvelope.data());
        }
    }
}
