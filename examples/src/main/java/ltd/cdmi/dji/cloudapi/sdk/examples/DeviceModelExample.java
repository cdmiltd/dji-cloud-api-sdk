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

package ltd.cdmi.dji.cloudapi.sdk.examples;

import ltd.cdmi.dji.cloudapi.sdk.model.DeviceCompatibility;
import ltd.cdmi.dji.cloudapi.sdk.model.DeviceModel;
import ltd.cdmi.dji.cloudapi.sdk.model.DeviceModels;
import ltd.cdmi.dji.cloudapi.sdk.model.DockModel;
import ltd.cdmi.dji.cloudapi.sdk.model.DroneModel;

/**
 * 示例 5：设备型号查询与兼容性校验。
 *
 * <p>演示 DeviceModels.findByDomainTypeSubType 从协议三元组反查型号，
 * 以及 DeviceCompatibility.isCompatible 校验机场-飞行器兼容性。
 *
 * <p>关键点：
 * <ul>
 *   <li>DJI 用 (domain, type, sub_type) 三元组唯一标识型号：
 *       domain=0 飞行器 / 2 遥控器 / 3 机场</li>
 *   <li>未知三元组返回 {@code Optional.empty()}（优雅降级，不抛异常），
 *       与单枚举 {@code fromType} 抛 IllegalArgumentException 不同</li>
 *   <li>organization_bind 回调的 device_model 为简称字符串，用
 *       findByShortName 反查回三元组</li>
 * </ul>
 */
public class DeviceModelExample {

    public static void main(String[] args) {
        // 1. 从 update_topo 上报的 (domain, type, sub_type) 反查设备型号
        System.out.println("===== 设备型号反查 =====");
        lookup(0, 67, 0);    // M30（与 DroneModel.M30 一致）
        lookup(3, 3, 0);     // Dock3（与 DockModel.DOCK3 一致）
        // 未知三元组 → Optional.empty()，优雅降级不抛异常
        lookup(0, 999, 0);

        // 简称反查（organization_bind 回调中 device_model 为简称字符串）
        DeviceModels.findByShortName("M4D")
                .ifPresent(m -> System.out.println("shortName=M4D → domain=" + m.domain()
                        + ", type=" + m.type() + ", subType=" + m.subType()));

        // 2. 兼容性校验
        System.out.println("\n===== 兼容性校验 =====");
        checkCompatible(DockModel.DOCK3, DroneModel.M4D);   // 兼容
        checkCompatible(DockModel.DOCK1, DroneModel.M4D);   // 不兼容
        checkCompatible(DockModel.DOCK2, DroneModel.M30T);  // 兼容
    }

    /** 三元组反查并打印，未知型号打印降级提示而非静默吞掉。 */
    private static void lookup(int domain, int type, int subType) {
        String name = DeviceModels.findByDomainTypeSubType(domain, type, subType)
                .map(DeviceModel::shortName)
                .orElse("未收录（Optional.empty）");
        System.out.println("domain=" + domain + ",type=" + type + ",subType=" + subType
                + " → " + name);
    }

    private static void checkCompatible(DockModel dock, DroneModel drone) {
        boolean ok = DeviceCompatibility.isCompatible(dock, drone);
        System.out.println(dock.shortName() + " + " + drone.shortName()
                + " → " + (ok ? "兼容" : "不兼容"));
    }
}
