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
 * {@code drc_ir_metering_area_set} 指令请求 data：红外区域测温设置。
 *
 * <p>回包复用 {@link DrcResultReply}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_IR_METERING_AREA_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_ir_metering_area_set Data 表 {payload_index, x, y, width, height}")
public record DrcIrMeteringAreaSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 测温区域 x（0–1） */
        Double x,
        /** 测温区域 y（0–1） */
        Double y,
        /** 测温区域宽（0–1） */
        Double width,
        /** 测温区域高（0–1） */
        Double height
) {
    public DrcIrMeteringAreaSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(x, "x 必填，DJI JSON 缺失 x 字段");
        Objects.requireNonNull(y, "y 必填，DJI JSON 缺失 y 字段");
        Objects.requireNonNull(width, "width 必填，DJI JSON 缺失 width 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON 缺失 height 字段");
    }
}
