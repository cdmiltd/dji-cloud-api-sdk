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
 * airport_bind_status 指令请求 data。
 *
 * <p>对应 DJI Cloud API {@code airport_bind_status} 指令（requests 通道）的请求 data。
 * 用于查询机场绑定状态。
 *
 * <p>字段依据：simulator {@code DockOnlineService} L193-L198 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService L193-L198 已对接 hivemind 验证")
public record AirportBindStatusRequest(
    List<Device> devices
) {
    public AirportBindStatusRequest {
        Objects.requireNonNull(devices, "devices 必填，DJI JSON 缺失 devices 字段");
    }

    /** devices 数组元素，机场设备标识。 */
    public record Device(
        String sn
    ) {
        public Device {
            Objects.requireNonNull(sn, "sn 必填，DJI JSON 缺失 sn 字段");
        }
    }
}
