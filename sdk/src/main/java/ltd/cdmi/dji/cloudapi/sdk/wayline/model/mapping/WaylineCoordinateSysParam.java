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
 * WPML {@code <wpml:waylineCoordinateSysParam>} 元素。
 *
 * <p>航线坐标系参数，定义坐标模式、高度模式、拍摄高度、定位类型以及
 * 地面跟随相关配置。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 waylineCoordinateSysParam 元素定义")
public record WaylineCoordinateSysParam(
    @JacksonXmlProperty(localName = "coordinateMode", namespace = WpmlNamespaces.WPML)
    String coordinateMode,
    @JacksonXmlProperty(localName = "heightMode", namespace = WpmlNamespaces.WPML)
    String heightMode,
    @JacksonXmlProperty(localName = "globalShootHeight", namespace = WpmlNamespaces.WPML)
    Double globalShootHeight,
    @JacksonXmlProperty(localName = "positioningType", namespace = WpmlNamespaces.WPML)
    String positioningType,
    @JacksonXmlProperty(localName = "surfaceFollowModeEnable", namespace = WpmlNamespaces.WPML)
    Integer surfaceFollowModeEnable,
    @JacksonXmlProperty(localName = "surfaceRelativeHeight", namespace = WpmlNamespaces.WPML)
    Double surfaceRelativeHeight
) {}
