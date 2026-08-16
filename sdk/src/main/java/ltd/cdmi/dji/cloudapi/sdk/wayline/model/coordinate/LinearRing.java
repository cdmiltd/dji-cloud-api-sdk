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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * KML {@code <LinearRing>} 元素。
 *
 * <p>线环，包含一组坐标点字符串，用于 {@link Polygon} 的外边界。
 * 坐标格式为 {@code "经度,纬度,高度 经度,纬度,高度 ..."}。
 *
 * @see Polygon
 */
public record LinearRing(
    @JacksonXmlProperty(localName = "coordinates", namespace = WpmlNamespaces.KML)
    String coordinates
) {}
