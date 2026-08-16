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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * WPML {@code <wpml:missionConfig>} 元素。
 *
 * <p>航线任务全局配置，包含飞行模式、结束动作、RC 失联策略、安全起飞高度、
 * 全局过渡速度、返航高度、自动绕行信息以及无人机/负载信息。
 *
 * <p>枚举类型字段（flyToWaylineMode / finishAction / exitOnRCLost /
 * executeRCLostAction）使用 {@code String} 承载，由 Builder 调用
 * 对应枚举的 {@code code()} 方法赋值。
 *
 * <p>{@code autoRerouteInfo} 为可选字段，仅 M3D/M3TD、M4D/M4TD、M4E/M4T 机型支持，
 * 其他机型传 {@code null}（WpmlCodec 的 NON_NULL 策略自动跳过）。
 *
 * @see DroneInfo
 * @see PayloadInfo
 * @see AutoRerouteInfo
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 missionConfig 元素定义")
public record MissionConfig(
    @JacksonXmlProperty(localName = "flyToWaylineMode", namespace = WpmlNamespaces.WPML)
    String flyToWaylineMode,
    @JacksonXmlProperty(localName = "finishAction", namespace = WpmlNamespaces.WPML)
    String finishAction,
    @JacksonXmlProperty(localName = "exitOnRCLost", namespace = WpmlNamespaces.WPML)
    String exitOnRCLost,
    @JacksonXmlProperty(localName = "executeRCLostAction", namespace = WpmlNamespaces.WPML)
    String executeRCLostAction,
    @JacksonXmlProperty(localName = "takeOffSecurityHeight", namespace = WpmlNamespaces.WPML)
    Double takeOffSecurityHeight,
    @JacksonXmlProperty(localName = "takeOffRefPoint", namespace = WpmlNamespaces.WPML)
    String takeOffRefPoint,
    @JacksonXmlProperty(localName = "takeOffRefPointAGLHeight", namespace = WpmlNamespaces.WPML)
    Double takeOffRefPointAGLHeight,
    @JacksonXmlProperty(localName = "globalTransitionalSpeed", namespace = WpmlNamespaces.WPML)
    Double globalTransitionalSpeed,
    @JacksonXmlProperty(localName = "globalRTHHeight", namespace = WpmlNamespaces.WPML)
    Double globalRTHHeight,
    @JacksonXmlProperty(localName = "autoRerouteInfo", namespace = WpmlNamespaces.WPML)
    AutoRerouteInfo autoRerouteInfo,
    @JacksonXmlProperty(localName = "droneInfo", namespace = WpmlNamespaces.WPML)
    DroneInfo droneInfo,
    @JacksonXmlProperty(localName = "payloadInfo", namespace = WpmlNamespaces.WPML)
    PayloadInfo payloadInfo
) {}
