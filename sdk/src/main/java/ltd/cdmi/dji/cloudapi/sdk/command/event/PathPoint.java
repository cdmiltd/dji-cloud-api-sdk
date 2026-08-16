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

package ltd.cdmi.dji.cloudapi.sdk.command.event;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 航迹点（经纬高坐标），events 通道共享 record。
 *
 * <p>对应 DJI Cloud API events 通道多个事件 data 中 {@code planned_path_points} 数组的元素，
 * 描述飞行轨迹的坐标点。被以下事件共享使用：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.ReturnHomeInfoData#plannedPathPoints()}</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.FlyToPointProgressData#plannedPathPoints()}</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.TakeoffToPointProgressData#plannedPathPoints()}</li>
 * </ul>
 *
 * <p>字段对应 DJI JSON 的 {@code latitude}/{@code longitude}/{@code height}，
 * Jackson SNAKE_CASE 策略下与 camelCase 字段名自动映射（latitude/longitude 同名，height 同名）。
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.buildPathPoints} 构建轨迹点已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.buildPathPoints 构建轨迹点已对接 hivemind 验证")
public record PathPoint(
    Double latitude,
    Double longitude,
    Double height
) {
    public PathPoint {
        Objects.requireNonNull(latitude, "latitude 必填，DJI JSON 缺失 latitude 字段");
        Objects.requireNonNull(longitude, "longitude 必填，DJI JSON 缺失 longitude 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON 缺失 height 字段");
    }
}
