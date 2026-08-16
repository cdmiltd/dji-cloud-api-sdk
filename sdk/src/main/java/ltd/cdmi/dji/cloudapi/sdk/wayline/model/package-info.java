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

/**
 * WPML template.kml XML 元素 POJO。
 *
 * <p>本包包含 DJI WPML（Waypoint Markup Language）template.kml 格式的 XML 元素对应的
 * Java POJO（{@code record}），使用 Jackson XML 注解进行序列化，用于生成符合 DJI WPML
 * 规范的航线模板文件。
 *
 * <h2>命名空间</h2>
 * <ul>
 *   <li>KML — {@link WpmlNamespaces#KML}（{@code http://www.opengis.net/kml/2.2}），
 *       涵盖 {@code <kml>}、{@code <Document>}、{@code <Folder>}、{@code <Placemark>}、
 *       {@code <Point>} 等 KML 标准元素</li>
 *   <li>WPML — {@link WpmlNamespaces#WPML}（{@code http://www.dji.com/wpmz/1.0.2}），
 *       涵盖 {@code <missionConfig>}、{@code <droneInfo>}、{@code <payloadInfo>}、
 *       {@code <actionGroup>}、{@code <action>} 等 DJI 扩展元素</li>
 * </ul>
 *
 * <h2>结构层次</h2>
 * <pre>{@code
 * Kml
 * └─ Document
 *    ├─ missionConfig
 *    │   ├─ droneInfo
 *    │   └─ payloadInfo
 *    └─ Folder
 *       ├─ waylineCoordinateSysParam
 *       ├─ globalWaypointHeadingParam
 *       └─ Placemark[]
 *          ├─ Point (coordinates)
 *          └─ actionGroup[]
 *             ├─ actionTrigger
 *             └─ action[]
 *                └─ actionActuatorFuncParam (sealed interface)
 * }</pre>
 *
 * <h2>多态参数</h2>
 * <p>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam} 为密封接口，由 13 个 action param record 实现，
 * 对应不同的 {@code actionActuatorFunc} 取值。每个实现 record 标注
 * {@code @JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)}。
 *
 * <h2>参考文档</h2>
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html">
 *       DJI WPML template.kml</a> — 结构元素定义</li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html">
 *       DJI WPML 共用元素</a> — actionActuatorFuncParam 各动作参数定义</li>
 * </ul>
 *
 * @see WpmlNamespaces
 * @see Kml
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam
 */
package ltd.cdmi.dji.cloudapi.sdk.wayline.model;
