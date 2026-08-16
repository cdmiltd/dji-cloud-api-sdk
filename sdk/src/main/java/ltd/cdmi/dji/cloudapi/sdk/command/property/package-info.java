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
 * property/set 通道封装（cloud-to-device 属性设置）。
 *
 * <p>DJI Cloud API 的 property/set 通道用于设置设备属性，与 services 通道不同：
 * <ul>
 *   <li>不使用 method 字段，直接在 data 中放置属性名→值的映射</li>
 *   <li>property/set_reply 返回每个属性的设置结果（code: 0=成功）</li>
 * </ul>
 *
 * <p>可设置属性见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.PropertySetMethod}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.property;
