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

package ltd.cdmi.dji.cloudapi.sdk.command.property;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * property/set 消息 data：属性设置请求。
 *
 * <p>property/set 通道（cloud-to-device）用于设置设备属性，
 * data 结构为属性名→值的扁平映射（无包裹键）。与 services 通道不同，不使用 method 字段。
 *
 * <p>可设置属性见 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.PropertySetMethod}。
 *
 * <p>JSON 序列化为扁平 map（{@code @JsonAnyGetter} 展开 properties 到顶层），
 * 反序列化同理（{@code @JsonCreator(DELEGATING)} 将整个 JSON 对象作为 Map 传入）。
 *
 * <p>示例（DJI 协议 data）：
 * <pre>{@code
 * {
 *   "battery_store_mode": 1,
 *   "cover_state": 0
 * }
 * }</pre>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 property/set</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html property/set 结构")
public record PropertySetRequest(
        /** 属性名→值的映射（key 见 PropertySetMethod 枚举） */
        Map<String, Object> properties
) {
    /**
     * 序列化时将 properties 展开为顶层属性（不包裹在 "properties" 键下）。
     */
    @JsonAnyGetter
    public Map<String, Object> properties() {
        return properties;
    }

    /**
     * 反序列化时将整个 JSON 对象作为 Map 传入（扁平 map → record）。
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PropertySetRequest of(Map<String, Object> properties) {
        return new PropertySetRequest(properties);
    }
}
