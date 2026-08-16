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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML template.kml {@code <Folder>} 元素（建图类模板通用）。
 *
 * <p>建图航拍（mapping2d）、倾斜摄影（mapping3d）、航带飞行（mappingStrip）
 * 三种模板共享的 Folder 结构，包含模板类型/ID、飞行速度、坐标系参数、
 * 负载设置以及单个 {@code Placemark}（测区/航带配置）。
 *
 * <p>与航点飞行模板 {@link Folder} 的差异：无全局航向/转弯参数，
 * 增加 {@code payloadParam}，Placemark 为单个而非列表。
 *
 * @param <P> Placemark 的具体类型（如 {@link Mapping2dPlacemark}）
 *
 * @see PayloadParam
 * @see WaylineCoordinateSysParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 Folder 元素定义（建图类模板）")
public record MappingFolder<P>(
    @JacksonXmlProperty(localName = "templateType", namespace = WpmlNamespaces.WPML)
    String templateType,
    @JacksonXmlProperty(localName = "templateId", namespace = WpmlNamespaces.WPML)
    Integer templateId,
    @JacksonXmlProperty(localName = "autoFlightSpeed", namespace = WpmlNamespaces.WPML)
    Double autoFlightSpeed,
    @JacksonXmlProperty(localName = "waylineCoordinateSysParam", namespace = WpmlNamespaces.WPML)
    WaylineCoordinateSysParam waylineCoordinateSysParam,
    @JacksonXmlProperty(localName = "payloadParam", namespace = WpmlNamespaces.WPML)
    PayloadParam payloadParam,
    @JacksonXmlProperty(localName = "Placemark", namespace = WpmlNamespaces.KML)
    P placemark
) {}
