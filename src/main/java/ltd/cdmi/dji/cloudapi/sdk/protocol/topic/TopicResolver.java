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
 * 根据 MQTT topic 解析 DJI Cloud API 路由信息。
 *
 * <p>DJI Cloud API 的 topic 形如 {@code thing/product/{sn}/{suffix}} 或
 * {@code sys/product/{sn}/{suffix}}，其中 {@code suffix} 标识通道类型
 * （osd / state / services / services_reply / events / ...）。本解析器从 topic 中提取
 * 设备 SN 与通道类型，并结合 {@link TopicChannel} 的方向定义确定消息方向。
 *
 * <p>与 {@link TopicBuilder} 互为逆操作：{@code TopicBuilder} 按 SN + 通道构造 topic，
 * {@code TopicResolver} 从 topic 解析出 SN + 通道。
 *
 * <p>解析逻辑：
 * <ol>
 *   <li>从 topic 提取设备 SN（{@code thing/product/{sn}/...} 或 {@code sys/product/{sn}/...}，SN 位于第 3 段）</li>
 *   <li>从 topic 后缀识别通道类型（匹配 {@link TopicChannel#suffix()}）</li>
 *   <li>method 保留原值（透传，不做解析）</li>
 *   <li>方向由通道类型确定（{@link TopicChannel#direction()}）</li>
 * </ol>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 连接与 Topic 划分</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档定义的 topic 结构与通道方向")
public final class TopicResolver {

    private TopicResolver() {
    }

    /**
     * 解析 topic 路由信息。
     *
     * @param topic  MQTT topic，如 {@code thing/product/1UUXN1Q00A001W/requests_reply}
     * @param method method 名称，原值透传
     * @return 解析结果，含通道、设备 SN、method 与方向
     */
    public static TopicInfo resolve(String topic, String method) {
        String deviceSn = extractDeviceSn(topic);
        TopicChannel channel = resolveChannel(topic);
        TopicDirection direction = (channel != null) ? channel.direction() : null;
        return new TopicInfo(channel, deviceSn, method, direction);
    }

    /**
     * 从 topic 提取设备 SN。
     *
     * <p>topic 形如 {@code thing/product/{sn}/{suffix}} 或 {@code sys/product/{sn}/{suffix}}，
     * SN 位于第 3 段（下标 2）。
     *
     * @param topic MQTT topic
     * @return 设备 SN，无法提取返回 null
     */
    private static String extractDeviceSn(String topic) {
        if (topic == null) {
            return null;
        }
        String[] parts = topic.split("/");
        // 期望：[thing|sys, product, {sn}, suffix...]
        if (parts.length >= 4 && "product".equals(parts[1])) {
            return parts[2];
        }
        return null;
    }

    /**
     * 从 topic 后缀识别通道类型。
     *
     * <p>后缀可能跨多段（如 {@code drc/up}、{@code property/set_reply}），需拼接第 4 段及之后。
     *
     * @param topic MQTT topic
     * @return 匹配到的 {@link TopicChannel}，无法识别返回 null
     */
    private static TopicChannel resolveChannel(String topic) {
        if (topic == null) {
            return null;
        }
        String[] parts = topic.split("/");
        if (parts.length < 4 || !"product".equals(parts[1])) {
            return null;
        }
        StringBuilder suffix = new StringBuilder();
        for (int i = 3; i < parts.length; i++) {
            if (suffix.length() > 0) {
                suffix.append('/');
            }
            suffix.append(parts[i]);
        }
        String suffixStr = suffix.toString();
        for (TopicChannel ch : TopicChannel.values()) {
            if (ch.suffix().equals(suffixStr)) {
                return ch;
            }
        }
        return null;
    }

    /**
     * topic 路由信息。
     *
     * @param channel   消息所属通道，无法识别时为 null
     * @param deviceSn  从 topic 提取的设备 SN，无法提取时为 null
     * @param method    method 名称（原值透传）
     * @param direction 消息方向，无法确定时为 null
     */
    public record TopicInfo(
            TopicChannel channel,
            String deviceSn,
            String method,
            TopicDirection direction
    ) {
    }
}
