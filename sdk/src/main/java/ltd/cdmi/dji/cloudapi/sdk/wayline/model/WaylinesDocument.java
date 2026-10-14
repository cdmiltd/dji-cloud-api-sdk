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
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;

/**
 * WPML waylines.wpml {@code <Document>} 元素（支持多 Folder）。
 *
 * <p>包含航线文件元数据（author / createTime / updateTime）、任务全局配置
 * {@link MissionConfig} 以及可执行航线 {@link ExecuteFolder} 列表。
 *
 * <p>与 template.kml 侧 {@link Document}{@code <T>} 的区别：waylines.wpml 的 Document
 * 可含多个 Folder（倾斜摄影模板 5 条航线 → 5 个 Folder），故此处以 {@code List} 建模。
 *
 * @param author        文件创建作者
 * @param createTime    文件创建时间（Unix Timestamp，毫秒）
 * @param updateTime    文件更新时间（Unix Timestamp，毫秒）
 * @param missionConfig 任务全局配置
 * @param folders       可执行航线列表（0..N 个 Folder）
 *
 * @see WaylinesWpml
 * @see ExecuteFolder
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 Document 元素定义（可含多个 Folder）")
public record WaylinesDocument(
    @JacksonXmlProperty(localName = "author", namespace = WpmlNamespaces.WPML)
    String author,
    @JacksonXmlProperty(localName = "createTime", namespace = WpmlNamespaces.WPML)
    Long createTime,
    @JacksonXmlProperty(localName = "updateTime", namespace = WpmlNamespaces.WPML)
    Long updateTime,
    @JacksonXmlProperty(localName = "missionConfig", namespace = WpmlNamespaces.WPML)
    MissionConfig missionConfig,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Folder", namespace = WpmlNamespaces.KML)
    List<ExecuteFolder> folders
) {}
