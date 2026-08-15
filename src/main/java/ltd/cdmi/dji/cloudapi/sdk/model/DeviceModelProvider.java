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
 * <p>由 {@link DockModel}、{@link DroneModel}、{@link ControllerModel} 枚举实现，
 * 统一将枚举常量转换为 {@link DeviceModel} record，便于跨型号统一处理。
 */
public interface DeviceModelProvider {

    /**
     * 转换为 {@link DeviceModel} record。
     *
     * @return 设备型号三元组及展示信息
     */
    DeviceModel toModel();
}
