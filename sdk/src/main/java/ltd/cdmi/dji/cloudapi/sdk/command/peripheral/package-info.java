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
 * peripheral 外设信息 POJO（O4 地面站）。
 *
 * <p>本包含 DJI O4 地面站 state 上报的外设接入登记与能力集 record：
 * <ul>
 *   <li>{@link PeripheralInfo} — peripherals_info 数组元素（外设接入登记）</li>
 *   <li>{@link PeripheralCapability} — peripheral_list 数组元素（外设能力集）</li>
 *   <li>{@link RidCapability} / {@link AdsbCapability} / {@link DockControlCapability} — 能力明细</li>
 * </ul>
 *
 * <p>DJI 文档：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html
 */
package ltd.cdmi.dji.cloudapi.sdk.command.peripheral;
