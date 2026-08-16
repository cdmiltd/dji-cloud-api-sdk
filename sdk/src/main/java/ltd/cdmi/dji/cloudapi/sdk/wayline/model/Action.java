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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam;

/**
 * WPML {@code <wpml:action>} 元素。
 *
 * <p>单个动作定义，包含动作 ID、执行函数名以及执行函数参数
 * {@link ActionActuatorFuncParam}（密封接口，具体参数由各 action param record 实现）。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 action 元素定义")
@JsonDeserialize(using = ActionDeserializer.class)
public record Action(
    @JacksonXmlProperty(localName = "actionId", namespace = WpmlNamespaces.WPML)
    Integer actionId,
    @JacksonXmlProperty(localName = "actionActuatorFunc", namespace = WpmlNamespaces.WPML)
    String actionActuatorFunc,
    @JacksonXmlProperty(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
    ActionActuatorFuncParam actionActuatorFuncParam
) {}
