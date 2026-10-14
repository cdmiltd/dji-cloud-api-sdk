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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * WPML waylines.wpml 根元素 {@code <kml>}。
 *
 * <p>DJI 文档「文件介绍」明确倾斜摄影模板将生成 5 条可执行航线，对应 waylines.wpml
 * 内的 5 个 {@code <Folder>} 元素，故 waylines.wpml 的 {@code <Document>} 可包含
 * 多个 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder}。
 * 本类型以 {@link WaylinesDocument} 承载多 Folder，替代早期单数 {@code Kml<ExecuteFolder>}
 * 的建模（§5-70 方案 B）。
 *
 * @param document waylines.wpml 文档元素
 *
 * @see WaylinesDocument
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 kml 根元素定义（Document 可含多个 Folder）")
@JacksonXmlRootElement(localName = "kml", namespace = WpmlNamespaces.KML)
public record WaylinesWpml(
    @JacksonXmlProperty(localName = "Document", namespace = WpmlNamespaces.KML)
    WaylinesDocument document
) {}
