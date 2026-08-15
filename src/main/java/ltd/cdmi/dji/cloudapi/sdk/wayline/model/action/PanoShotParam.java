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
 * WPML {@code actionActuatorFuncParam}（panoShot）参数。
 *
 * <p>全景拍照动作参数，定义负载位置、拍摄照片存储类型、是否使用全局存储类型
 * 以及全景拍照子模式。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code payloadLensIndex} — 拍摄照片存储类型，枚举-string 列表。
 *       取值：{@code zoom}/{@code wide}/{@code ir}/{@code narrow_band}/{@code visable}。
 *       多镜头格式如 {@code "wide,ir,narrow_band"}</li>
 *   <li>{@code useGlobalPayloadLensIndex} — 是否使用全局存储类型（0=不使用, 1=使用）</li>
 *   <li>{@code panoShotSubMode} — 全景拍照模式，当前仅支持 {@code panoShot_360}（全景模式）</li>
 * </ul>
 *
 * <p>支持机型：M30/M30T、M3D/M3TD。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 panoShot actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record PanoShotParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "payloadLensIndex", namespace = WpmlNamespaces.WPML)
    String payloadLensIndex,
    @JacksonXmlProperty(localName = "useGlobalPayloadLensIndex", namespace = WpmlNamespaces.WPML)
    int useGlobalPayloadLensIndex,
    @JacksonXmlProperty(localName = "panoShotSubMode", namespace = WpmlNamespaces.WPML)
    String panoShotSubMode
) implements ActionActuatorFuncParam {}
