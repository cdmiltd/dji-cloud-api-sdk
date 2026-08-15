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
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * fileupload_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code fileupload_progress} 事件（events 通道）的 data。
 * 用于日志文件上传进度上报，{@code need_reply=1} 需平台回复，含执行结果与 output（status/ext），
 * ext.files 数组描述每个文件的上传进度。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FILEUPLOAD_PROGRESS}
 *
 * <p>字段依据：simulator {@code RemoteLogSimulator.publishFileUploadProgress}（L310-L352）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，4 层嵌套保留在类体内以保持高内聚）：
 * <ul>
 *   <li>{@link Output} — 输出对象（status/ext）</li>
 *   <li>{@link Output.Ext} — 扩展信息（files 数组）</li>
 *   <li>{@link Output.Ext.FileItem} — 单个文件信息（module/size/device_sn/key/fingerprint/progress）</li>
 *   <li>{@link Output.Ext.FileItem.FileProgress} — 单个文件上传进度（current_step/finish_time/progress/
 *       result/status/total_step/upload_rate）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log-upload.html")
@Verified(basis = "simulator RemoteLogSimulator.publishFileUploadProgress L310-L352 已对接 hivemind 验证")
public record FileuploadProgressData(
    Integer result,
    Output output
) {
    public FileuploadProgressData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** fileupload_progress 事件 output 字段。 */
    public record Output(
        String status,
        Ext ext
    ) {}

    /** output.ext 字段，含文件列表。 */
    public record Ext(
        List<FileItem> files
    ) {}

    /** output.ext.files 数组元素，单个文件信息。 */
    public record FileItem(
        String module,
        Long size,
        String deviceSn,
        String key,
        String fingerprint,
        FileProgress progress
    ) {}

    /** output.ext.files[].progress 字段，单个文件上传进度。 */
    public record FileProgress(
        Integer currentStep,
        Long finishTime,
        Integer progress,
        Integer result,
        String status,
        Integer totalStep,
        Integer uploadRate
    ) {}
}
