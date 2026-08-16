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
 * WPML {@code actionActuatorFuncParam}（orientedShoot）参数。
 *
 * <p>定向拍照动作参数，用于精准复拍。定义云台姿态、对焦区域、变焦、飞行器偏航、
 * 目标框选信息、负载配置以及参考照片元数据等完整参数。
 *
 * <p>字段顺序依照 DJI WPML 共用元素文档 orientedShoot 定义。
 *
 * <p>支持机型：M30/M30T、M3E/M3T、M3D/M3TD。
 *
 * <p>注：M3E/M3T、M3D/M3TD 机型 {@code gimbalYawRotateAngle} 与 {@code aircraftHeading}
 * 需保持一致。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 orientedShoot actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record OrientedShootParam(
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
    @JacksonXmlProperty(localName = "actionUUID", namespace = WpmlNamespaces.WPML)
    String actionUUID,
    @JacksonXmlProperty(localName = "imageWidth", namespace = WpmlNamespaces.WPML)
    int imageWidth,
    @JacksonXmlProperty(localName = "imageHeight", namespace = WpmlNamespaces.WPML)
    int imageHeight,
    @JacksonXmlProperty(localName = "AFPos", namespace = WpmlNamespaces.WPML)
    int AFPos,
    @JacksonXmlProperty(localName = "gimbalPort", namespace = WpmlNamespaces.WPML)
    int gimbalPort,
    @JacksonXmlProperty(localName = "orientedCameraType", namespace = WpmlNamespaces.WPML)
    int orientedCameraType,
    @JacksonXmlProperty(localName = "orientedFilePath", namespace = WpmlNamespaces.WPML)
    String orientedFilePath,
    @JacksonXmlProperty(localName = "orientedFileMD5", namespace = WpmlNamespaces.WPML)
    String orientedFileMD5,
    @JacksonXmlProperty(localName = "orientedFileSize", namespace = WpmlNamespaces.WPML)
    int orientedFileSize,
    @JacksonXmlProperty(localName = "orientedFileSuffix", namespace = WpmlNamespaces.WPML)
    String orientedFileSuffix,
    @JacksonXmlProperty(localName = "orientedCameraApertue", namespace = WpmlNamespaces.WPML)
    int orientedCameraApertue,
    @JacksonXmlProperty(localName = "orientedCameraLuminance", namespace = WpmlNamespaces.WPML)
    int orientedCameraLuminance,
    @JacksonXmlProperty(localName = "orientedCameraShutterTime", namespace = WpmlNamespaces.WPML)
    double orientedCameraShutterTime,
    @JacksonXmlProperty(localName = "orientedCameraISO", namespace = WpmlNamespaces.WPML)
    int orientedCameraISO,
    @JacksonXmlProperty(localName = "orientedPhotoMode", namespace = WpmlNamespaces.WPML)
    String orientedPhotoMode
) implements ActionActuatorFuncParam {}
