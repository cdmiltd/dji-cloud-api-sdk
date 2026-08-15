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
 * {@code poi_mode_enter} 指令请求 data：进入 POI 环绕模式（Pilot 上云）。
 *
 * <p>飞行控制—进入 POI 环绕模式，使飞行器围绕指定目标点环绕飞行。
 *
 * <p>字段集依据 DJI Pilot-to-Cloud drc.html poi_mode_enter Data 表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html")
@Verified(basis = "DJI Pilot-to-Cloud drc.html poi_mode_enter Data 表")
public record PoiModeEnterRequest(
        /** 目标点纬度（角度值，南纬负/北纬正，精度到小数点后 6 位，范围 -90~90） */
        double latitude,
        /** 目标点经度（角度值，东经正/西经负，精度到小数点后 6 位，范围 -180~180） */
        double longitude,
        /**
         * 目标点高度（椭球高，相对起飞点，单位米，范围 2~10000）。
         * <p>对齐用户偏好：所有高度参数相对起飞点。
         */
        float height
) {}
