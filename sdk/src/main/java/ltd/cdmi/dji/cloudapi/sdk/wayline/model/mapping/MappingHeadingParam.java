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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML {@code <wpml:mappingHeadingParam>} 元素。
 *
 * <p>建图航拍飞行器朝向参数，定义偏航角模式与角度。
 * 仅用于建图航拍（mapping2d）模板，支持机型 M3E/M3T/M3M、M3D/M3TD。
 *
 * @see Mapping2dPlacemark
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 mappingHeadingParam 元素定义")
public record MappingHeadingParam(
    @JacksonXmlProperty(localName = "mappingHeadingMode", namespace = WpmlNamespaces.WPML)
    String mappingHeadingMode,
    @JacksonXmlProperty(localName = "mappingHeadingAngle", namespace = WpmlNamespaces.WPML)
    Integer mappingHeadingAngle
) {}
