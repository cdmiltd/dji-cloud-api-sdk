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

package ltd.cdmi.dji.cloudapi.sdk.protocol.topic;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * MQTT Topic 构造工具。
 *
 * <p>根据网关设备 SN 与 {@link TopicChannel} 构造完整的可订阅/可发布的 MQTT topic。
 * 自动处理 thing/product 与 sys/product 两种前缀的差异：
 * <ul>
 *   <li>大部分通道使用 {@code thing/product/{sn}/{suffix}}</li>
 *   <li>机场上云的 status/status_reply 使用 {@code sys/product/{sn}/{suffix}}</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 连接与 Topic</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "基于 DJI Cloud API 官方 topic 前缀规则构造")
public final class TopicBuilder {

    private TopicBuilder() {
    }

    /**
     * 使用 thing/product 前缀构造完整 topic。
     *
     * <p><b>已废弃</b>：此方法始终使用 thing/product 前缀，但 STATUS/STATUS_REPLY 通道
     * 在机场上云场景需要 sys/product 前缀（Pilot 上云仍用 thing/product）。
     * 前缀取决于网关类型，无法仅凭 channel 自动判断，调用者容易误用。
     *
     * @param sn       网关设备 SN
     * @param channel  通道类型
     * @return 完整 topic，如 {@code thing/product/1UUXN1Q00A001W/osd}
     * @deprecated 仅适用于 thing/product 前缀场景。STATUS/STATUS_REPLY 通道请使用
     *             {@link #build(String, TopicChannel, boolean)} 显式指定前缀，
     *             或使用 {@link #buildWithSysPrefix(String, TopicChannel)} 构造 sys/product 前缀
     */
    @Deprecated
    public static String build(String sn, TopicChannel channel) {
        return String.format(TopicTemplate.thingProduct(channel.suffix()), sn);
    }

    /**
     * 使用 sys/product 前缀构造完整 topic（用于机场上云的 status/status_reply）。
     *
     * @param sn       网关设备 SN
     * @param channel  通道类型
     * @return 完整 topic，如 {@code sys/product/1UUXN1Q00A001W/status}
     */
    public static String buildWithSysPrefix(String sn, TopicChannel channel) {
        return String.format(TopicTemplate.sysProduct(channel.suffix()), sn);
    }

    /**
     * 综合构造方法，按是否使用 sys/product 前缀选择模板。
     *
     * <p>典型用法：机场上云的 status/status_reply 传入 {@code useSysPrefix=true}，
     * 其余通道传入 {@code false}。
     *
     * @param sn            网关设备 SN
     * @param channel       通道类型
     * @param useSysPrefix  true 使用 sys/product 前缀，false 使用 thing/product 前缀
     * @return 完整 topic
     */
    public static String build(String sn, TopicChannel channel, boolean useSysPrefix) {
        String template = useSysPrefix
                ? TopicTemplate.sysProduct(channel.suffix())
                : TopicTemplate.thingProduct(channel.suffix());
        return String.format(template, sn);
    }
}
