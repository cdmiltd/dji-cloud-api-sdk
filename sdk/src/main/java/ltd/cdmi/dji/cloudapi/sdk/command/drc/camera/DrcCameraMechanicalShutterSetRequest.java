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
 * {@code drc_camera_mechanical_shutter_set} 指令请求 data：机械快门设置。
 *
 * <p>回包复用 {@link DrcResultReply}。{@code mechanical_shutter_state} 值域见
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype.SwitchState}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_MECHANICAL_SHUTTER_SET
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_mechanical_shutter_set Data 表 {payload_index, camera_type, mechanical_shutter_state(enum_int 0/1)}")
public record DrcCameraMechanicalShutterSetRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 相机类型 */
        String cameraType,
        /** 机械快门状态：0=关闭, 1=开启 */
        Integer mechanicalShutterState
) {
    public DrcCameraMechanicalShutterSetRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(cameraType, "cameraType 必填，DJI JSON 缺失 camera_type 字段");
        Objects.requireNonNull(mechanicalShutterState, "mechanicalShutterState 必填，DJI JSON 缺失 mechanical_shutter_state 字段");
    }
}
