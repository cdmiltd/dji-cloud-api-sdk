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

package ltd.cdmi.dji.cloudapi.sdk.command.service.pilot;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code poi_circle_speed_set} 指令请求 data：设置 POI 环绕速度（Pilot 上云）。
 *
 * <p>飞行控制—POI 环绕速度设置，需在 POI 环绕模式下调用。
 *
 * <p>字段集依据 DJI Pilot-to-Cloud drc.html poi_circle_speed_set Data 表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html")
@Verified(basis = "DJI Pilot-to-Cloud drc.html poi_circle_speed_set Data 表")
public record PoiCircleSpeedSetRequest(
        /**
         * 环绕速度值（单位 m/s）。
         * <p>负数代表顺时针转，正数代表逆时针转。
         * <p>最大值以 {@code poi_status_notify} 事件上报的最大环绕速度为准。
         */
        float circleSpeed
) {}
