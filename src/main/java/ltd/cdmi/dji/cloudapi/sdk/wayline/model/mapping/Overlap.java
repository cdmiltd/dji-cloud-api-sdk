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

/**
 * WPML {@code <wpml:overlap>} 元素。
 *
 * <p>重叠率参数，定义激光/可见光在航向/旁向的正射/倾斜重叠率。
 * 用于建图航拍、倾斜摄影、航带飞行模板。
 *
 * <p>所有字段为百分比整型，范围 [0, 100]。
 *
 * @see Mapping2dPlacemark
 * @see Mapping3dPlacemark
 * @see MappingStripPlacemark
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 overlap 元素定义")
public record Overlap(
    @JacksonXmlProperty(localName = "orthoLidarOverlapH", namespace = WpmlNamespaces.WPML)
    Integer orthoLidarOverlapH,
    @JacksonXmlProperty(localName = "orthoLidarOverlapW", namespace = WpmlNamespaces.WPML)
    Integer orthoLidarOverlapW,
    @JacksonXmlProperty(localName = "orthoCameraOverlapH", namespace = WpmlNamespaces.WPML)
    Integer orthoCameraOverlapH,
    @JacksonXmlProperty(localName = "orthoCameraOverlapW", namespace = WpmlNamespaces.WPML)
    Integer orthoCameraOverlapW,
    @JacksonXmlProperty(localName = "inclinedLidarOverlapH", namespace = WpmlNamespaces.WPML)
    Integer inclinedLidarOverlapH,
    @JacksonXmlProperty(localName = "inclinedLidarOverlapW", namespace = WpmlNamespaces.WPML)
    Integer inclinedLidarOverlapW,
    @JacksonXmlProperty(localName = "inclinedCameraOverlapH", namespace = WpmlNamespaces.WPML)
    Integer inclinedCameraOverlapH,
    @JacksonXmlProperty(localName = "inclinedCameraOverlapW", namespace = WpmlNamespaces.WPML)
    Integer inclinedCameraOverlapW
) {}
