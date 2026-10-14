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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply;

/**
 * {@code drc_stealth_state_set} 指令请求 data：隐蔽模式开关。
 *
 * <p>回包复用 {@link DrcResultReply}（{@code {result}}）。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_STEALTH_STATE_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_stealth_state_set Data 表 {stealth_state(enum_int 0/1)}")
public record DrcStealthStateSetRequest(
        /** 隐蔽模式状态：0=关闭, 1=开启 */
        Integer stealthState
) {
    public DrcStealthStateSetRequest {
        Objects.requireNonNull(stealthState, "stealthState 必填，DJI JSON 缺失 stealth_state 字段");
    }
}
