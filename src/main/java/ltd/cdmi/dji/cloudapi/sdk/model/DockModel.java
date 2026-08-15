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
 * DJI 机场型号枚举（domain=3）。
 *
 * <p>机场作为 Dock to Cloud 的网关设备，通过 {@code sys/product/{sn}/status} 上报拓扑。
 * 数据从模拟器 DeviceType 提取，型号三元组对齐 DJI 官方文档。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档机场型号三元组：Dock1=3-1-0，Dock2=3-2-0，Dock3=3-3-0")
public enum DockModel implements DeviceModelProvider {

    /** 大疆机场 */
    DOCK1(3, 1, 0, "大疆机场", "Dock1", "1UUXN1Q00A001W"),

    /** 大疆机场2 */
    DOCK2(3, 2, 0, "大疆机场2", "Dock2", "2UUXN1Q00A002W"),

    /** 大疆机场3 */
    DOCK3(3, 3, 0, "大疆机场3", "Dock3", "7UUXN1Q00A008W");

    private final int domain;
    private final int type;
    private final int subType;
    private final String displayName;
    private final String shortName;
    private final String defaultSn;

    DockModel(int domain, int type, int subType, String displayName, String shortName, String defaultSn) {
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
