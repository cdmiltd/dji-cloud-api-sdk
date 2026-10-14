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
 * {@code drc_camera_look_at} 指令请求 data：让相机看向指定目标点（经纬度 + 高度）。
 *
 * <p>回包复用 {@link DrcResultReply}。无论画框/变焦进行中，只要 cloud 调用都会被响应，
 * 相反命令会被置为 false。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_CAMERA_LOOK_AT
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_look_at Data 表 {payload_index, locked, latitude, longitude, height}")
public record DrcCameraLookAtRequest(
        /** 相机枚举，格式 {type-subtype-gimbalindex} */
        String payloadIndex,
        /** 是否锁定机头 */
        Boolean locked,
        /** 目标纬度 */
        Double latitude,
        /** 目标经度 */
        Double longitude,
        /** 目标高度（米） */
        Double height
) {
    public DrcCameraLookAtRequest {
        Objects.requireNonNull(payloadIndex, "payloadIndex 必填，DJI JSON 缺失 payload_index 字段");
        Objects.requireNonNull(locked, "locked 必填，DJI JSON 缺失 locked 字段");
        Objects.requireNonNull(latitude, "latitude 必填，DJI JSON 缺失 latitude 字段");
        Objects.requireNonNull(longitude, "longitude 必填，DJI JSON 缺失 longitude 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON 缺失 height 字段");
    }
}
