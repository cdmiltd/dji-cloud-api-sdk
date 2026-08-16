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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML waylines.wpml {@code <Folder>} 元素（可执行航线）。
 *
 * <p>由 template.kml 的 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.Folder}
 * 转换而来，移除模板类型与全局参数，新增 {@code executeHeightMode} 和 {@code waylineId}，
 * 全局参数已展开到各航点。
 *
 * @see ExecutePlacemark
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 Folder 元素定义")
public record ExecuteFolder(
    @JacksonXmlProperty(localName = "templateId", namespace = WpmlNamespaces.WPML)
    Integer templateId,
    @JacksonXmlProperty(localName = "executeHeightMode", namespace = WpmlNamespaces.WPML)
    String executeHeightMode,
    @JacksonXmlProperty(localName = "waylineId", namespace = WpmlNamespaces.WPML)
    Integer waylineId,
    @JacksonXmlProperty(localName = "autoFlightSpeed", namespace = WpmlNamespaces.WPML)
    Double autoFlightSpeed,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Placemark", namespace = WpmlNamespaces.KML)
    List<ExecutePlacemark> placemarks
) {}
