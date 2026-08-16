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

/**
 * 设备型号提供者接口。
 *
 * <p>由 {@link DockModel}、{@link DroneModel}、{@link RcModel} 枚举实现，
 * 统一将枚举常量转换为 {@link DeviceModel} record，便于跨型号统一处理。
 *
 * <p>default 方法委托到 {@link #toModel()}，调用方可直接通过枚举实例访问
 * domain/type/subType 等属性，无需显式调用 {@code toModel()}。
 */
public interface DeviceModelProvider {

    /**
     * 转换为 {@link DeviceModel} record。
     *
     * @return 设备型号三元组及展示信息
     */
    DeviceModel toModel();

    /** 设备大类，见 {@link DeviceDomain} */
    default int domain()           { return toModel().domain(); }

    /** 设备类型 */
    default int type()             { return toModel().type(); }

    /** 设备子类型 */
    default int subType()          { return toModel().subType(); }

    /** 完整展示名，如 "大疆机场3" */
    default String displayName()   { return toModel().displayName(); }

    /** 简称，如 "Dock3" */
    default String shortName()     { return toModel().shortName(); }

    /** 默认 SN */
    default String defaultSn()     { return toModel().defaultSn(); }

    /** model_key 格式："domain-type-subType" */
    default String modelKey()      { return toModel().modelKey(); }

    /** 是否为机场（domain=3） */
    default boolean isDock()       { return toModel().isDock(); }

    /** 是否为遥控器（domain=2） */
    default boolean isController() { return toModel().isController(); }

    /** 是否为飞行器（domain=0） */
    default boolean isAircraft()   { return toModel().isAircraft(); }
}
