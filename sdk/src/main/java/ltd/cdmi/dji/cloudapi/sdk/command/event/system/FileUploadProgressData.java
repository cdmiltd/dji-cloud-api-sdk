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

package ltd.cdmi.dji.cloudapi.sdk.command.event.system;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code fileupload_progress} 事件 data（need_reply=0）：日志文件上传进度上报。
 *
 * <p>结构：{@code result + output{status?, ext{files[]{module, size, device_sn, key, fingerprint,
 * progress{progress, finish_time, upload_rate}}}}}。
 *
 * <p><b>标 @Inferred（§5-54）</b>：文档字段表 {@code output} 仅列 {@code ext}（无 {@code status}）、
 * {@code progress} 仅 3 字段；而 example {@code output} 含 {@code status:"ok"}、{@code progress} 7 字段
 * （{@code current_step/finish_time/progress/result/status/total_step/upload_rate}）。
 * 本 record 取「字段表 ∪ example 的交集并集」：保留 {@code status} 与 3 个 progress 字段，
 * 其余 example 独有字段待文档修订。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/log.html">
 * DJI O4 地面站远程日志</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FILEUPLOAD_PROGRESS
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/log.html")
@Verified(basis = "DJI O4 ground-station log.html fileupload_progress example：output{status, ext{files[]{module,size,device_sn,key,fingerprint,progress{progress,finish_time,upload_rate}}}}")
@Inferred(
        reason = "文档字段表 output 仅 ext（无 status）、progress 仅 3 字段，与 example（含 status + 7 字段）不一致",
        verifyPoint = "待 DJI 修订 fileupload_progress 字段表与 example 的口径差异"
)
public record FileUploadProgressData(
        /** 返回码（0=成功，非 0=错误） */
        Integer result,
        /** 输出结构 */
        Output output
) {

    /**
     * 输出结构。
     *
     * @param status 上传状态（如 ok；文档字段表未列，example 含）
     * @param ext    扩展信息（含 files 列表）
     */
    public record Output(
            String status,
            Ext ext
    ) {}

    /**
     * 扩展信息。
     *
     * @param files 文件上传项列表
     */
    public record Ext(
            List<FileItem> files
    ) {}

    /**
     * 单个文件上传项。
     *
     * @param module      模块标识
     * @param size        文件大小
     * @param deviceSn    设备 SN
     * @param key         对象存储 key
     * @param fingerprint 文件指纹
     * @param progress    上传进度
     */
    public record FileItem(
            String module,
            Long size,
            String deviceSn,
            String key,
            String fingerprint,
            Progress progress
    ) {}

    /**
     * 上传进度。
     *
     * @param progress   进度百分比（0-100）
     * @param finishTime 完成时间
     * @param uploadRate 上传速率
     */
    public record Progress(
            Integer progress,
            Long finishTime,
            Integer uploadRate
    ) {}
}
