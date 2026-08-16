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
 * WPML template.kml {@code <Placemark>} 元素（倾斜摄影模板 mapping3d）。
 *
 * <p>倾斜摄影测区配置，包含标定飞行、倾斜云台俯仰角、倾斜飞行速度、
 * 拍照模式、航线方向、外扩距离、重叠率、全局高度以及测区多边形 {@link Polygon}。
 *
 * <p>倾斜摄影模板会被生成五条航线，其中 1 条采集正射影像，4 条采集倾斜影像。
 * {@code inclinedGimbalPitch} 和 {@code inclinedFlightSpeed} 用于设置倾斜影像
 * 采集时的云台俯仰角度和飞行速度。
 *
 * <p>字段顺序依照 DJI WPML template.kml 文档倾斜摄影模板元素定义。
 *
 * @see Polygon
 * @see Overlap
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档倾斜摄影模板元素定义")
public record Mapping3dPlacemark(
    @JacksonXmlProperty(localName = "caliFlightEnable", namespace = WpmlNamespaces.WPML)
    Integer caliFlightEnable,
    @JacksonXmlProperty(localName = "inclinedGimbalPitch", namespace = WpmlNamespaces.WPML)
    int inclinedGimbalPitch,
    @JacksonXmlProperty(localName = "inclinedFlightSpeed", namespace = WpmlNamespaces.WPML)
    double inclinedFlightSpeed,
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
    @JacksonXmlProperty(localName = "Polygon", namespace = WpmlNamespaces.KML)
    Polygon polygon
) {}
