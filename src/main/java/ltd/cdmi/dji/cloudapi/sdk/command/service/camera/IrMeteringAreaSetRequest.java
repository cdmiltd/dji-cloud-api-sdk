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

package ltd.cdmi.dji.cloudapi.sdk.command.service.camera;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * ir_metering_area_set 指令请求 data。
 *
 * <p>设置红外测光区域。
 *
 * <p>Reply 使用 {@link NoOutputReply}（services_reply 仅返回 result=0，无 output 字段）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator PayloadControlHandler 已对接 hivemind 验证")
public record IrMeteringAreaSetRequest(
    String payloadIndex,
    Double x,
    Double y,
    Double width,
    Double height
) {
    public IrMeteringAreaSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(x, "x 必填，DJI JSON 缺失 x 字段");
        Objects.requireNonNull(y, "y 必填，DJI JSON 缺失 y 字段");
        Objects.requireNonNull(width, "width 必填，DJI JSON 缺失 width 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON 缺失 height 字段");
    }
}
