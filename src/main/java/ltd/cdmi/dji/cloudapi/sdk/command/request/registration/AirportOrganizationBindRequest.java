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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * airport_organization_bind 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code airport_organization_bind} 指令（requests 通道）的请求 data。
 * 用于将机场及子设备绑定到指定组织。
 *
 * <p>{@code device_model_key} 格式 {@code "domain-type-subtype"}（如 {@code "3-1-0"}）。
 *
 * <p>字段依据：simulator {@code DockOnlineService} L230-L247 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService L230-L247 已对接 hivemind 验证")
public record AirportOrganizationBindRequest(
    List<BindDevice> bindDevices
) {
    public AirportOrganizationBindRequest {
        Objects.requireNonNull(bindDevices, "bindDevices 必填，DJI JSON 缺失 bind_devices 字段");
    }

    /** bind_devices 数组元素，待绑定设备信息。 */
    public record BindDevice(
        String sn,
        String deviceModelKey,
        String deviceCallsign,
        String organizationId,
        String deviceBindingCode
    ) {
        public BindDevice {
            Objects.requireNonNull(sn, "sn 必填，DJI JSON 缺失 sn 字段");
            Objects.requireNonNull(deviceModelKey, "deviceModelKey 必填，DJI JSON 缺失 device_model_key 字段");
            Objects.requireNonNull(deviceCallsign, "deviceCallsign 必填，DJI JSON 缺失 device_callsign 字段");
            Objects.requireNonNull(organizationId, "organizationId 必填，DJI JSON 缺失 organization_id 字段");
            Objects.requireNonNull(deviceBindingCode, "deviceBindingCode 必填，DJI JSON 缺失 device_binding_code 字段");
        }
    }
}
