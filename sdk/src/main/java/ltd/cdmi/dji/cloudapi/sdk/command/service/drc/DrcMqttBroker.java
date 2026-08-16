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

package ltd.cdmi.dji.cloudapi.sdk.command.service.drc;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DRC MQTT Broker 配置（drc_mode_enter 指令的 mqtt_broker 字段）。
 *
 * <p>对应 DJI Cloud API {@code drc_mode_enter} 请求 data 中 {@code mqtt_broker} 字段。
 *
 * @see DrcModeEnterRequest#mqttBroker()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator AuthFlowHandler 已对接 hivemind 验证")
public record DrcMqttBroker(
    String address,
    String clientId,
    String username,
    Boolean enableTls,
    Long expireTime
) {
    public DrcMqttBroker {
        Objects.requireNonNull(address, "address 必填，DJI JSON 缺失 address 字段");
        Objects.requireNonNull(clientId, "clientId 必填，DJI JSON 缺失 client_id 字段");
        Objects.requireNonNull(username, "username 必填，DJI JSON 缺失 username 字段");
        Objects.requireNonNull(enableTls, "enableTls 必填，DJI JSON 缺失 enable_tls 字段");
        Objects.requireNonNull(expireTime, "expireTime 必填，DJI JSON 缺失 expire_time 字段");
    }
}
