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

package ltd.cdmi.dji.cloudapi.sdk.command.service.fts;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply;

/**
 * {@code drone_propeller_stop_v1} 指令请求 data：飞行器桨叶停止（需 FTS 验证码）。
 *
 * <p>回复仅 {@code {result}} → 复用 {@link NoOutputReply}。
 *
 * <p>验证码来源：飞行器 OSD {@code fts_info.fts_verification_code}（包含数字和大写字母，共 8 位），
 * 该验证码以顶层扁平字段 {@code fts_verification_code} 承载。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#DRONE_PROPELLER_STOP_V1
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/fts.html")
@Verified(basis = "DJI Dock3 FTS services 文档 drone_propeller_stop_v1 Data 表 {fts_verification_code(text 8 位数字/大写字母)}")
public record DronePropellerStopV1Request(
        /** FTS 验证码（8 位，数字 + 大写字母） */
        String ftsVerificationCode
) {
    public DronePropellerStopV1Request {
        Objects.requireNonNull(ftsVerificationCode, "ftsVerificationCode 必填，DJI JSON 缺失 fts_verification_code 字段");
    }
}
