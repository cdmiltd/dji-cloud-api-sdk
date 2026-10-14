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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.psdk;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_psdk_input_box_text_set} 指令（DRC 通道）请求 data：设置 PSDK 输入框文本。
 *
 * <p>services 通道同名指令见 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.psdk.PsdkInputBoxTextSetRequest}；
 * 本 record 为 DRC 通道（{@code drc/down}）镜像，回包复用
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_psdk_input_box_text_set Data 表 {psdk_index(int), value(text ≤128)}")
public record DrcPsdkInputBoxTextSetRequest(
        /** PSDK 设备索引 */
        int psdkIndex,
        /** 输入框文本内容（文档约束 length ≤ 128） */
        String value
) {}
