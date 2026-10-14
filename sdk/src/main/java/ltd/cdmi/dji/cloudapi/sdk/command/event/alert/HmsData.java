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

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * hms 事件 data。
 *
 * <p>对应 DJI Cloud API {@code hms} 事件（events 通道）的 data。
 * 用于 HMS 告警上报，{@code need_reply=0} 单向通知，data 含告警列表（list 数组）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#HMS}
 *
 * <p>字段依据：simulator {@code HmsSimulator.publishHmsEvent}（L158-L176）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Item} — 单个告警项（code/level/module/in_the_sky/device_type/imminent/args）</li>
 *   <li>{@link Item.Args} — 告警参数（component_index/sensor_index）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/hms.html")
@Verified(basis = "DJI Dock3 hms.html Data 表（Item.code Type=text，示例 \"0x16100083\"；其余字段 level(0/1/2)/module(0-3)/in_the_sky(0/1)/device_type(text)/imminent(0/1)/args 核对一致）；另与 simulator HmsSimulator.publishHmsEvent 对接验证")
public record HmsData(
    List<Item> list
) {
    public HmsData {
        Objects.requireNonNull(list, "list 必填，DJI JSON 缺失 list 字段");
    }

    /** hms 事件 list 数组元素，单个告警项。 */
    public record Item(
        /** 告警码（文档 Type=text，十六进制字符串，如 "0x16100083"） */
        String code,
        Integer level,
        Integer module,
        Integer inTheSky,
        String deviceType,
        Integer imminent,
        Args args
    ) {}

    /** item.args 字段，告警参数。 */
    public record Args(
        Integer componentIndex,
        Integer sensorIndex
    ) {}
}
