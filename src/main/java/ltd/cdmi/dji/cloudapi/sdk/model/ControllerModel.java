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
 * DJI 遥控器型号枚举（domain=2）。
 *
 * <p>遥控器作为 Pilot to Cloud 的网关设备，搭配手持飞行器上云。
 * 数据从模拟器 DeviceType 提取，型号三元组对齐 DJI 官方文档。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档遥控器型号三元组，数据从模拟器 DeviceType 提取")
public enum ControllerModel implements DeviceModelProvider {

    /** DJI 带屏遥控器行业版（搭配 Matrice 300 RTK） */
    SMART_CONTROLLER_ENTERPRISE(2, 56, 0, "DJI 带屏遥控器行业版", "Smart Controller Enterprise", "1581F5RCD05600101"),

    /** DJI RC Plus */
    RC_PLUS(2, 119, 0, "DJI RC Plus", "RC Plus", "1581F5RCD001001"),

    /** DJI RC Plus 2 */
    RC_PLUS_2(2, 174, 0, "DJI RC Plus 2", "RC Plus 2", "1581F5RCD002001"),

    /** DJI RC Pro 行业版 */
    RC_PRO(2, 144, 0, "DJI RC Pro 行业版", "RC Pro", "1581F5RCD003001");

    private final int domain;
    private final int type;
    private final int subType;
    private final String displayName;
    private final String shortName;
    private final String defaultSn;

    ControllerModel(int domain, int type, int subType, String displayName, String shortName, String defaultSn) {
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
