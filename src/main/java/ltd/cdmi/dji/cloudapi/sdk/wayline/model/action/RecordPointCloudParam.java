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
 * WPML {@code actionActuatorFuncParam}（recordPointCloud）参数。
 *
 * <p>点云录制动作参数，定义负载位置与点云操作类型。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code payloadPositionIndex} — 负载挂载位置</li>
 *   <li>{@code recordPointCloudOperate} — 点云操作类型，取值：
 *     <ul>
 *       <li>{@code startRecord} — 开始点云录制</li>
 *       <li>{@code pauseRecord} — 暂停点云录制</li>
 *       <li>{@code resumeRecord} — 继续点云录制</li>
 *       <li>{@code stopRecord} — 结束点云录制</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>支持机型：M300 RTK、M350 RTK。
 *
 * @see ActionActuatorFuncParam
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 recordPointCloud actionActuatorFuncParam 参数定义")
@JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)
public record RecordPointCloudParam(
    @JacksonXmlProperty(localName = "payloadPositionIndex", namespace = WpmlNamespaces.WPML)
    int payloadPositionIndex,
    @JacksonXmlProperty(localName = "recordPointCloudOperate", namespace = WpmlNamespaces.WPML)
    String recordPointCloudOperate
) implements ActionActuatorFuncParam {}
