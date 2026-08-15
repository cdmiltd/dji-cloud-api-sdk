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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * poi_circle_status 事件 data。
 *
 * <p>对应 DJI Cloud API {@code poi_circle_status} 事件（events 通道）的 data。
 * 用于 POI 环绕状态信息通知，{@code need_reply=1} 需平台回复，含状态、原因、环绕半径与速度。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#POI_CIRCLE_STATUS}
 *
 * <p>标记 @Inferred：simulator 实现方法名为 {@code poi_status_notify}，而
 * {@code EventMethod} 枚举为 {@code poi_circle_status}，两者可能为同一事件不同文档版本命名，
 * 待真机验证方法名及字段结构。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Inferred(
    reason = "simulator 实现 poi_status_notify 方法名，EventMethod 枚举为 poi_circle_status，两者可能为同一事件不同文档版本命名",
    verifyPoint = "真机验证 poi_circle_status vs poi_status_notify 方法名及字段结构"
)
public record PoiCircleStatusData(
    String status,
    Integer reason,
    Double circleRadius,
    Double circleSpeed,
    Double maxCircleSpeed
) {
    public PoiCircleStatusData {
        Objects.requireNonNull(status, "status 必填，DJI JSON 缺失 status 字段");
        Objects.requireNonNull(reason, "reason 必填，DJI JSON 缺失 reason 字段");
    }
}
