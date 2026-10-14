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

import com.fasterxml.jackson.annotation.JsonAlias;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.WirelessLink;

/**
 * 遥控器 OSD 遥测数据结构（Pilot 上云模式）。
 *
 * <p>Pilot 上云模式下，遥控器（RC，Remote Controller）作为网关设备（domain=2）上报 OSD 遥测数据。
 * 与机场/飞行器 OSD 相比，遥控器 OSD 字段集更精简。
 *
 * <p>使用包装类型允许 {@code null}，因为不同 Pilot 版本上报的字段集可能不同。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/others/rc/properties.html">DJI 遥控器设备属性推送</a>
 *
 * @see DockOsd
 * @see DroneOsd
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/others/rc/properties.html")
@Verified(basis = "DJI Cloud API 通用 RC 设备属性页（others/rc/properties.html），与 RC Pro / RC Plus 2 机型页逐字一致；osd 含 capacity_percent，无 mode_code")
public record RcOsd(

        /** 纬度 */
        Double latitude,

        /** 经度 */
        Double longitude,

        /**
         * 遥控器剩余电量百分比（0-100），文档 wire key 为 {@code capacity_percent}。
         * <p>兼容旧 wire key {@code battery}（{@link JsonAlias}），真机核对后移除兼容。
         */
        @JsonAlias("battery")
        Integer capacityPercent,

        /** 国家区域码（如 "CN"，仅 RC Pro 上报） */
        String country,

        /** 图传链路 */
        WirelessLink wirelessLink,

        /** DRC 链路状态（0=未连接, 1=连接中, 2=已连接），见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DrcState} */
        Integer drcState,

        /** 椭球高度（米） */
        Double height
) {
}
