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
 * WPML {@code actionActuatorFuncParam}（gimbalRotate）参数。
 *
 * <p>云台旋转动作参数，定义负载位置、偏航角转动坐标系（{@code gimbalHeadingYawBase}）、
 * 旋转模式（{@code gimbalRotateMode} 为 {@code String}，由 GimbalRotateMode 枚举的
 * {@code code()} 赋值）、三轴旋转使能与角度、旋转时间使能与时长。
 *
 * <p>字段顺序依照 DJI WPML 共用元素文档 gimbalRotate 定义。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 gimbalRotate actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record GimbalRotateParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "gimbalHeadingYawBase", namespace = WpmlNamespaces.WPML)
    String gimbalHeadingYawBase,
    @JacksonXmlProperty(localName = "gimbalRotateMode", namespace = WpmlNamespaces.WPML)
    String gimbalRotateMode,
    @JacksonXmlProperty(localName = "gimbalPitchRotateEnable", namespace = WpmlNamespaces.WPML)
    int gimbalPitchRotateEnable,
    @JacksonXmlProperty(localName = "gimbalPitchRotateAngle", namespace = WpmlNamespaces.WPML)
    double gimbalPitchRotateAngle,
    @JacksonXmlProperty(localName = "gimbalRollRotateEnable", namespace = WpmlNamespaces.WPML)
    int gimbalRollRotateEnable,
    @JacksonXmlProperty(localName = "gimbalRollRotateAngle", namespace = WpmlNamespaces.WPML)
    double gimbalRollRotateAngle,
    @JacksonXmlProperty(localName = "gimbalYawRotateEnable", namespace = WpmlNamespaces.WPML)
    int gimbalYawRotateEnable,
    @JacksonXmlProperty(localName = "gimbalYawRotateAngle", namespace = WpmlNamespaces.WPML)
    double gimbalYawRotateAngle,
    @JacksonXmlProperty(localName = "gimbalRotateTimeEnable", namespace = WpmlNamespaces.WPML)
    int gimbalRotateTimeEnable,
    @JacksonXmlProperty(localName = "gimbalRotateTime", namespace = WpmlNamespaces.WPML)
    double gimbalRotateTime
) implements ActionActuatorFuncParam {}
