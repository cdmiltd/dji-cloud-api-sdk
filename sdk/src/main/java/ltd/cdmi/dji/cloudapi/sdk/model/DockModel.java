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
 * DJI 机场型号枚举（domain=3）。
 *
 * <p>机场作为 Dock to Cloud 的网关设备，通过 {@code sys/product/{sn}/status} 上报拓扑。
 * 数据从模拟器 DeviceType 提取，型号三元组对齐 DJI 官方文档。
 *
 * <p>反查：{@link #fromType(int, int)} 按 (type, subType) 查找，
 * {@link #fromModelKey(String)} 按 "domain-type-subType" 字符串查找。
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

    /** (type-subType) → 枚举 查找表 */
    private static final Map<String, DockModel> BY_TYPE =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(
                    e -> e.type + "-" + e.subType, Function.identity()));

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

    /**
     * 按 (type, subType) 反查机场型号。
     *
     * @param type    设备类型
     * @param subType 设备子类型
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException type+subType 不匹配任何已知型号
     */
    public static DockModel fromType(int type, int subType) {
        DockModel model = BY_TYPE.get(type + "-" + subType);
        if (model == null) {
            throw new IllegalArgumentException("未知的机场型号: type=" + type + ", subType=" + subType);
        }
        return model;
    }

    /**
     * 按 model_key 反查机场型号。
     *
     * @param modelKey 格式 "domain-type-subType"，如 "3-3-0"
     * @return 匹配的枚举常量
     * @throws IllegalArgumentException modelKey 格式错误、domain 不匹配或型号未知
     */
    public static DockModel fromModelKey(String modelKey) {
        if (modelKey == null) {
            throw new IllegalArgumentException("modelKey 不能为 null");
        }
        String[] parts = modelKey.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("modelKey 格式错误，应为 'domain-type-subType': " + modelKey);
        }
        int domain = Integer.parseInt(parts[0]);
        if (domain != DeviceDomain.DOCK.value()) {
            throw new IllegalArgumentException("modelKey domain=" + domain + " 不是机场（domain=3）: " + modelKey);
        }
        return fromType(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}
