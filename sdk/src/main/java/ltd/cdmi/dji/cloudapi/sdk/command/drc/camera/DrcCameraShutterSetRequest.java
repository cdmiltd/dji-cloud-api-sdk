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
 * {@code drc_camera_shutter_set} 指令请求 data：电子快门设置。
 *
 * <p>回包复用 {@link DrcResultReply}。值域参考
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.ShutterValue}（0–60）。
 *
 * <p><b>标 @Inferred</b>：文档未列出 0–59 各档快门速度映射。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_SHUTTER_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_shutter_set Data 表 {payload_index, camera_type, shutter_value(enum_int 0–60)}")
@Inferred(
        reason = "文档未列出 shutter_value 0–59 各档快门速度映射",
        verifyPoint = "按 DJI 文档原文补全 0–59 快门速度档位映射"
)
public record DrcCameraShutterSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机类型 */
        String cameraType,
        /** 快门值：0–59 各档, 60=自动 */
        Integer shutterValue
) {
    public DrcCameraShutterSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraType, "cameraType 必填，DJI JSON 缺失 camera_type 字段");
        Objects.requireNonNull(shutterValue, "shutterValue 必填，DJI JSON 缺失 shutter_value 字段");
    }
}
