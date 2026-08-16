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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * airsense_warning 事件 data。
 *
 * <p>对应 DJI Cloud API {@code airsense_warning} 事件（events 通道）的 data。
 * 用于 AirSense 告警上报，{@code need_reply=0} 单向通知。
 *
 * <p><b>特殊结构</b>：本事件 data 直接是 JSON 数组（非对象包裹），即 {@code data: [...] }，
 * 与其他事件 {@code data: {...}} 的对象结构不同。
 *
 * <p><b>反序列化</b>：通过 {@link JsonDeserialize} 指定
 * {@link AirSenseWarningDataDeserializer} 自定义反序列化器，将整个 JSON 数组
 * 转为 {@code List<Alert>} 再包裹为 record。调用方使用
 * {@code objectMapper.treeToValue(data, AirSenseWarningData.class)} 即可，
 * 与其他事件 POJO 用法完全一致，无需感知 bare-array 结构。
 *
 * <p><b>技术背景</b>：Jackson 2.17 + record 的默认 BeanDeserializer 无法将
 * bare-array 反序列化为 record（需对象 {@code {"alerts": [...]}}），
 * {@code @JsonCreator(DELEGATING)} 静态工厂也不生效，故改用自定义反序列化器。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#AIRSENSE_WARNING}
 *
 * <p>字段依据：simulator {@code AirSenseSimulator.publishAirSenseEvent}（L81-L96）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Alert} — 单个告警（icao/warning_level/latitude/longitude/altitude/altitude_type/
 *       heading/relative_altitude/vert_trend/distance）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/wayline.html")
@Verified(basis = "simulator AirSenseSimulator.publishAirSenseEvent L81-L96 已对接 hivemind 验证；DJI 文档把 airsense_warning 事件归在 dock1/wayline.html 类目下，URL 指向正确")
@JsonDeserialize(using = AirSenseWarningDataDeserializer.class)
public record AirSenseWarningData(
    List<Alert> alerts
) {
    public AirSenseWarningData {
        Objects.requireNonNull(alerts, "alerts 必填，DJI JSON data 不能为空");
    }

    /** airsense_warning 事件 data 数组元素，单个 AirSense 告警。 */
    public record Alert(
        String icao,
        Integer warningLevel,
        Double latitude,
        Double longitude,
        Double altitude,
        Integer altitudeType,
        Double heading,
        Double relativeAltitude,
        Integer vertTrend,
        Double distance
    ) {}
}
