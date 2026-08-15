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
 * WPML {@code <wpml:globalWaypointHeadingParam>} 元素。
 *
 * <p>全局航点航向参数，定义航点航向模式、航向角度、兴趣点坐标以及
 * 航向路径模式。
 *
 * <p>类型安全保障：枚举字段（{@code waypointHeadingMode} / {@code waypointHeadingPathMode}）
 * 使用 {@code String} 承载以兼容 XML 序列化，类型安全由 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate}
 * Builder 层提供——Builder 字段为 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode}
 * 和 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode} 枚举，
 * 通过 {@code codeOf()} 转换为 {@code String} 存入本 record。与 {@link PayloadParam} 的模式一致。
 *
 * <p>范围校验：{@code waypointHeadingAngle} 限制为 [-180, 180]，
 * {@code waypointPoiPoint} 的纬度/经度范围校验均在 Builder 方法中实现。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 globalWaypointHeadingParam 元素定义")
public record GlobalWaypointHeadingParam(
    @JacksonXmlProperty(localName = "waypointHeadingMode", namespace = WpmlNamespaces.WPML)
    String waypointHeadingMode,
    @JacksonXmlProperty(localName = "waypointHeadingAngle", namespace = WpmlNamespaces.WPML)
    Double waypointHeadingAngle,
    @JacksonXmlProperty(localName = "waypointPoiPoint", namespace = WpmlNamespaces.WPML)
    String waypointPoiPoint,
    @JacksonXmlProperty(localName = "waypointHeadingPathMode", namespace = WpmlNamespaces.WPML)
    String waypointHeadingPathMode
) {}
