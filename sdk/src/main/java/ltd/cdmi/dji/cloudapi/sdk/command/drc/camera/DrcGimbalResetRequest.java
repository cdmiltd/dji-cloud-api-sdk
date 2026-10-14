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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply;

/**
 * {@code drc_gimbal_reset} 指令请求 data：云台复位。
 *
 * <p>回包复用 {@link DrcResultReply}。{@code reset_mode} 值域见
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.GimbalResetMode}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_GIMBAL_RESET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_gimbal_reset Data 表 {payload_index, reset_mode(enum_int 0–3)}")
public record DrcGimbalResetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 复位模式：0=回中, 1=向下, 2=偏航回中, 3=俯仰向下 */
        Integer resetMode
) {
    public DrcGimbalResetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(resetMode, "resetMode 必填，DJI JSON 缺失 reset_mode 字段");
    }
}
