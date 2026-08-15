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
 * DJI Cloud API MQTT 通道类型（对应 topic 后缀）。
 *
 * <p>DJI Cloud API 的 MQTT topic 形如 {@code thing/product/{sn}/{suffix}}，
 * 其中 {@code suffix} 即本枚举的 {@link #suffix()}。每个通道有明确的方向
 * （{@link #direction()}）与用途。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 连接与 Topic 划分</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档定义的标准 topic 后缀与方向")
public enum TopicChannel {

    /** osd：遥测数据上行（设备→云，高频周期上报） */
    OSD("osd", TopicDirection.UP, "遥测数据上行"),

    /** state：状态变化上行（设备→云，仅在状态变化时上报） */
    STATE("state", TopicDirection.UP, "状态变化上行"),

    /** services：服务调用下行（云→设备） */
    SERVICES("services", TopicDirection.DOWN, "服务调用下行"),

    /** services_reply：服务回复上行（设备→云，应答 services） */
    SERVICES_REPLY("services_reply", TopicDirection.UP, "服务回复上行"),

    /** events：事件上行（设备→云） */
    EVENTS("events", TopicDirection.UP, "事件上行"),

    /** events_reply：事件回复下行（云→设备，应答 events） */
    EVENTS_REPLY("events_reply", TopicDirection.DOWN, "事件回复下行"),

    /** requests：设备请求上行（设备→云，设备主动向云请求） */
    REQUESTS("requests", TopicDirection.UP, "设备请求上行"),

    /** requests_reply：请求回复下行（云→设备，应答 requests） */
    REQUESTS_REPLY("requests_reply", TopicDirection.DOWN, "请求回复下行"),

    /** status：设备拓扑上行（设备→云，上线/下线通知） */
    STATUS("status", TopicDirection.UP, "设备拓扑上行"),

    /** status_reply：拓扑回复下行（云→设备，应答 status） */
    STATUS_REPLY("status_reply", TopicDirection.DOWN, "拓扑回复下行"),

    /** drc/up：DRC 上行通道（设备→云，DRC 模式下实时状态推送） */
    DRC_UP("drc/up", TopicDirection.UP, "DRC 上行"),

    /** drc/down：DRC 下行通道（云→设备，DRC 模式下实时控制指令） */
    DRC_DOWN("drc/down", TopicDirection.DOWN, "DRC 下行"),

    /** property/set：属性设置下行（云→设备） */
    PROPERTY_SET("property/set", TopicDirection.DOWN, "属性设置下行"),

    /** property/set_reply：属性设置回复上行（设备→云，应答 property/set） */
    PROPERTY_SET_REPLY("property/set_reply", TopicDirection.UP, "属性设置回复上行");

    private final String suffix;
    private final TopicDirection direction;
    private final String description;

    TopicChannel(String suffix, TopicDirection direction, String description) {
        this.suffix = suffix;
        this.direction = direction;
        this.description = description;
    }

    /** topic 后缀，如 "osd"、"services_reply"、"drc/up" */
    public String suffix() {
        return suffix;
    }

    /** 消息方向 */
    public TopicDirection direction() {
        return direction;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }
}
