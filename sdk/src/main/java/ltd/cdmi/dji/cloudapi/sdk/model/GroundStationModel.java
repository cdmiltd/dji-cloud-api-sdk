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

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 地面站型号枚举（domain=8）。
 *
 * <p>地面站（O4 漫游基站）作为 Dock to Cloud 的网关设备，通过
 * {@code sys/product/{gateway_sn}/status} 上报拓扑，osd/state 走
 * {@code thing/product/{device_sn}} 双通道。
 *
 * <p>反查：{@link #fromType(int, int)} 按 (type, subType) 查找，
 * {@link #fromModelKey(String)} 按 "domain-type-subType" 字符串查找。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/device.html">DJI 地面站设备标识</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/device.html")
@Verified(basis = "DJI Cloud API 官方文档 O4 地面站设备标识：domain=8, type=135, sub_type=0")
public enum GroundStationModel implements DeviceModelProvider {

    /** 大疆地面站（O4 漫游基站） */
    O4(8, 135, 0, "大疆地面站", "O4", "8UUXN1Q00A008W");

    private final int domain;
    private final int type;
    private final int subType;
    private final String displayName;
    private final String shortName;
    private final String defaultSn;

    /** (type-subType) → 枚举 查找表 */
    private static final Map<String, GroundStationModel> BY_TYPE =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(
                    e -> e.type + "-" + e.subType, Function.identity()));

    GroundStationModel(int domain, int type, int subType, String displayName, String shortName, String defaultSn) {
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

    /**
     * 按 (type, subType) 反查地面站型号。
     *
     * @param type    设备类型
     * @param subType 设备子类型
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException type+subType 不匹配任何已知型号
     */
    public static GroundStationModel fromType(int type, int subType) {
        GroundStationModel model = BY_TYPE.get(type + "-" + subType);
        if (model == null) {
            throw new IllegalArgumentException("未知的地面站型号: type=" + type + ", subType=" + subType);
        }
        return model;
    }

    /**
     * 按 model_key 反查地面站型号。
     *
     * @param modelKey 格式 "domain-type-subType"，如 "8-135-0"
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException modelKey 格式错误、domain 不匹配或型号未知
     */
    public static GroundStationModel fromModelKey(String modelKey) {
        if (modelKey == null) {
            throw new IllegalArgumentException("modelKey 不能为 null");
        }
        String[] parts = modelKey.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("modelKey 格式错误，应为 'domain-type-subType': " + modelKey);
        }
        int domain = Integer.parseInt(parts[0]);
        if (domain != DeviceDomain.GROUND_STATION.value()) {
            throw new IllegalArgumentException("modelKey domain=" + domain + " 不是地面站（domain=8）: " + modelKey);
        }
        return fromType(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}
