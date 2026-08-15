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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.model.DeviceModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 设备上线（update_topo）流程定义与报文构造。
 *
 * <p>从模拟器 {@code DockOnlineService.publishStatus()} / {@code buildUpdateTopoData()} 提取。
 * 机场上云通过 {@code sys/product/{gatewaySn}/status} 上报；Pilot 上云按遥控器型号差异化
 * （见 {@link PilotRegistrationFlow}，使用 {@code thing/product/{gatewaySn}/status}）。
 *
 * <p>报文结构（对齐 DJI 官方文档「设备拓扑更新」，使用标准 envelope tid/bid/method/timestamp/data）：
 * <pre>{@code
 * {
 *   "tid": "uuid",
 *   "bid": "uuid",
 *   "method": "update_topo",
 *   "timestamp": 1234567890123,
 *   "data": {
 *     "domain": "3",            // 网关 domain（string）
 *     "type": 3,                // 网关 type（int）
 *     "sub_type": 0,            // 网关 sub_type（int）
 *     "device_secret": "secret",
 *     "nonce": "nonce",
 *     "thing_version": "3.0.0.0",
 *     "sub_devices": [          // 子设备列表，空列表表示下线
 *       {
 *         "sn": "drone-sn",
 *         "domain": "0",        // 子设备 domain（string）
 *         "type": 60,
 *         "sub_type": 0,
 *         "index": "A",
 *         "device_secret": "secret",
 *         "nonce": "nonce",
 *         "thing_version": "3.0.0.0"
 *       }
 *     ]
 *   }
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/device.html">DJI 设备拓扑更新</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/device.html")
@Verified(basis = "DJI 官方文档「设备拓扑更新」：topic=sys/product/{gateway_sn}/status，envelope 含 tid/bid/method/timestamp/data，data 含 domain(string)/type(int)/sub_type(int)/device_secret/nonce/thing_version/sub_devices")
public final class OnlineFlow {

    private OnlineFlow() {
    }

    /** update_topo 的 method 名称 */
    public static final String METHOD = "update_topo";

    /**
     * update_topo 子设备描述（如飞行器）。
     *
     * @param sn           子设备序列号
     * @param model        子设备型号三元组（提供 domain/type/sub_type）
     * @param index        连接网关的通道索引（如 "A"）
     * @param deviceSecret 子设备密钥
     * @param nonce        nonce
     * @param thingVersion 子设备物模型版本
     */
    public record SubDevice(
            String sn,
            DeviceModel model,
            String index,
            String deviceSecret,
            String nonce,
            String thingVersion
    ) {
    }

    /**
     * 构造 update_topo 上线报文的 JSON 字符串。
     *
     * <p>tid/bid 自动生成（UUID），timestamp 取当前毫秒。报文发布到
     * {@code sys/product/{gatewaySn}/status}（机场）或 {@code thing/product/{gatewaySn}/status}（Pilot）。
     * 按 DJI 规范，网关 SN 属于 topic 而非 payload body，此参数用于调用方关联报文归属的网关。
     *
     * @param gatewaySn    网关设备 SN（报文所属 topic 的网关，非 payload 字段）
     * @param gatewayModel 网关设备型号三元组（提供 domain/type/sub_type）
     * @param deviceSecret 网关设备密钥
     * @param nonce        nonce
     * @param thingVersion 网关物模型版本
     * @param subDevices   子设备列表，飞行器激活时包含飞行器，休眠/下线时传空列表
     * @return update_topo 报文 JSON 字符串
     */
    public static String buildUpdateTopoPayload(
            String gatewaySn,
            DeviceModel gatewayModel,
            String deviceSecret,
            String nonce,
            String thingVersion,
            List<SubDevice> subDevices
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        // DJI 文档：domain 为 string，type/sub_type 为 int
        data.put("domain", String.valueOf(gatewayModel.domain()));
        data.put("type", gatewayModel.type());
        data.put("sub_type", gatewayModel.subType());
        data.put("device_secret", deviceSecret);
        data.put("nonce", nonce);
        data.put("sub_devices", buildSubDevices(subDevices));
        data.put("thing_version", thingVersion);

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("tid", UUID.randomUUID().toString());
        envelope.put("bid", UUID.randomUUID().toString());
        envelope.put("method", METHOD);
        envelope.put("timestamp", System.currentTimeMillis());
        envelope.put("data", data);

        return MessageCodec.toJson(envelope);
    }

    /**
     * 构造子设备列表（每个子设备含 sn/domain(string)/type/sub_type/index/device_secret/nonce/thing_version）。
     *
     * @param subDevices 子设备列表
     * @return 子设备数据列表，空输入返回空列表
     */
    private static List<Map<String, Object>> buildSubDevices(List<SubDevice> subDevices) {
        if (subDevices == null || subDevices.isEmpty()) {
            return List.of();
        }
        return subDevices.stream()
                .map(sd -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("sn", sd.sn());
                    m.put("domain", String.valueOf(sd.model().domain()));
                    m.put("type", sd.model().type());
                    m.put("sub_type", sd.model().subType());
                    m.put("index", sd.index());
                    m.put("device_secret", sd.deviceSecret());
                    m.put("nonce", sd.nonce());
                    m.put("thing_version", sd.thingVersion());
                    return m;
                })
                .toList();
    }
}
