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
 * DJI Cloud API MQTT 消息方向。
 *
 * <p>DJI Cloud API 基于 MQTT 实现，所有交互都通过 topic 划分方向：
 * <ul>
 *   <li>{@link #UP}：设备 → 云，上行（遥测、状态、事件、服务回复等）</li>
 *   <li>{@link #DOWN}：云 → 设备，下行（服务调用、属性设置、事件回复等）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">DJI Cloud API 连接介绍</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "DJI Cloud API 官方文档明确按 topic 后缀划分上行/下行方向")
public enum TopicDirection {

    /** 设备 → 云，上行 */
    UP("设备→云，上行"),

    /** 云 → 设备，下行 */
    DOWN("云→设备，下行");

    private final String description;

    TopicDirection(String description) {
        this.description = description;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }
}
