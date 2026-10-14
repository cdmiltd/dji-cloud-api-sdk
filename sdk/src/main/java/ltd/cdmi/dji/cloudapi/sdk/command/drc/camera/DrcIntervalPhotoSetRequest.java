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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply;

/**
 * {@code drc_interval_photo_set} 指令请求 data：定时拍间隔设置。
 *
 * <p>回包复用 {@link DrcResultReply}。间隔键含小数 {@code "0.7"}（0.7/1/2/3/5/7/10/15/20/30/60 共 11 档），
 * 按 {@code Double} 承载。
 *
 * <p><b>标 @Inferred</b>：{@code interval} 键类型在 Dock2（enum_string）/Dock3（int）两页口径不一。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_INTERVAL_PHOTO_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_interval_photo_set Data 表 {payload_index, interval（0.7/1/2/3/5/7/10/15/20/30/60 秒）}")
@Inferred(
        reason = "interval 键类型在两页文档口径不一（enum_string vs int），wire 承载类型未统一确认",
        verifyPoint = "真机验证 drc_interval_photo_set.interval 的 wire 承载类型"
)
public record DrcIntervalPhotoSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 定时拍间隔（秒，可含小数如 0.7） */
        Double interval
) {
    public DrcIntervalPhotoSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(interval, "interval 必填，DJI JSON 缺失 interval 字段");
    }
}
