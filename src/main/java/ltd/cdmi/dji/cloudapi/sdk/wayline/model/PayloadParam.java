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
 * WPML {@code <wpml:payloadParam>} 元素。
 *
 * <p>负载设置参数，定义负载挂载位置、对焦模式、测光模式、畸变矫正、
 * 激光雷达回波模式、采样率、扫描模式、真彩上色以及图片格式列表。
 * 用于建图航拍、倾斜摄影、航带飞行模板。
 *
 * @see MappingFolder
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 payloadParam 元素定义")
public record PayloadParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "focusMode", namespace = WpmlNamespaces.WPML)
    String focusMode,
    @JacksonXmlProperty(localName = "meteringMode", namespace = WpmlNamespaces.WPML)
    String meteringMode,
    @JacksonXmlProperty(localName = "dewarpingEnable", namespace = WpmlNamespaces.WPML)
    Integer dewarpingEnable,
    @JacksonXmlProperty(localName = "returnMode", namespace = WpmlNamespaces.WPML)
    String returnMode,
    @JacksonXmlProperty(localName = "samplingRate", namespace = WpmlNamespaces.WPML)
    Integer samplingRate,
    @JacksonXmlProperty(localName = "scanningMode", namespace = WpmlNamespaces.WPML)
    String scanningMode,
    @JacksonXmlProperty(localName = "modelColoringEnable", namespace = WpmlNamespaces.WPML)
    Integer modelColoringEnable,
    @JacksonXmlProperty(localName = "imageFormat", namespace = WpmlNamespaces.WPML)
    String imageFormat
) {}
