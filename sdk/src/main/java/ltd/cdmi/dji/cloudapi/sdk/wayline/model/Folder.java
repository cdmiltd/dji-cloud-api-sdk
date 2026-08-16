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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.WaylineCoordinateSysParam;

/**
 * WPML template.kml {@code <Folder>} 元素。
 *
 * <p>航线模板核心配置容器，包含模板类型/ID、坐标系参数、全局飞行参数
 * （速度/高度/转向/航向）以及航点列表 {@link Placemark}。
 *
 * @see WaylineCoordinateSysParam
 * @see GlobalWaypointHeadingParam
 * @see Placemark
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 Folder 元素定义")
public record Folder(
    @JacksonXmlProperty(localName = "templateType", namespace = WpmlNamespaces.WPML)
    String templateType,
    @JacksonXmlProperty(localName = "templateId", namespace = WpmlNamespaces.WPML)
    Integer templateId,
    @JacksonXmlProperty(localName = "waylineCoordinateSysParam", namespace = WpmlNamespaces.WPML)
    WaylineCoordinateSysParam waylineCoordinateSysParam,
    @JacksonXmlProperty(localName = "autoFlightSpeed", namespace = WpmlNamespaces.WPML)
    Double autoFlightSpeed,
    @JacksonXmlProperty(localName = "gimbalPitchMode", namespace = WpmlNamespaces.WPML)
    String gimbalPitchMode,
    @JacksonXmlProperty(localName = "globalHeight", namespace = WpmlNamespaces.WPML)
    Double globalHeight,
    @JacksonXmlProperty(localName = "globalWaypointHeadingParam", namespace = WpmlNamespaces.WPML)
    GlobalWaypointHeadingParam globalWaypointHeadingParam,
    @JacksonXmlProperty(localName = "globalWaypointTurnMode", namespace = WpmlNamespaces.WPML)
    String globalWaypointTurnMode,
    @JacksonXmlProperty(localName = "globalUseStraightLine", namespace = WpmlNamespaces.WPML)
    Integer globalUseStraightLine,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Placemark", namespace = WpmlNamespaces.KML)
    List<Placemark> placemarks
) {}
