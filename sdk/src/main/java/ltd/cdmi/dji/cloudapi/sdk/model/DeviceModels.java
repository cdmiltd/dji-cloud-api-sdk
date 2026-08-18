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

import java.util.Optional;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 设备型号跨枚举统一查询工具。
 *
 * <p>DJI Cloud API 通过 (domain, type, sub_type) 三元组唯一标识设备型号，SDK 将型号
 * 按 domain 拆分到 {@link DroneModel}（飞行器）、{@link RcModel}（遥控器）、{@link DockModel}（机场）
 * 三个独立枚举，各自提供 {@code fromType(int, int)} 反查。本工具类封装跨枚举的统一查询入口，
 * 调用方无需自行 switch domain 分发。
 *
 * <p>与 {@link DeviceCompatibility} 职责区分：
 * <ul>
 *   <li>{@code DeviceCompatibility}：判断两个<b>已知型号</b>间的兼容关系（dock↔drone / controller↔drone）</li>
 *   <li>{@code DeviceModels}：从<b>协议三元组数值</b>反查未知型号（domain+type+subType → DeviceModel）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档设备型号三元组定义，反查分发到 DroneModel/RcModel/DockModel 的 fromType")
public final class DeviceModels {

    private DeviceModels() {
    }

    /**
     * 按 (domain, type, sub_type) 三元组跨枚举反查设备型号。
     *
     * <p>根据 domain 值分发到对应枚举的 {@code fromType(int, int)}：
     * <ul>
     *   <li>{@code domain=0}（飞行器）→ {@link DroneModel#fromType(int, int)}</li>
     *   <li>{@code domain=2}（遥控器）→ {@link RcModel#fromType(int, int)}</li>
     *   <li>{@code domain=3}（机场）→ {@link DockModel#fromType(int, int)}</li>
     * </ul>
     *
     * <p>与单枚举 {@code fromType} 抛 {@link IllegalArgumentException} 不同，本方法对未知 domain
     * 或未知 (type, subType) 组合返回 {@link Optional#empty()}，便于调用方在协议数据不全或
     * 设备型号未收录时优雅降级，而非中断处理流程。
     *
     * @param domain 设备大类（0=飞行器, 2=遥控器, 3=机场）
     * @param type    设备类型
     * @param subType 设备子类型
     * @return 匹配的 {@link DeviceModel}；未知 domain 或型号返回 {@link Optional#empty()}
     */
    public static Optional<DeviceModel> findByDomainTypeSubType(int domain, int type, int subType) {
        DeviceDomain deviceDomain;
        try {
            deviceDomain = DeviceDomain.fromValue(domain);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        try {
            return switch (deviceDomain) {
                case AIRCRAFT   -> Optional.of(DroneModel.fromType(type, subType).toModel());
                case CONTROLLER -> Optional.of(RcModel.fromType(type, subType).toModel());
                case DOCK       -> Optional.of(DockModel.fromType(type, subType).toModel());
            };
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * 按设备简称（如 {@code "M30"}、{@code "Dock1"}）跨枚举反查设备型号。
     *
     * <p>遍历 {@link DroneModel}、{@link RcModel}、{@link DockModel} 三个枚举，返回首个
     * {@link DeviceModelProvider#shortName()} 与输入匹配的型号。SDK 未提供跨枚举的简称反查入口，
     * 本方法补充该能力。
     *
     * <p>典型场景：DJI {@code organization_bind} 回调中 {@code device_model} 字段为简称字符串
     * （如 {@code "M30"}），需反查为 {@link DeviceModel} 以获取 (domain, type, subType) 三元组。
     *
     * @param shortName 设备简称（如 {@code "M30"}、{@code "Dock1"}），可为 null 或空字符串
     * @return 匹配的 {@link DeviceModel}；不匹配或输入为 null/空白返回 {@link Optional#empty()}
     */
    public static Optional<DeviceModel> findByShortName(String shortName) {
        if (shortName == null || shortName.isBlank()) return Optional.empty();
        String trimmed = shortName.trim();
        for (DroneModel m : DroneModel.values()) {
            if (trimmed.equals(m.shortName())) return Optional.of(m.toModel());
        }
        for (RcModel m : RcModel.values()) {
            if (trimmed.equals(m.shortName())) return Optional.of(m.toModel());
        }
        for (DockModel m : DockModel.values()) {
            if (trimmed.equals(m.shortName())) return Optional.of(m.toModel());
        }
        return Optional.empty();
    }
}
