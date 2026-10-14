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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_ai_spotlight_zoom_select} 指令请求 data。
 *
 * <p>AI 框选目标跟随。提交画面归一化坐标的矩形框启动跟随，随后需下发
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.NoParameterDrcRequest}（drc_ai_spotlight_zoom_confirm）
 * 开始追踪。
 *
 * <p>Reply 使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.DrcResultReply}。
 *
 * <p><b>坐标约定</b>：归一化坐标 [0, 1] × 10000 = 实际传输值 [0, 10000]（整数），
 * 与 SEI 解析中 {@code dji_ai_obj_2d_box_with_distance} 的 {@code cx/cy/w/h} 单位一致
 * （1/10000 × 画面宽/高）。
 *
 * <p>字段说明（文档 Type={@code int}、constraint {@code {max:10000, min:0}}）：
 * <ul>
 *   <li>{@code centerX} — 框的中心点 X 坐标，单位 1/10000 × 画面宽度，范围 [0, 10000]</li>
 *   <li>{@code centerY} — 框的中心点 Y 坐标，单位 1/10000 × 画面高度，范围 [0, 10000]</li>
 *   <li>{@code width} — 框的宽度，单位 1/10000 × 画面宽度，范围 [0, 10000]</li>
 *   <li>{@code height} — 框的高度，单位 1/10000 × 画面高度，范围 [0, 10000]</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcMethod#DRC_AI_SPOTLIGHT_ZOOM_SELECT
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.18 remote-control 文档 drc_ai_spotlight_zoom_select Data 表 center_x/center_y/width/height Type=int、约束 {max:10000, min:0}")
public record AiSpotlightZoomSelectRequest(
    Integer centerX,
    Integer centerY,
    Integer width,
    Integer height
) {
    public AiSpotlightZoomSelectRequest {
        Objects.requireNonNull(centerX, "centerX 必填，DJI JSON 缺失 center_x 字段");
        Objects.requireNonNull(centerY, "centerY 必填，DJI JSON 缺失 center_y 字段");
        Objects.requireNonNull(width, "width 必填，DJI JSON 缺失 width 字段");
        Objects.requireNonNull(height, "height 必填，DJI JSON 缺失 height 字段");
    }
}
