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
 * WPML {@code actionActuatorFuncParam}（focus）参数。
 *
 * <p>对焦动作参数。当 {@code isPointFocus = 1} 时为点对焦，使用 {@code focusX} /
 * {@code focusY}；当 {@code isPointFocus = 0} 时为区域对焦，需额外提供
 * {@code focusRegionWidth} / {@code focusRegionHeight}（可空）。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 focus actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record FocusParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "isPointFocus", namespace = WpmlNamespaces.WPML)
    int isPointFocus,
    @JacksonXmlProperty(localName = "focusX", namespace = WpmlNamespaces.WPML)
    double focusX,
    @JacksonXmlProperty(localName = "focusY", namespace = WpmlNamespaces.WPML)
    double focusY,
    @JacksonXmlProperty(localName = "focusRegionWidth", namespace = WpmlNamespaces.WPML)
    Double focusRegionWidth,
    @JacksonXmlProperty(localName = "focusRegionHeight", namespace = WpmlNamespaces.WPML)
    Double focusRegionHeight,
    @JacksonXmlProperty(localName = "isInfiniteFocus", namespace = WpmlNamespaces.WPML)
    Integer isInfiniteFocus
) implements ActionActuatorFuncParam {}
