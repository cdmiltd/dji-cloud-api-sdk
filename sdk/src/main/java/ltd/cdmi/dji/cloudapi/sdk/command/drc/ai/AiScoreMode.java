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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI AI 识别置信度模式枚举（{@code score_mode} 字段）。
 *
 * <p>用于 {@code drc_ai_identify_score_mode_set}（设置置信度模式）以及
 * {@code drc_ai_info_push} 推送中 {@code selected_ai_model.score_mode} 字段。
 *
 * <p>计数模式和搜救模式下系统自动适配置信度阈值，用户无法调整；
 * 自定义模式下可由业务侧通过 {@code drc_ai_identify_score_set} 设置具体阈值（0–100）。
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue} 与 {@link JsonCreator} 实现 int 值与枚举双向绑定。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - 设置 AI 识别置信度模式</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_identify_score_mode_set / drc_ai_info_push Data 表 score_mode 枚举值 {0:无效值, 1:计数模式, 2:搜救模式, 3:用户自定义模式}")
public enum AiScoreMode {

    INVALID(0, "无效值"),
    COUNT(1, "计数模式"),
    SEARCH_RESCUE(2, "搜救模式"),
    CUSTOM(3, "用户自定义模式");

    private static final Map<Integer, AiScoreMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiScoreMode::code, Function.identity()));

    private final int code;
    private final String description;

    AiScoreMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiScoreMode fromCode(int code) {
        AiScoreMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 AI 置信度模式: " + code);
        }
        return m;
    }
}
