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

package ltd.cdmi.dji.cloudapi.sdk.command.service.log;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code fileupload_start} 指令请求 data：发起日志文件上传。
 *
 * <p>simulator 解析文件列表后返回 {@code {result:0}}，并启动异步进度模拟
 * （in_progress 50% → ok 100%）。进度通过 events 通道 {@code fileupload_progress} 异步上报。
 *
 * <p>字段集依据 DJI Dock3 文档 + simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/RemoteLogSimulator.java#L154-L190">
 * RemoteLogSimulator.handleFileUploadStart</a> 已对接 hivemind 验证。
 *
 * <p><b>注</b>：DJI 文档 Example 中 {@code files} 字段被包在 {@code params} 子对象下
 * （{@code data.params.files}），与其他 services 方法的 {@code data.xxx} 直接平铺不同。
 * simulator 实现与 DJI Example 一致，按 {@code params.files} 解析。
 *
 * <p>storage 凭证字段（bucket/region/credentials/endpoint/provider）依据 DJI Dock3
 * log.html fileupload_start Data 表，simulator 未解析这些字段（仅由设备端用于上传）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html">
 * DJI Dock3 远程日志 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html")
@Verified(basis = "DJI Dock3 log.html fileupload_start Data 表 + simulator RemoteLogSimulator.handleFileUploadStart 已对接 hivemind 验证")
public record FileUploadStartRequest(
        /** 对象存储桶名称 */
        String bucket,
        /** 数据中心所在的地域 */
        String region,
        /** 凭证信息 */
        Credentials credentials,
        /** 对外服务的访问域名 */
        String endpoint,
        /** 云厂商枚举值（enum_string：ali=阿里云, aws=亚马逊云, minio=minio） */
        String provider,
        /** 上传参数（含 files 列表） */
        FileUploadParams params
) {
    /**
     * 凭证信息。
     *
     * @param accessKeyId     访问密钥 ID
     * @param accessKeySecret 秘密访问密钥
     * @param expire          访问密钥过期时间（秒）
     * @param securityToken   会话凭证
     */
    public record Credentials(
            String accessKeyId,
            String accessKeySecret,
            Long expire,
            String securityToken
    ) {}

    /**
     * 上传参数子结构。
     *
     * @param files 文件列表（每个文件含 module + object_key + list）
     */
    public record FileUploadParams(
            List<FileUploadFile> files
    ) {}

    /**
     * 单个文件上传项。
     *
     * @param module    模块标识（"0"=飞行器, "3"=机场, ...）
     * @param objectKey 对象存储 key 前缀
     * @param list      待上传文件 boot_index 列表
     */
    public record FileUploadFile(
            String module,
            String objectKey,
            List<FileUploadBoot> list
    ) {
        /**
         * 单个 boot_index 项。
         *
         * @param bootIndex 启动序号
         */
        public record FileUploadBoot(
                int bootIndex
        ) {}
    }
}
