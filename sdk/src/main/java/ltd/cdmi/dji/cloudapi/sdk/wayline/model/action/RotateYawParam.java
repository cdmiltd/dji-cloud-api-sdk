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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.action;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * WPML {@code actionActuatorFuncParam}（rotateYaw）参数。
 *
 * <p>飞行器偏航旋转动作参数，定义目标偏航角（相对于地理北）与转动方向。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code aircraftHeading} — 飞行器目标偏航角（°），范围 [-180, 180]。
 *       0°为正北，90°为正东，-90°为正西，-180°/180°为正南</li>
 *   <li>{@code aircraftPathMode} — 偏航角转动模式，取值 {@code clockwise}（顺时针）
 *       或 {@code counterClockwise}（逆时针）</li>
 * </ul>
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 rotateYaw actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record RotateYawParam(
    @JacksonXmlProperty(localName = "aircraftHeading", namespace = WpmlNamespaces.WPML)
    double aircraftHeading,
    @JacksonXmlProperty(localName = "aircraftPathMode", namespace = WpmlNamespaces.WPML)
    String aircraftPathMode
) implements ActionActuatorFuncParam {}
