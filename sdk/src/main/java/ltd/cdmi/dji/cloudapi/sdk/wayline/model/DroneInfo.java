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
 * WPML {@code <wpml:droneInfo>} 元素。
 *
 * <p>标识航线模板目标机型，{@code droneEnumValue} 为主类型枚举值，
 * {@code droneSubEnumValue} 为子类型枚举值。
 *
 * @see PayloadInfo
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 droneInfo 元素定义")
public record DroneInfo(
    @JacksonXmlProperty(localName = "droneEnumValue", namespace = WpmlNamespaces.WPML)
    int droneEnumValue,
    @JacksonXmlProperty(localName = "droneSubEnumValue", namespace = WpmlNamespaces.WPML)
    int droneSubEnumValue
) {}
