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
 * WPML（Waypoint Mission Markup Language）航线模板枚举值定义包。
 *
 * <p>本包提供 DJI WPML {@code template.kml} 航线模板生成工具所需的全部枚举值类型。
 * WPML 是 DJI 定义的航线任务标记语言，用于描述航线任务的结构、航点属性、动作触发等内容，
 * 由 {@code Template} 节点（航线模板配置）与 {@code Placemark} 节点（航点配置）共同组成。
 *
 * <p>WPML 枚举值均为字符串（非整数），每个枚举类型提供 {@code code()}（字符串码）、
 * {@code description()}（中文描述）与 {@code fromCode(String)}（反查）方法，
 * 便于在序列化 / 反序列化 KML 时与 WPML 文本协议对齐。
 *
 * <p>枚举按所属 WPML 文档分两类：
 * <ul>
 *   <li>template-kml 类：{@link FlyToWaylineMode}、{@link FinishAction}、{@link ExitOnRCLost}、
 *       {@link ExecuteRCLostAction}、{@link CoordinateMode}、{@link HeightMode}、
 *       {@link PositioningType}、{@link GimbalPitchMode}</li>
 *   <li>common-element 类：{@link WaypointHeadingMode}、{@link WaypointHeadingPathMode}、
 *       {@link WaypointTurnMode}、{@link ActionGroupMode}、{@link ActionTriggerType}、
 *       {@link ActionActuatorFunc}、{@link GimbalRotateMode}</li>
 * </ul>
 *
 * <p>参考 DJI 官方文档：
 * <ul>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html">template-kml 文档</a></li>
 *   <li><a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html">common-element 文档</a></li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl
 * @see ltd.cdmi.dji.cloudapi.sdk.annotation.Verified
 * @see ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred
 */
package ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype;
