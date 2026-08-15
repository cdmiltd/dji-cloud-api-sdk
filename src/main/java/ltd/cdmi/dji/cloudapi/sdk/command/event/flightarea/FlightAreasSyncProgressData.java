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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * flight_areas_sync_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code flight_areas_sync_progress} 事件（events 通道）的 data。
 * 用于文件同步进度上报，{@code need_reply=1} 需平台回复，含状态、原因与同步文件信息。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#FLIGHT_AREAS_SYNC_PROGRESS}
 *
 * <p>字段依据：simulator {@code FlightAreaSimulator.publishSyncProgressEvent}（L186-L207）
 * 已对接 hivemind 验证。
 *
 * <p>{@code status} 枚举值：{@code fail}/{@code switch_fail}/{@code synchronized}/
 * {@code synchronizing}/{@code wait_sync}。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link SyncFile} — 同步文件（name/checksum）</li>
 * </ul>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "simulator FlightAreaSimulator.publishSyncProgressEvent L186-L207 已对接 hivemind 验证")
public record FlightAreasSyncProgressData(
    String status,
    Integer reason,
    SyncFile file
) {
    public FlightAreasSyncProgressData {
        Objects.requireNonNull(status, "status 必填，DJI JSON 缺失 status 字段");
        Objects.requireNonNull(reason, "reason 必填，DJI JSON 缺失 reason 字段");
    }

    /** file 字段，同步文件信息。 */
    public record SyncFile(
        String name,
        String checksum
    ) {}
}
