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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.action;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML {@code actionActuatorFuncParam}（searchlight）参数。
 *
 * <p>探照灯动作参数，定义负载位置、动作标识（可选）、探照灯操作类型与亮度。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code actionUUID} — 动作唯一标识，<b>非必需</b>（目前 actionActuatorFuncParam 中
 *       唯一标注为"否"的 actionUUID 字段），故使用 {@code String} 而非基本类型</li>
 *   <li>{@code searchlightOperateType} — 探照灯操作类型，0=关灯，1=照明，2=爆闪</li>
 *   <li>{@code searchlightBrightness} — 亮度，范围 0-100</li>
 * </ul>
 *
 * <p>支持机型：M4D/M4TD。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 searchlight actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record SearchlightParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "actionUUID", namespace = WpmlNamespaces.WPML)
    String actionUUID,
    @JacksonXmlProperty(localName = "searchlightOperateType", namespace = WpmlNamespaces.WPML)
    int searchlightOperateType,
    @JacksonXmlProperty(localName = "searchlightBrightness", namespace = WpmlNamespaces.WPML)
    int searchlightBrightness
) implements ActionActuatorFuncParam {}
