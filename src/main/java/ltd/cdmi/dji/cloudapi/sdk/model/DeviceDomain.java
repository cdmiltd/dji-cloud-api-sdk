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
 * DJI 设备 domain 值。
 *
 * <p>DJI Cloud API 通过 (domain, type, sub_type) 三元组唯一标识设备型号，
 * 其中 domain 表示设备大类。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持与 domain 定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档产品支持：domain=0 飞行器，domain=2 遥控器，domain=3 机场")
public enum DeviceDomain {

    /** 飞行器（domain=0） */
    AIRCRAFT(0, "飞行器"),

    /** 遥控器（domain=2，Pilot to Cloud 网关设备） */
    CONTROLLER(2, "遥控器"),

    /** 机场（domain=3，Dock to Cloud 网关设备） */
    DOCK(3, "机场");

    private final int value;
    private final String description;

    DeviceDomain(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /** domain 数值 */
    public int value() {
        return value;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }
}
