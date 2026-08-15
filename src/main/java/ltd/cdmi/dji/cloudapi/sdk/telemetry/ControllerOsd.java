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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 遥控器 OSD 遥测数据结构（Pilot 上云模式）。
 *
 * <p>Pilot 上云模式下，遥控器作为网关设备（domain=2）上报 OSD 遥测数据。
 * 与机场/飞行器 OSD 相比，遥控器 OSD 字段集更精简。
 *
 * <p>使用包装类型允许 {@code null}，因为不同 Pilot 版本上报的字段集可能不同。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/remote-controller/properties.html">DJI 遥控器设备属性推送</a>
 *
 * @see DockOsd
 * @see DroneOsd
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/properties.html")
@Verified(basis = "DJI Cloud API 官方文档 RC Plus 遥控器设备属性推送属性列表")
public record ControllerOsd(

        /** 模式码，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode} */
        Integer modeCode,

        /** 纬度 */
        Double latitude,

        /** 经度 */
        Double longitude,

        /** 电池电量百分比（0-100） */
        Integer battery,

        /** 国家区域码（如 "CN"，仅 RC Pro 上报） */
        String country,

        /** 图传链路（struct：dongle_number/4g_link_state/sdr_link_state/link_workmode/sdr_quality/4g_quality 等） */
        Object wirelessLink,

        /** DRC 链路状态（0=未连接, 1=连接中, 2=已连接），见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DrcState} */
        Integer drcState,

        /** 椭球高度（米） */
        Double height
) {
}
