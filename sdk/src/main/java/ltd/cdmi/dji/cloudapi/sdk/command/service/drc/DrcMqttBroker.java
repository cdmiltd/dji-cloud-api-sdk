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
 * <p>字段语义（依据 DJI 官方文档 drc.md 字段表）：
 * <ul>
 *   <li>{@code address}：DRC MQTT Broker 地址（如 {@code emqx:1883}），<b>必填</b></li>
 *   <li>{@code client_id}：DRC 专用连接的 MQTT client 标识（如 {@code drc-{sn}}），<b>必填</b>，
 *       在 EMQX 中不可重复</li>
 *   <li>{@code username}：建立连接时认证所需要的用户名，可空（匿名认证场景）</li>
 *   <li>{@code password}：建立连接时认证所需要的密码（如 EMQX JWT），可空</li>
 *   <li>{@code enable_tls}：是否启用 TLS，可空（未下发视为不启用）</li>
 *   <li>{@code expire_time}：认证信息过期时间，<b>单位秒</b>（绝对时间戳），可空。
 *       文档原文："在有效期内认证信息可以重复使用，认证信息过期后，并不会影响已建立连接的设备"
 *       ——即到期<b>不断开</b>已建立的连接</li>
 * </ul>
 *
 * <p>历史问题：v1.16.1.2 及之前全部字段 requireNonNull 强制必填且遗漏 {@code password}，
 * 平台下发缺省字段（如无 expire_time）的合法报文时反序列化失败，导致设备端误判为
 * "未携带 mqtt_broker" 而跳过 DRC 专用连接建立（EMQX 看不到 drc-{sn} client）。
 *
 * @see DrcModeEnterRequest#mqttBroker()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/drc.html")
@Verified(basis = "DJI 文档字段表（rc-pro/drc.md、dock2 110.drc.md）：mqtt_broker 含 address/client_id/username/password/expire_time/enable_tls；expire_time 单位秒且过期不断连")
public record DrcMqttBroker(
    String address,
    String clientId,
    String username,
    String password,
    Boolean enableTls,
    Long expireTime
) {
    public DrcMqttBroker {
        // 仅校验建连必需字段；username/password/enableTls/expireTime 文档未标必填，允许缺省
        //（接收端按需容错：enable_tls 缺省视为 false，expire_time 缺省视为凭证不过期）
        Objects.requireNonNull(address, "address 必填，DJI JSON 缺失 address 字段");
        Objects.requireNonNull(clientId, "clientId 必填，DJI JSON 缺失 client_id 字段");
    }
}
