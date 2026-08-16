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
 * WPML {@code <wpml:waypointHeadingParam>} 元素（航点级航向参数）。
 *
 * <p>共享类型：在 template.kml 中作为 per-waypoint 参数（useGlobalHeadingParam=0 时），
 * 在 waylines.wpml 中作为展开后的航点航向参数。
 *
 * <p>字段集与 {@link GlobalWaypointHeadingParam} 一致。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waypointHeadingParam 元素定义")
public record WaypointHeadingParam(
    @JacksonXmlProperty(localName = "waypointHeadingMode", namespace = WpmlNamespaces.WPML)
    String waypointHeadingMode,
    @JacksonXmlProperty(localName = "waypointHeadingAngle", namespace = WpmlNamespaces.WPML)
    Double waypointHeadingAngle,
    @JacksonXmlProperty(localName = "waypointPoiPoint", namespace = WpmlNamespaces.WPML)
    String waypointPoiPoint,
    @JacksonXmlProperty(localName = "waypointHeadingPathMode", namespace = WpmlNamespaces.WPML)
    String waypointHeadingPathMode
) {}
