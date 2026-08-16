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

package ltd.cdmi.dji.cloudapi.sdk.command.status;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * update_topo 状态上报 data。
 *
 * <p>对应 DJI Cloud API {@code update_topo}（status 通道）的上报 data。
 * 用于机场上线/下线拓扑通知。{@code sub_devices} 为空列表表示下线。
 *
 * <p>{@code domain} 为 {@code String} 类型（DJI 文档规定 domain 为文本，非数值）。
 *
 * <p>字段依据：simulator {@code DockOnlineService.buildUpdateTopoData} L456-L485 +
 * {@code offline()} L401-L408 已对接 hivemind 验证。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService.buildUpdateTopoData L456-L485 + offline() L401-L408 已对接 hivemind 验证")
public record UpdateTopoData(
    String domain,
    Integer type,
    Integer subType,
    String deviceSecret,
    String nonce,
    List<SubDevice> subDevices,
    String thingVersion
) {
    public UpdateTopoData {
        Objects.requireNonNull(domain, "domain 必填，DJI JSON 缺失 domain 字段");
        Objects.requireNonNull(type, "type 必填，DJI JSON 缺失 type 字段");
        Objects.requireNonNull(subType, "subType 必填，DJI JSON 缺失 sub_type 字段");
        Objects.requireNonNull(deviceSecret, "deviceSecret 必填，DJI JSON 缺失 device_secret 字段");
        Objects.requireNonNull(nonce, "nonce 必填，DJI JSON 缺失 nonce 字段");
        Objects.requireNonNull(thingVersion, "thingVersion 必填，DJI JSON 缺失 thing_version 字段");
    }

    /**
     * sub_devices 数组元素，子设备（飞行器）拓扑信息。
     *
     * <p>{@code index} 设备挂载位置（如 {@code "A"}）。
     * {@code domain} 为 {@code String} 类型（DJI 文档规定 domain 为文本，非数值）。
     *
     * <p><b>{@code index}/{@code domain} 可空</b>：RC Plus 2 的子设备不上报
     * {@code domain}/{@code index} 字段（参见 {@link ltd.cdmi.dji.cloudapi.sdk.flow.PilotRegistrationFlow#UPDATE_TOPO}
     * 的 {@code @Inferred} 注解，待真机抓包确认）。机场场景必填，Pilot 场景可省略。
     */
    public record SubDevice(
        String sn,
        String domain,
        Integer type,
        Integer subType,
        String index,
        String deviceSecret,
        String nonce,
        String thingVersion
    ) {
        public SubDevice {
            Objects.requireNonNull(sn, "sn 必填，DJI JSON 缺失 sn 字段");
            Objects.requireNonNull(type, "type 必填，DJI JSON 缺失 type 字段");
            Objects.requireNonNull(subType, "subType 必填，DJI JSON 缺失 sub_type 字段");
            Objects.requireNonNull(deviceSecret, "deviceSecret 必填，DJI JSON 缺失 device_secret 字段");
            Objects.requireNonNull(nonce, "nonce 必填，DJI JSON 缺失 nonce 字段");
            Objects.requireNonNull(thingVersion, "thingVersion 必填，DJI JSON 缺失 thing_version 字段");
            // domain/index 允许 null：RC Plus 2 子设备不上报这两个字段（PilotRegistrationFlow.UPDATE_TOPO @Inferred）
        }
    }
}
