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
 * KML {@code <Polygon>} 元素。
 *
 * <p>测区多边形，包含外边界 {@link LinearRing}。坐标格式为
 * {@code "经度,纬度,高度 经度,纬度,高度 ..."}。
 * 用于建图航拍（mapping2d）和倾斜摄影（mapping3d）模板的测区定义。
 *
 * @see LinearRing
 */
public record Polygon(
    @JacksonXmlProperty(localName = "outerBoundaryIs", namespace = WpmlNamespaces.KML)
    OuterBoundaryIs outerBoundaryIs
) {
    /**
     * KML {@code <outerBoundaryIs>} 元素，包含 {@link LinearRing}。
     */
    public record OuterBoundaryIs(
        @JacksonXmlProperty(localName = "LinearRing", namespace = WpmlNamespaces.KML)
        LinearRing linearRing
    ) {}
}
