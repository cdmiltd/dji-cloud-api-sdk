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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.Point;

/**
 * WPML template.kml {@code <Placemark>} 元素。
 *
 * <p>表示一个航点，包含 KML {@link Point} 坐标、航点索引、椭球高度、
 * 相对高度、全局参数使用标志、航点级参数（speed/headingParam/turnParam，
 * 当对应 useGlobalXxx=0 时生效）、云台俯仰角以及动作组列表 {@link ActionGroup}。
 *
 * <p>{@code actionGroups} 可为 {@code null}（无动作的航点）。
 *
 * @see Point
 * @see ActionGroup
 * @see WaypointHeadingParam
 * @see WaypointTurnParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 Placemark 元素定义")
public record Placemark(
    @JacksonXmlProperty(localName = "Point", namespace = WpmlNamespaces.KML)
    Point point,
    @JacksonXmlProperty(localName = "index", namespace = WpmlNamespaces.WPML)
    Integer index,
    @JacksonXmlProperty(localName = "ellipsoidHeight", namespace = WpmlNamespaces.WPML)
    Double ellipsoidHeight,
    @JacksonXmlProperty(localName = "height", namespace = WpmlNamespaces.WPML)
    Double height,
    @JacksonXmlProperty(localName = "useGlobalHeight", namespace = WpmlNamespaces.WPML)
    Integer useGlobalHeight,
    @JacksonXmlProperty(localName = "useGlobalSpeed", namespace = WpmlNamespaces.WPML)
    Integer useGlobalSpeed,
    @JacksonXmlProperty(localName = "useGlobalHeadingParam", namespace = WpmlNamespaces.WPML)
    Integer useGlobalHeadingParam,
    @JacksonXmlProperty(localName = "useGlobalTurnParam", namespace = WpmlNamespaces.WPML)
    Integer useGlobalTurnParam,
    @JacksonXmlProperty(localName = "waypointSpeed", namespace = WpmlNamespaces.WPML)
    Double waypointSpeed,
    @JacksonXmlProperty(localName = "waypointHeadingParam", namespace = WpmlNamespaces.WPML)
    WaypointHeadingParam waypointHeadingParam,
    @JacksonXmlProperty(localName = "waypointTurnParam", namespace = WpmlNamespaces.WPML)
    WaypointTurnParam waypointTurnParam,
    @JacksonXmlProperty(localName = "gimbalPitchAngle", namespace = WpmlNamespaces.WPML)
    Double gimbalPitchAngle,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "actionGroup", namespace = WpmlNamespaces.WPML)
    List<ActionGroup> actionGroups
) {}
