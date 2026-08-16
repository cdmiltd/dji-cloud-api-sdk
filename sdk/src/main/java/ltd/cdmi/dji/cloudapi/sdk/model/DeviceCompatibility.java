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

package ltd.cdmi.dji.cloudapi.sdk.model;

import java.util.Set;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 机场-飞行器-遥控器兼容性矩阵。
 *
 * <p>不同网关设备只支持特定型号的挂载飞行器，本工具类封装兼容性判断，
 * 供模拟器（设备组合校验）与平台（配置校验）共同引用，避免重复维护。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档产品搭配关系：Dock1↔M30/M30T，Dock2↔M3D/M3TD/M30/M30T，Dock3↔M4D/M4TD；遥控器搭配见文档")
public final class DeviceCompatibility {

    private DeviceCompatibility() {
    }

    /**
     * 判断机场与飞行器是否兼容。
     *
     * <ul>
     *   <li>DOCK1 ↔ M30 / M30T</li>
     *   <li>DOCK2 ↔ M3D / M3TD / M30 / M30T</li>
     *   <li>DOCK3 ↔ M4D / M4TD</li>
     * </ul>
     *
     * @param dock   机场型号
     * @param drone  飞行器型号
     * @return true 表示兼容
     */
    public static boolean isCompatible(DockModel dock, DroneModel drone) {
        Set<DroneModel> supported = switch (dock) {
            case DOCK1 -> Set.of(DroneModel.M30, DroneModel.M30T);
            case DOCK2 -> Set.of(DroneModel.M3D, DroneModel.M3TD, DroneModel.M30, DroneModel.M30T);
            case DOCK3 -> Set.of(DroneModel.M4D, DroneModel.M4TD);
        };
        return supported.contains(drone);
    }

    /**
     * 判断遥控器与飞行器是否兼容。
     *
     * <ul>
     *   <li>SMART_CONTROLLER_ENTERPRISE ↔ M300_RTK</li>
     *   <li>RC_PLUS ↔ M350_RTK / M300_RTK / M30 / M30T</li>
     *   <li>RC_PLUS_2 ↔ M400 / M4E / M4T</li>
     *   <li>RC_PRO ↔ MAVIC_3E / MAVIC_3T</li>
     * </ul>
     *
     * @param controller 遥控器型号
     * @param drone      飞行器型号
     * @return true 表示兼容
     */
    public static boolean isCompatible(RcModel controller, DroneModel drone) {
        Set<DroneModel> supported = switch (controller) {
            case SMART_CONTROLLER_ENTERPRISE -> Set.of(DroneModel.M300_RTK);
            case RC_PLUS -> Set.of(DroneModel.M350_RTK, DroneModel.M300_RTK, DroneModel.M30, DroneModel.M30T);
            case RC_PLUS_2 -> Set.of(DroneModel.M400, DroneModel.M4E, DroneModel.M4T);
            case RC_PRO -> Set.of(DroneModel.MAVIC_3E, DroneModel.MAVIC_3T);
        };
        return supported.contains(drone);
    }
}
