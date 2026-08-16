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
 * WPML {@code actionActuatorFuncParam}（accurateShoot）参数。
 *
 * <p>精准复拍动作参数。DJI 文档注明"已暂停维护，建议使用 orientedShoot 替代"，
 * 但 DJI Pilot 仍可能导出包含 accurateShoot 的 KMZ 文件，故保留独立 POJO 以支持解析。
 *
 * <p>与 {@link OrientedShootParam} 的关键差异：
 * <ul>
 *   <li>字段名前缀为 {@code accurate*}（非 {@code oriented*}）</li>
 *   <li>无 {@code actionUUID} 和 {@code orientedPhotoMode} 字段</li>
 *   <li>{@code accurateCameraType} 枚举值含 H20/H20T/H30/H30T（42/43/82/83），
 *       而 {@code orientedCameraType} 含 M3E/M3T/M3D/M3TD（66/67/80/81）</li>
 *   <li>支持机型为 M300 RTK/M350 RTK/M30/M30T（含 M300/M350，不含 M3E/M3D/M4D）</li>
 * </ul>
 *
 * <p>字段顺序依照 DJI WPML 共用元素文档 accurateShoot 定义。
 *
 * <p>支持机型：M300 RTK、M350 RTK、M30/M30T。
 *
 * @see ActionActuatorFuncParam
 * @see OrientedShootParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 accurateShoot actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record AccurateShootParam(
    @JacksonXmlProperty(localName = "gimbalPitchRotateAngle", namespace = WpmlNamespaces.WPML)
    double gimbalPitchRotateAngle,
    @JacksonXmlProperty(localName = "gimbalYawRotateAngle", namespace = WpmlNamespaces.WPML)
    double gimbalYawRotateAngle,
    @JacksonXmlProperty(localName = "focusX", namespace = WpmlNamespaces.WPML)
    int focusX,
    @JacksonXmlProperty(localName = "focusY", namespace = WpmlNamespaces.WPML)
    int focusY,
    @JacksonXmlProperty(localName = "focusRegionWidth", namespace = WpmlNamespaces.WPML)
    int focusRegionWidth,
    @JacksonXmlProperty(localName = "focusRegionHeight", namespace = WpmlNamespaces.WPML)
    int focusRegionHeight,
    @JacksonXmlProperty(localName = "focalLength", namespace = WpmlNamespaces.WPML)
    double focalLength,
    @JacksonXmlProperty(localName = "aircraftHeading", namespace = WpmlNamespaces.WPML)
    double aircraftHeading,
    @JacksonXmlProperty(localName = "accurateFrameValid", namespace = WpmlNamespaces.WPML)
    int accurateFrameValid,
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "payloadLensIndex", namespace = WpmlNamespaces.WPML)
    String payloadLensIndex,
    @JacksonXmlProperty(localName = "useGlobalPayloadLensIndex", namespace = WpmlNamespaces.WPML)
    int useGlobalPayloadLensIndex,
    @JacksonXmlProperty(localName = "targetAngle", namespace = WpmlNamespaces.WPML)
    double targetAngle,
    @JacksonXmlProperty(localName = "imageWidth", namespace = WpmlNamespaces.WPML)
    int imageWidth,
    @JacksonXmlProperty(localName = "imageHeight", namespace = WpmlNamespaces.WPML)
    int imageHeight,
    @JacksonXmlProperty(localName = "AFPos", namespace = WpmlNamespaces.WPML)
    int AFPos,
    @JacksonXmlProperty(localName = "gimbalPort", namespace = WpmlNamespaces.WPML)
    int gimbalPort,
    @JacksonXmlProperty(localName = "accurateCameraType", namespace = WpmlNamespaces.WPML)
    int accurateCameraType,
    @JacksonXmlProperty(localName = "accurateFilePath", namespace = WpmlNamespaces.WPML)
    String accurateFilePath,
    @JacksonXmlProperty(localName = "accurateFileMD5", namespace = WpmlNamespaces.WPML)
    String accurateFileMD5,
    @JacksonXmlProperty(localName = "accurateFileSize", namespace = WpmlNamespaces.WPML)
    int accurateFileSize,
    @JacksonXmlProperty(localName = "accurateFileSuffix", namespace = WpmlNamespaces.WPML)
    String accurateFileSuffix,
    @JacksonXmlProperty(localName = "accurateCameraApertue", namespace = WpmlNamespaces.WPML)
    int accurateCameraApertue,
    @JacksonXmlProperty(localName = "accurateCameraLuminance", namespace = WpmlNamespaces.WPML)
    int accurateCameraLuminance,
    @JacksonXmlProperty(localName = "accurateCameraShutterTime", namespace = WpmlNamespaces.WPML)
    double accurateCameraShutterTime,
    @JacksonXmlProperty(localName = "accurateCameraISO", namespace = WpmlNamespaces.WPML)
    int accurateCameraISO
) implements ActionActuatorFuncParam {}
