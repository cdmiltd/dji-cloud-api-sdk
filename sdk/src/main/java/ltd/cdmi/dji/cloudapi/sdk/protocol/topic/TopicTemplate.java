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
 * DJI Cloud API 的 MQTT Topic 模板常量。
 *
 * <p>DJI Cloud API 的 topic 分两种前缀：
 * <ul>
 *   <li><b>thing/product</b> 前缀：绝大部分通道使用，形如 {@code thing/product/%s/{suffix}}</li>
 *   <li><b>sys/product</b> 前缀：机场上云的 status/status_reply 使用，形如 {@code sys/product/%s/{suffix}}</li>
 * </ul>
 * <p>其中 {@code %s} 为网关设备 SN，需通过 {@link String#format(String, Object...)} 替换。
 *
 * <p><b>status/status_reply 特殊说明</b>：在机场上云（机场作为网关）场景使用
 * {@code sys/product/%s/...}；在 Pilot 上云（如 DJI RC Plus 2）场景使用
 * {@code thing/product/%s/...}。详见 DJI 设备管理文档。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 连接与 Topic</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档定义的 topic 前缀规则：thing/product 用于业务通道，sys/product 用于机场上云拓扑")
public final class TopicTemplate {

    private TopicTemplate() {
    }

    /** thing/product 前缀模板前半部分 */
    public static final String THING_PRODUCT_PREFIX = "thing/product/%s/";
    /** sys/product 前缀模板前半部分 */
    public static final String SYS_PRODUCT_PREFIX = "sys/product/%s/";

    // ==================== thing/product 通道默认模板 ====================

    /** osd 通道模板 */
    public static final String OSD = thingProduct(TopicChannel.OSD.suffix());
    /** state 通道模板 */
    public static final String STATE = thingProduct(TopicChannel.STATE.suffix());
    /** services 通道模板 */
    public static final String SERVICES = thingProduct(TopicChannel.SERVICES.suffix());
    /** services_reply 通道模板 */
    public static final String SERVICES_REPLY = thingProduct(TopicChannel.SERVICES_REPLY.suffix());
    /** events 通道模板 */
    public static final String EVENTS = thingProduct(TopicChannel.EVENTS.suffix());
    /** events_reply 通道模板 */
    public static final String EVENTS_REPLY = thingProduct(TopicChannel.EVENTS_REPLY.suffix());
    /** requests 通道模板 */
    public static final String REQUESTS = thingProduct(TopicChannel.REQUESTS.suffix());
    /** requests_reply 通道模板 */
    public static final String REQUESTS_REPLY = thingProduct(TopicChannel.REQUESTS_REPLY.suffix());
    /** drc/up 通道模板 */
    public static final String DRC_UP = thingProduct(TopicChannel.DRC_UP.suffix());
    /** drc/down 通道模板 */
    public static final String DRC_DOWN = thingProduct(TopicChannel.DRC_DOWN.suffix());
    /** property/set 通道模板 */
    public static final String PROPERTY_SET = thingProduct(TopicChannel.PROPERTY_SET.suffix());
    /** property/set_reply 通道模板 */
    public static final String PROPERTY_SET_REPLY = thingProduct(TopicChannel.PROPERTY_SET_REPLY.suffix());

    // ==================== sys/product 通道模板（机场上云 status/status_reply） ====================

    /** status 通道模板（机场上云使用 sys/product 前缀） */
    public static final String STATUS = sysProduct(TopicChannel.STATUS.suffix());
    /** status_reply 通道模板（机场上云使用 sys/product 前缀） */
    public static final String STATUS_REPLY = sysProduct(TopicChannel.STATUS_REPLY.suffix());

    /**
     * 构造 thing/product 前缀的 topic 模板。
     *
     * @param suffix topic 后缀，如 {@link TopicChannel#OSD#suffix()}
     * @return topic 模板，如 {@code thing/product/%s/osd}，需用 SN 替换 {@code %s}
     */
    public static String thingProduct(String suffix) {
        return THING_PRODUCT_PREFIX + suffix;
    }

    /**
     * 构造 sys/product 前缀的 topic 模板。
     *
     * @param suffix topic 后缀，如 {@link TopicChannel#STATUS#suffix()}
     * @return topic 模板，如 {@code sys/product/%s/status}，需用 SN 替换 {@code %s}
     */
    public static String sysProduct(String suffix) {
        return SYS_PRODUCT_PREFIX + suffix;
    }
}
