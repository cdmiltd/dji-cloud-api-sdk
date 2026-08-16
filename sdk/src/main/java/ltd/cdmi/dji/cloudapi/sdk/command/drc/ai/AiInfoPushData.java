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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_ai_info_push} 推送数据：AI 目标识别功能状态。
 *
 * <p>设备通过 {@code drc/up} 通道推送 AI 功能相关状态，包括识别/跟随开关、跟随状态与原因、
 * 可用模型列表、当前选中模型与参数、航线 AI 状态等，便于端侧做 UI 呈现与异常处理。
 *
 * <p><b>信封</b>：DRC 上行 {@code {method, data, seq}}，{@code seq} 与 data 同级。
 *
 * <p>字段说明（顶层 6 个，全部可选以兼容不同推送场景）：
 * <ul>
 *   <li>{@code identifyOn} — 识别开关（{@link AiSwitchState}）</li>
 *   <li>{@code spotlightZoomOn} — 跟随开关（{@link AiSwitchState}）</li>
 *   <li>{@code aiSpotlightZoom} — AI 跟随状态与原因（{@link SpotlightZoomState}）</li>
 *   <li>{@code aiModelList} — 设备可用的 AI 模型列表（{@code List<AiModelInfo>}）</li>
 *   <li>{@code selectedAiModel} — 当前选中模型信息（{@link SelectedAiModel}）</li>
 *   <li>{@code aiWaylineState} — 航线 AI 状态（{@link AiWaylineState}，{@link Inferred}）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 状态上报</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcUpMethod#DRC_AI_INFO_PUSH
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_info_push Data 表 5 个顶层字段 + Example JSON 核实")
public record AiInfoPushData(
        /** 识别开关：0=关闭, 1=开启 */
        AiSwitchState identifyOn,
        /** 跟随开关：0=关闭, 1=开启 */
        AiSwitchState spotlightZoomOn,
        /** AI 跟随状态与原因 */
        SpotlightZoomState aiSpotlightZoom,
        /** 设备可用的 AI 模型列表 */
        List<AiModelInfo> aiModelList,
        /** 当前选中模型信息 */
        SelectedAiModel selectedAiModel,
        /**
         * 航线 AI 状态（DJI 文档 Data 表未列出，基于 Example 推断）。
         * <p>字段存在性本身为推断：Data 表仅列 5 个顶层字段，Example JSON 出现第 6 个
         * {@code ai_wayline_state}，字段类型见 {@link AiWaylineState}（同样标注 {@link Inferred}）。
         */
        @Inferred(
                reason = "DJI 文档 drc_ai_info_push Data 表未列出 ai_wayline_state 字段，基于 Example JSON 推断其存在性",
                verifyPoint = "待 DJI 文档补充或真机验证字段是否必填、推送时机"
        )
        AiWaylineState aiWaylineState
) {

    /**
     * AI 跟随状态与原因。
     *
     * @param state       跟随状态（{@link AiTrackState}）：0=空闲, 1=等待选择, 2=等待确认, 3=跟随中
     * @param stateReason 状态原因（{@link AiTrackStateReason}）：0-15 正常原因, 160-168 退出原因
     */
    public record SpotlightZoomState(
            AiTrackState state,
            AiTrackStateReason stateReason
    ) {}

    /**
     * AI 模型信息（{@code ai_model_list} 数组元素）。
     *
     * @param index      模型编号（min=0；DJI 内置模型 index&lt;128，三方模型 index≥128）
     * @param signedName 模型签名名称
     */
    public record AiModelInfo(
            Integer index,
            String signedName
    ) {}

    /**
     * 当前选中模型信息。
     *
     * @param index         当前选中的模型编号
     * @param score         当前识别结果的置信度，范围 [0, 100]
     * @param scoreMode     置信度模式（{@link AiScoreMode}）
     * @param imageSource   模型支持的码流（enum_list 多选，{@link AiImageSource} 值域）
     * @param digitalEffect 模型支持的红外调色板类型（enum_list 多选，{@link AiDigitalEffect} 值域）
     * @param filters       当前目标过滤列表（128 偏移规则同 {@link AiIdentifyFilterSetRequest}）
     * @param labels        模型支持识别的标签集合
     */
    public record SelectedAiModel(
            Integer index,
            Integer score,
            AiScoreMode scoreMode,
            List<Integer> imageSource,
            List<Integer> digitalEffect,
            List<Integer> filters,
            List<AiLabel> labels
    ) {}

    /**
     * 识别标签（{@code labels} 数组元素）。
     *
     * @param index 标签唯一标识符（min=0）
     * @param name  标签名称
     */
    public record AiLabel(
            Integer index,
            String name
    ) {}

    /**
     * 航线 AI 状态。
     *
     * <p><b>注意</b>：DJI 文档 Data 表中未列出 {@code ai_wayline_state} 字段，
     * 但 {@code drc_ai_info_push} Example JSON 中明确存在。字段类型基于 Example 推断。
     *
     * @param sequenceShot  连拍
     * @param waitControl   等待控制
     * @param record        录制
     * @param normalShot    单拍
     * @param countDownTime 倒计时时间
     * @param alertUuid     告警 UUID
     */
    @Inferred(
            reason = "DJI 文档 drc_ai_info_push Data 表未列出 ai_wayline_state，基于 Example JSON 推断字段名与类型",
            verifyPoint = "待 DJI 文档补充或真机验证字段名、类型、是否必填、业务含义"
    )
    public record AiWaylineState(
            Boolean sequenceShot,
            Boolean waitControl,
            Boolean record,
            Boolean normalShot,
            Integer countDownTime,
            String alertUuid
    ) {}
}
