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
 * WPML {@code actionActuatorFuncParam}（megaphone）参数。
 *
 * <p>喊话器动作参数，定义负载位置、动作标识、喊话操作类型、音量、循环播放、
 * 音频文件路径/名称/原名/MD5 以及压缩比特率。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code megaphoneOperateType} — 喊话动作开关，0=开始喊话，1=结束喊话</li>
 *   <li>{@code megaphoneOperateVolume} — 喊话音量，范围 0-100</li>
 *   <li>{@code megaphoneOperateLoop} — 单曲循环播放，0=关闭，1=开启</li>
 *   <li>{@code megaphoneOperateFilePath} — 音频文件在 kmz 中的路径，如 {@code /wpmz/res/audio/xxx.opus}</li>
 *   <li>{@code megaphoneFileBitrate} — 压缩比特率，枚举值 1-6（当前仅支持 4=32000）</li>
 * </ul>
 *
 * <p>支持机型：M4D/M4TD。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 megaphone actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record MegaphoneParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "actionUUID", namespace = WpmlNamespaces.WPML)
    String actionUUID,
    @JacksonXmlProperty(localName = "megaphoneOperateType", namespace = WpmlNamespaces.WPML)
    int megaphoneOperateType,
    @JacksonXmlProperty(localName = "megaphoneOperateVolume", namespace = WpmlNamespaces.WPML)
    int megaphoneOperateVolume,
    @JacksonXmlProperty(localName = "megaphoneOperateLoop", namespace = WpmlNamespaces.WPML)
    int megaphoneOperateLoop,
    @JacksonXmlProperty(localName = "megaphoneOperateFilePath", namespace = WpmlNamespaces.WPML)
    String megaphoneOperateFilePath,
    @JacksonXmlProperty(localName = "megaphoneFileName", namespace = WpmlNamespaces.WPML)
    String megaphoneFileName,
    @JacksonXmlProperty(localName = "megaphoneFileOriginalName", namespace = WpmlNamespaces.WPML)
    String megaphoneFileOriginalName,
    @JacksonXmlProperty(localName = "megaphoneFileMd5", namespace = WpmlNamespaces.WPML)
    String megaphoneFileMd5,
    @JacksonXmlProperty(localName = "megaphoneFileBitrate", namespace = WpmlNamespaces.WPML)
    int megaphoneFileBitrate
) implements ActionActuatorFuncParam {}
