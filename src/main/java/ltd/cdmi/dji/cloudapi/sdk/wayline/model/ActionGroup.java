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

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * WPML {@code <wpml:actionGroup>} 元素。
 *
 * <p>动作组，将一组 {@link Action} 关联到同一触发器 {@link ActionTrigger}，
 * 定义动作组 ID、起止航点索引、执行模式以及动作列表。
 *
 * @see ActionTrigger
 * @see Action
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 actionGroup 元素定义")
public record ActionGroup(
    @JacksonXmlProperty(localName = "actionGroupId", namespace = WpmlNamespaces.WPML)
    Integer actionGroupId,
    @JacksonXmlProperty(localName = "actionGroupStartIndex", namespace = WpmlNamespaces.WPML)
    Integer actionGroupStartIndex,
    @JacksonXmlProperty(localName = "actionGroupEndIndex", namespace = WpmlNamespaces.WPML)
    Integer actionGroupEndIndex,
    @JacksonXmlProperty(localName = "actionGroupMode", namespace = WpmlNamespaces.WPML)
    String actionGroupMode,
    @JacksonXmlProperty(localName = "actionTrigger", namespace = WpmlNamespaces.WPML)
    ActionTrigger actionTrigger,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "action", namespace = WpmlNamespaces.WPML)
    List<Action> actions
) {}
