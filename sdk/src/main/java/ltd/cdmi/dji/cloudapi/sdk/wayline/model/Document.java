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
 * WPML template.kml {@code <Document>} 元素。
 *
 * <p>包含航线模板的元数据（author / createTime / updateTime）、任务全局配置
 * {@link MissionConfig} 以及模板内容 {@code folder}。
 *
 * <p>泛型参数 {@code T} 为 Folder 的具体类型，支持航点飞行（{@link Folder}）
 * 和建图航拍/倾斜摄影/航带飞行（{@code MappingFolder<?>}）等不同模板复用。
 *
 * @param author        文件创建作者
 * @param createTime    文件创建时间（Unix Timestamp，毫秒）
 * @param updateTime    文件更新时间（Unix Timestamp，毫秒）
 * @param missionConfig 任务全局配置
 * @param folder        模板内容（Folder 的具体类型由模板类型决定）
 * @param <T>           Folder 的具体类型
 *
 * @see MissionConfig
 * @see Folder
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 Document 元素定义")
public record Document<T>(
    @JacksonXmlProperty(localName = "author", namespace = WpmlNamespaces.WPML)
    String author,
    @JacksonXmlProperty(localName = "createTime", namespace = WpmlNamespaces.WPML)
    Long createTime,
    @JacksonXmlProperty(localName = "updateTime", namespace = WpmlNamespaces.WPML)
    Long updateTime,
    @JacksonXmlProperty(localName = "missionConfig", namespace = WpmlNamespaces.WPML)
    MissionConfig missionConfig,
    @JacksonXmlProperty(localName = "Folder", namespace = WpmlNamespaces.KML)
    T folder
) {}
