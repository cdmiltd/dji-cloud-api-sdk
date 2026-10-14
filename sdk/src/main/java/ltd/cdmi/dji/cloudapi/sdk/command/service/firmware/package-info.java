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
 * 固件升级 services 指令 POJO。
 *
 * <p>本包含 2 个 POJO：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.firmware.OtaCreateRequest OtaCreateRequest}
 *       — ota_create 请求（devices[] 结构，firmware_upgrade_type 类型化枚举
 *       {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.FirmwareUpgradeType}）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.firmware.OtaCreateReply OtaCreateReply}
 *       — ota_create 回复（{result, output{status}}）</li>
 * </ul>
 *
 * <p>数据来源依据为 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/OtaSimulator.java">
 * OtaSimulator</a> 已对接 hivemind 验证的字段集，并核对 DJI O4 地面站 firmware.html。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.firmware;
