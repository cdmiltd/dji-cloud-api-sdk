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
 * WPML {@code <wpml:autoRerouteInfo>} 元素。
 *
 * <p>自动绕行信息，定义任务航线与过渡航线的绕行模式。当航线遇到禁飞区/限飞区时，
 * 飞行器将根据该配置自动绕行。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code missionAutoRerouteMode} — 任务航线绕行模式（0=不开启, 1=开启）</li>
 *   <li>{@code transitionalAutoRerouteMode} — 过渡航线绕行模式（0=不开启, 1=开启）</li>
 * </ul>
 *
 * <p>支持机型：M3D/M3TD、M4D/M4TD、M4E/M4T。
 * 该元素在 {@link MissionConfig} 中为可选字段（其他机型不输出）。
 *
 * @see MissionConfig
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 autoRerouteInfo 元素定义")
public record AutoRerouteInfo(
    @JacksonXmlProperty(localName = "missionAutoRerouteMode", namespace = WpmlNamespaces.WPML)
    int missionAutoRerouteMode,
    @JacksonXmlProperty(localName = "transitionalAutoRerouteMode", namespace = WpmlNamespaces.WPML)
    int transitionalAutoRerouteMode
) {}
