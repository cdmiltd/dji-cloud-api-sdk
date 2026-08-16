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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * KML {@code <Point>} 元素。
 *
 * <p>航点坐标，{@code coordinates} 遵循 KML 标准 {@code "longitude,latitude"} 格式，
 * 不含高度（高度由 WPML 的 {@code ellipsoidHeight} / {@code height} 单独定义）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 Point 元素定义")
public record Point(
    @JacksonXmlProperty(localName = "coordinates", namespace = WpmlNamespaces.KML)
    String coordinates
) {}
