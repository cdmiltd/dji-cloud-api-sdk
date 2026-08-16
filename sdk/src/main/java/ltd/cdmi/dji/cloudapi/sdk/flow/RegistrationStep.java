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

package ltd.cdmi.dji.cloudapi.sdk.flow;

/**
 * DJI Cloud API 设备注册流程的一个步骤。
 *
 * <p>描述注册/上线流程中每一步的 method、所用 MQTT 通道、超时与重试策略，
 * 供模拟器与 hivemind 平台统一引用，避免流程定义散落在实现代码中。
 *
 * @param methodName           步骤对应的 method 名称（如 "config"、"airport_bind_status"）
 * @param description          步骤描述
 * @param channelType          使用的 MQTT 通道类型（REQUESTS / REQUESTS_REPLY / STATUS）
 * @param timeoutSeconds       超时时间（秒），超时表示平台无响应
 * @param retryCount           重试次数（0=不重试，config 步骤为 3）
 * @param retryIntervalSeconds 重试间隔（秒）
 */
public record RegistrationStep(
        String methodName,
        String description,
        ChannelType channelType,
        int timeoutSeconds,
        int retryCount,
        int retryIntervalSeconds
) {

    /**
     * 注册流程步骤使用的 MQTT 通道类型。
     *
     * <p>注册步骤（config / airport_bind_status / airport_organization_get /
     * airport_organization_bind）通过 requests 通道上行请求、requests_reply 通道接收回复；
     * 上线步骤（update_topo）通过 status 通道上行设备拓扑，不走 requests 通道。
     */
    public enum ChannelType {

        /** requests 通道（设备→云），注册请求上行 */
        REQUESTS,

        /** requests_reply 通道（云→设备），注册请求回复下行 */
        REQUESTS_REPLY,

        /** status 通道（设备→云），update_topo 设备拓扑上行 */
        STATUS
    }
}
