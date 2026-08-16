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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.LineString;

/**
 * WPML template.kml {@code <Placemark>} 元素（航带飞行模板 mappingStrip）。
 *
 * <p>航带飞行配置，包含标定飞行、拍照模式、航线方向、外扩距离、
 * 单航线飞行、子航带长度、边缘优化、左右外扩、中心线、重叠率、
 * 全局高度、变高航带以及航带线 {@link LineString}。
 *
 * <p>字段顺序依照 DJI WPML template.kml 文档航带飞行模板元素定义。
 *
 * @see LineString
 * @see Overlap
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档航带飞行模板元素定义")
public record MappingStripPlacemark(
    @JacksonXmlProperty(localName = "caliFlightEnable", namespace = WpmlNamespaces.WPML)
    Integer caliFlightEnable,
    @JacksonXmlProperty(localName = "shootType", namespace = WpmlNamespaces.WPML)
    String shootType,
    @JacksonXmlProperty(localName = "direction", namespace = WpmlNamespaces.WPML)
    int direction,
    @JacksonXmlProperty(localName = "margin", namespace = WpmlNamespaces.WPML)
    int margin,
    @JacksonXmlProperty(localName = "singleLineEnable", namespace = WpmlNamespaces.WPML)
    int singleLineEnable,
    @JacksonXmlProperty(localName = "cuttingDistance", namespace = WpmlNamespaces.WPML)
    double cuttingDistance,
    @JacksonXmlProperty(localName = "boundaryOptimEnable", namespace = WpmlNamespaces.WPML)
    int boundaryOptimEnable,
    @JacksonXmlProperty(localName = "leftExtend", namespace = WpmlNamespaces.WPML)
    int leftExtend,
    @JacksonXmlProperty(localName = "rightExtend", namespace = WpmlNamespaces.WPML)
    int rightExtend,
    @JacksonXmlProperty(localName = "includeCenterEnable", namespace = WpmlNamespaces.WPML)
    int includeCenterEnable,
    @JacksonXmlProperty(localName = "overlap", namespace = WpmlNamespaces.WPML)
    Overlap overlap,
    @JacksonXmlProperty(localName = "ellipsoidHeight", namespace = WpmlNamespaces.WPML)
    double ellipsoidHeight,
    @JacksonXmlProperty(localName = "height", namespace = WpmlNamespaces.WPML)
    double height,
    @JacksonXmlProperty(localName = "stripUseTemplateAltitude", namespace = WpmlNamespaces.WPML)
    int stripUseTemplateAltitude,
    @JacksonXmlProperty(localName = "LineString", namespace = WpmlNamespaces.KML)
    LineString lineString
) {}
