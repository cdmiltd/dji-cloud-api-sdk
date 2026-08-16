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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * WPML 命名空间常量。
 *
 * <p>定义 DJI WPML（Waypoint Markup Language）template.kml 中使用的 XML 命名空间，
 * 供本包内所有 record 的 {@code @JacksonXmlProperty(namespace = ...)} 引用。
 *
 * <ul>
 *   <li>{@link #KML} — OGC KML 2.2 命名空间</li>
 *   <li>{@link #WPML} — DJI WPML 1.0.2 命名空间</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档命名空间声明")
public final class WpmlNamespaces {

    /** OGC KML 2.2 命名空间 */
    public static final String KML = "http://www.opengis.net/kml/2.2";

    /** DJI WPML 1.0.2 命名空间 */
    public static final String WPML = "http://www.dji.com/wpmz/1.0.2";

    private WpmlNamespaces() {}
}
