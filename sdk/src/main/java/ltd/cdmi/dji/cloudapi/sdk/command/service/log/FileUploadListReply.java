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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code fileupload_list} 指令回复 output：返回设备可上传的日志文件列表。
 *
 * <p>回复结构为 {@code {result:0, output:{files:[...]}}}，{@code files} 每个元素对应一个模块，
 * 含 {@code device_sn}（设备 SN）+ {@code result}（模块级结果码）+ {@code module}（模块标识）+
 * {@code list}（该模块的日志文件列表）。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/RemoteLogSimulator.java#L217-L265">
 * RemoteLogSimulator.handleFileUploadList</a> 已对接 hivemind 验证。
 *
 * <p><b>M-2 诊断日志</b>（{@link Inferred}）：
 * <ul>
 *   <li>{@code end_time} 字段拼写差异 — DJI Example 中第二个 list 项将 {@code end_time} 误写为 {@code end_ime}，
 *       Column 表为 {@code end_time}（正确），SDK 按 Column 表正确拼写</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html">
 * DJI Dock3 远程日志 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html")
@Verified(basis = "DJI Dock3 log.html fileupload_list services_reply Data 表 + simulator RemoteLogSimulator.handleFileUploadList 已对接 hivemind 验证")
@Inferred(
        reason = "DJI Example 中 end_time 误写为 end_ime，SDK 按 Column 表正确拼写 end_time",
        verifyPoint = "待 DJI 修正 Example 中的 end_ime 拼写错误"
)
public record FileUploadListReply(
        /** 模块文件列表 */
        List<FileGroup> files,
        /** 顶层结果码（0=成功） */
        int result
) {
    /**
     * 单个模块的文件组。
     *
     * @param deviceSn 设备 SN（飞行器或机场）
     * @param result   模块级结果码（0=成功）
     * @param module   模块标识（"0"=飞行器, "3"=机场, ...）
     * @param list     该模块的日志文件列表
     */
    public record FileGroup(
            String deviceSn,
            int result,
            String module,
            List<LogFile> list
    ) {}

    /**
     * 单个日志文件信息。
     *
     * @param bootIndex 启动序号
     * @param startTime 日志开始时间（毫秒，DJI Column 表标注 unit_name=毫秒/ms）
     * @param endTime   日志结束时间（毫秒，DJI Column 表标注 unit_name=毫秒/ms；DJI Example 误写为 end_ime）
     * @param size      文件大小（字节）
     */
    public record LogFile(
            int bootIndex,
            long startTime,
            long endTime,
            long size
    ) {}
}
