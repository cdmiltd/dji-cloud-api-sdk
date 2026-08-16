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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.up;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_drone_state_push} 推送数据：飞行器状态（mode_code/stealth/night_lights/landing_*）。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L246-L253">
 * DeviceSimulator.publishDrcEvents drc_drone_state_push 部分</a> 已对接 hivemind 验证。
 *
 * <p>stealth_state/night_lights_state 在 simulator 中以 {@code 0/1} 整数推送，
 * 非 boolean。landing_type/landing_protection_type 默认 0，simulator 未实现其状态变化。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_drone_state_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.publishDrcEvents drc_drone_state_push 部分已对接 hivemind 验证")
public record DroneStatePushData(
        /** 飞行器模式码（mode_code 枚举值） */
        int modeCode,
        /** 隐蔽模式状态（0=关闭, 1=开启） */
        int stealthState,
        /** 夜航灯状态（0=关闭, 1=开启） */
        int nightLightsState,
        /** 降落类型 */
        int landingType,
        /** 降落保护类型 */
        int landingProtectionType
) {}
