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
 * DJI 设备型号三元组封装。
 *
 * <p>DJI Cloud API 通过 (domain, type, sub_type) 唯一标识设备型号。本 record
 * 在三元组基础上补充展示名、简称与默认 SN，便于模拟器和平台统一引用。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 *
 * @param domain      设备大类，见 {@link DeviceDomain}
 * @param type        设备类型
 * @param subType     设备子类型
 * @param displayName 完整展示名，如 "大疆机场3"
 * @param shortName   简称，如 "Dock3"
 * @param defaultSn   默认 SN（SN 格式对齐 DJI 真实设备：机场 15 位，飞行器 20 位）
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档设备型号三元组定义")
public record DeviceModel(
        int domain,
        int type,
        int subType,
        String displayName,
        String shortName,
        String defaultSn
) {

    /** model_key 格式："domain-type-subType"，如 "3-3-0" 表示 Dock3 */
    public String modelKey() {
        return domain + "-" + type + "-" + subType;
    }

    /** 是否为机场（domain=3） */
    public boolean isDock() {
        return domain == DeviceDomain.DOCK.value();
    }

    /** 是否为遥控器（domain=2） */
    public boolean isController() {
        return domain == DeviceDomain.CONTROLLER.value();
    }

    /** 是否为飞行器（domain=0） */
    public boolean isAircraft() {
        return domain == DeviceDomain.AIRCRAFT.value();
    }
}
