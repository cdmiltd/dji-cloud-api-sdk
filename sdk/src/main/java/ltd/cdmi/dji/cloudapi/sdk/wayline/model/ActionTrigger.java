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
 * WPML {@code <wpml:actionTrigger>} 元素。
 *
 * <p>动作触发器，定义触发类型与触发参数，控制所属 {@link ActionGroup} 中
 * {@link Action} 的执行时机。
 *
 * @see ActionGroup
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 actionTrigger 元素定义")
public record ActionTrigger(
    @JacksonXmlProperty(localName = "actionTriggerType", namespace = WpmlNamespaces.WPML)
    String actionTriggerType,
    @JacksonXmlProperty(localName = "actionTriggerParam", namespace = WpmlNamespaces.WPML)
    Double actionTriggerParam
) {}
