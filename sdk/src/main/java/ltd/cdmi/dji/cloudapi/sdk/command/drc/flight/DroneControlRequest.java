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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * drone_control 指令请求 data。
 *
 * <p>飞行器控制（综合控制指令）。已废弃 — Dock1 有效，Dock2/Dock3 已废弃，
 * 建议使用 {@link StickControlRequest}（stick_control）替代。
 *
 * <p><b>无回包机制</b>：成功不回复，仅异常时回包 {@code result=非0}。
 * 本指令无 Reply record。
 *
 * <p>字段说明：
 * <ul>
 *   <li>{@code seq} — 序列号，保证指令顺序执行</li>
 *   <li>{@code x} — x 轴位移</li>
 *   <li>{@code y} — y 轴位移</li>
 *   <li>{@code h} — 高度变化</li>
 *   <li>{@code w} — 偏航角变化</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRONE_CONTROL
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "simulator DrcCommandHandler.registerFlightControlHandlers 已对接 hivemind 验证（Dock1 有效，Dock2/3 已废弃）")
@Inferred(
    reason = "drone_control 已废弃，DJI 文档标注 Dock2/Dock3 不再支持；字段结构基于 simulator 实现，DJI 文档未详细列出废弃指令的字段",
    verifyPoint = "drone_control 字段结构（seq/x/y/h/w）待 DJI 文档最终确认"
)
public record DroneControlRequest(
    Integer seq,
    Double x,
    Double y,
    Double h,
    Double w
) {
    public DroneControlRequest {
        Objects.requireNonNull(seq, "seq 必填，DJI JSON 缺失 seq 字段");
        Objects.requireNonNull(x, "x 必填，DJI JSON 缺失 x 字段");
        Objects.requireNonNull(y, "y 必填，DJI JSON 缺失 y 字段");
        Objects.requireNonNull(h, "h 必填，DJI JSON 缺失 h 字段");
        Objects.requireNonNull(w, "w 必填，DJI JSON 缺失 w 字段");
    }
}
