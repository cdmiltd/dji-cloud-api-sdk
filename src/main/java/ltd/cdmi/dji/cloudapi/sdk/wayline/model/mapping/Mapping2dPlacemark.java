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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.Polygon;

/**
 * WPML template.kml {@code <Placemark>} 元素（建图航拍模板 mapping2d）。
 *
 * <p>建图航拍测区配置，包含标定飞行、高程优化、智能摆拍、拍照模式、
 * 航线方向、外扩距离、重叠率、全局高度以及测区多边形 {@link Polygon}。
 *
 * <p>字段顺序依照 DJI WPML template.kml 文档建图航拍模板元素定义。
 *
 * @see Polygon
 * @see Overlap
 * @see MappingHeadingParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档建图航拍模板元素定义")
public record Mapping2dPlacemark(
    @JacksonXmlProperty(localName = "caliFlightEnable", namespace = WpmlNamespaces.WPML)
    Integer caliFlightEnable,
    @JacksonXmlProperty(localName = "elevationOptimizeEnable", namespace = WpmlNamespaces.WPML)
    int elevationOptimizeEnable,
    @JacksonXmlProperty(localName = "smartObliqueEnable", namespace = WpmlNamespaces.WPML)
    Integer smartObliqueEnable,
    @JacksonXmlProperty(localName = "smartObliqueGimbalPitch", namespace = WpmlNamespaces.WPML)
    Integer smartObliqueGimbalPitch,
    @JacksonXmlProperty(localName = "shootType", namespace = WpmlNamespaces.WPML)
    String shootType,
    @JacksonXmlProperty(localName = "direction", namespace = WpmlNamespaces.WPML)
    int direction,
    @JacksonXmlProperty(localName = "margin", namespace = WpmlNamespaces.WPML)
    int margin,
    @JacksonXmlProperty(localName = "overlap", namespace = WpmlNamespaces.WPML)
    Overlap overlap,
    @JacksonXmlProperty(localName = "ellipsoidHeight", namespace = WpmlNamespaces.WPML)
    double ellipsoidHeight,
    @JacksonXmlProperty(localName = "height", namespace = WpmlNamespaces.WPML)
    double height,
    @JacksonXmlProperty(localName = "facadeWaylineEnable", namespace = WpmlNamespaces.WPML)
    Integer facadeWaylineEnable,
    @JacksonXmlProperty(localName = "Polygon", namespace = WpmlNamespaces.KML)
    Polygon polygon,
    @JacksonXmlProperty(localName = "mappingHeadingParam", namespace = WpmlNamespaces.WPML)
    MappingHeadingParam mappingHeadingParam,
    @JacksonXmlProperty(localName = "gimbalPitchMode", namespace = WpmlNamespaces.WPML)
    String gimbalPitchMode,
    @JacksonXmlProperty(localName = "gimbalPitchAngle", namespace = WpmlNamespaces.WPML)
    Integer gimbalPitchAngle
) {}
