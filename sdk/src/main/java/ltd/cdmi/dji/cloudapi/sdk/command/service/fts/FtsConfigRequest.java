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

package ltd.cdmi.dji.cloudapi.sdk.command.service.fts;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply;

/**
 * {@code fts_config} 指令请求 data：配置飞行终止系统（FTS）的服务器。
 *
 * <p>回复仅 {@code {result}} → 复用 {@link NoOutputReply}。
 *
 * <p>运营提示（文档说明，SDK 不承载该业务约束）：FTS 触发需飞行器接入 4G 图传（仅 SDR 链路无法触发）；
 * 验证码默认每 120 分钟更新，单架次内有效。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#FTS_CONFIG
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/fts.html")
@Verified(basis = "DJI Dock3 FTS services 文档 fts_config Data 表 {fts_server_type(enum_int 0/1), mqtt_broker}")
public record FtsConfigRequest(
        /** FTS 服务端类型：0=DJI FTS, 1=自定义 */
        Integer ftsServerType,
        /** MQTT Broker 配置（条件必填：fts_server_type=1 时必填，允许 null） */
        MqttBroker mqttBroker
) {
    public FtsConfigRequest {
        Objects.requireNonNull(ftsServerType, "ftsServerType 必填，DJI JSON 缺失 fts_server_type 字段");
    }

    /**
     * 条件必填的 MQTT Broker 连接配置。
     *
     * @param address  Broker 地址
     * @param username 用户名
     * @param password 密码（敏感字段，协议层原样承载，不做脱敏）
     */
    public record MqttBroker(
            /** Broker 地址 */
            String address,
            /** 用户名 */
            String username,
            /** 密码（敏感字段） */
            String password
    ) {}
}
