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

package ltd.cdmi.dji.cloudapi.sdk.command.event.psdk;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * psdk_ui_resource_upload_result 事件 data。
 *
 * <p>对应 DJI Cloud API {@code psdk_ui_resource_upload_result} 事件（events 通道）的 data。
 * 用于 PSDK UI 资源包上传结果上报，{@code need_reply=0} 单向通知，
 * data 直接平铺 psdk_index + object_key + size + result（非 output 包裹）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#PSDK_UI_RESOURCE_UPLOAD_RESULT}
 *
 * <p>字段依据：simulator {@code PsdkSimulator.triggerUiResourceUploadResult}（L403-L427）
 * 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html">
 * DJI Dock3 PSDK events</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html")
@Verified(basis = "simulator PsdkSimulator.triggerUiResourceUploadResult L403-L427 已对接 hivemind 验证")
public record PsdkUiResourceUploadResultData(
    /** PSDK 负载设备索引 */
    int psdkIndex,
    /** OSS 对象 key */
    String objectKey,
    /** 文件大小（字节） */
    long size,
    /** 错误码（0=成功） */
    int result
) {}
