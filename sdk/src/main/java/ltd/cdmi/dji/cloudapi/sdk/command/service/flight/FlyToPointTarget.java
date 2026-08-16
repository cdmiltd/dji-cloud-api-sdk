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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * fly_to_point / fly_to_point_update 指令的目标点坐标。
 *
 * <p>DJI 协议规定这两个指令的 data 用 {@code points: [{latitude, longitude, height}]}
 * 数组结构（与 {@link TakeoffToPointRequest} 的 {@code target_latitude} 平铺字段不同），
 * 本 record 是数组元素的单点结构。
 *
 * <p>注：当前 DJI 文档示例均仅使用 points[0]（单目标点），但协议字段定义为数组，
 * SDK 按协议定义保留 List 结构，调用方可取 {@code points().get(0)} 或处理多点场景。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.handleFlyToPoint/handleFlyToPointUpdate 已对接 hivemind 验证 points 数组结构")
public record FlyToPointTarget(
    Double latitude,
    Double longitude,
    Double height
) {
    public FlyToPointTarget {
        Objects.requireNonNull(latitude, "latitude 必填，DJI JSON points[*] 缺失 latitude 字段");
        Objects.requireNonNull(longitude, "longitude 必填，DJI JSON points[*] 缺失 longitude 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON points[*] 缺失 height 字段");
    }
}
