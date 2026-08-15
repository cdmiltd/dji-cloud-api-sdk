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

package ltd.cdmi.dji.cloudapi.sdk.command.request.wayline;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code flighttask_resource_get} 指令回复 data：返回航线文件资源。
 *
 * <p>对应 DJI Cloud API {@code flighttask_resource_get} 指令（requests 通道）的回复 data。
 * {@code result=0} 成功；{@code output.file} 包含航线文件 URL 和指纹。
 *
 * <p>回复结构：{@code result + output{file{url, fingerprint}}}。
 *
 * <p>字段依据：simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/WaylineTaskSimulator.java#L1199">
 * WaylineTaskSimulator.publishFlighttaskResourceGet Javadoc</a> 注明回复结构 +
 * Dock3 wayline.html Requests flighttask_resource_get。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 Requests flighttask_resource_get</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator WaylineTaskSimulator Javadoc 注明回复结构 result + output{file{url, fingerprint}} + Dock3 wayline.html")
public record FlighttaskResourceGetReply(
    /** 返回码（0=成功，非 0=错误） */
    Integer result,
    /** 输出结构（含航线文件信息） */
    Output output
) {
    public FlighttaskResourceGetReply {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /**
     * 输出结构。
     *
     * @param file 航线文件信息
     */
    public record Output(
        /** 航线文件信息 */
        File file
    ) {}

    /**
     * 航线文件信息。
     *
     * @param url        航线文件下载 URL
     * @param fingerprint 航线文件指纹（MD5）
     */
    public record File(
        String url,
        String fingerprint
    ) {}
}
