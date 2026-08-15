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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 飞行器型号枚举（domain=0）。
 *
 * <p>飞行器作为机场或遥控器挂载的子设备，其 OSD/State 数据挂在网关设备的 topic 下。
 * 数据从模拟器 DeviceType 提取，型号三元组对齐 DJI 官方文档。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档飞行器型号三元组，数据从模拟器 DeviceType 提取")
public enum DroneModel implements DeviceModelProvider {

    /** Matrice 30 */
    M30(0, 67, 0, "Matrice 30", "M30", "1581F4HBD12340010101"),

    /** Matrice 30T */
    M30T(0, 67, 1, "Matrice 30T", "M30T", "1581F4HBD12340010201"),

    /** Matrice 3D */
    M3D(0, 91, 0, "Matrice 3D", "M3D", "1581F6HGD23110010101"),

    /** Matrice 3TD */
    M3TD(0, 91, 1, "Matrice 3TD", "M3TD", "1581F6HGD23110010201"),

    /** Matrice 4D */
    M4D(0, 100, 0, "Matrice 4D", "M4D", "1081F8HGD25110010001"),

    /** Matrice 4TD */
    M4TD(0, 100, 1, "Matrice 4TD", "M4TD", "1081F8HGD25110010059"),

    /** Matrice 350 RTK */
    M350_RTK(0, 89, 0, "Matrice 350 RTK", "M350 RTK", "1581F4HBD89110101"),

    /** Matrice 300 RTK */
    M300_RTK(0, 60, 0, "Matrice 300 RTK", "M300 RTK", "1581F4HBD60110101"),

    /** Mavic 3E */
    MAVIC_3E(0, 77, 0, "Mavic 3E", "Mavic 3E", "1581F4HBD77110101"),

    /** Mavic 3T */
    MAVIC_3T(0, 77, 1, "Mavic 3T", "Mavic 3T", "1581F4HBD77110201"),

    /** Mavic 3TA */
    MAVIC_3TA(0, 77, 3, "Mavic 3TA", "Mavic 3TA", "1581F4HBD77110301"),

    /** Matrice 400 */
    M400(0, 103, 0, "Matrice 400", "M400", "1581F4HBD03110101"),

    /** DJI Matrice 4E */
    M4E(0, 99, 0, "DJI Matrice 4E", "M4E", "1581F8HGD99110101"),

    /** DJI Matrice 4T */
    M4T(0, 99, 1, "DJI Matrice 4T", "M4T", "1581F8HGD99110201");

    private final int domain;
    private final int type;
    private final int subType;
    private final String displayName;
    private final String shortName;
    private final String defaultSn;

    DroneModel(int domain, int type, int subType, String displayName, String shortName, String defaultSn) {
        this.domain = domain;
        this.type = type;
        this.subType = subType;
        this.displayName = displayName;
        this.shortName = shortName;
        this.defaultSn = defaultSn;
    }

    @Override
    public DeviceModel toModel() {
        return new DeviceModel(domain, type, subType, displayName, shortName, defaultSn);
    }
}
