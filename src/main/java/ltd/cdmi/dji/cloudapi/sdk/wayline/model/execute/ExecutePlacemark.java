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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ActionGroup;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointHeadingParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointTurnParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.Point;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML waylines.wpml {@code <Placemark>} 元素（可执行航点）。
 *
 * <p>由 template.kml 的 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.Placemark}
 * 转换而来：{@code height}→{@code executeHeight}，移除 {@code useGlobalXxx} 标志和
 * {@code ellipsoidHeight}，展开全局参数为 {@code waypointSpeed} /
 * {@code waypointHeadingParam} / {@code waypointTurnParam}。
 *
 * @see WaypointHeadingParam
 * @see WaypointTurnParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 Placemark 元素定义")
public record ExecutePlacemark(
    @JacksonXmlProperty(localName = "Point", namespace = WpmlNamespaces.KML)
    Point point,
    @JacksonXmlProperty(localName = "index", namespace = WpmlNamespaces.WPML)
    Integer index,
    @JacksonXmlProperty(localName = "executeHeight", namespace = WpmlNamespaces.WPML)
    Double executeHeight,
    @JacksonXmlProperty(localName = "waypointSpeed", namespace = WpmlNamespaces.WPML)
    Double waypointSpeed,
    @JacksonXmlProperty(localName = "gimbalPitchAngle", namespace = WpmlNamespaces.WPML)
    Double gimbalPitchAngle,
    @JacksonXmlProperty(localName = "waypointHeadingParam", namespace = WpmlNamespaces.WPML)
    WaypointHeadingParam waypointHeadingParam,
    @JacksonXmlProperty(localName = "waypointTurnParam", namespace = WpmlNamespaces.WPML)
    WaypointTurnParam waypointTurnParam,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "actionGroup", namespace = WpmlNamespaces.WPML)
    List<ActionGroup> actionGroups
) {}
