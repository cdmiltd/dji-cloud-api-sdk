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
 * DJI AI 跟随状态枚举（{@code ai_spotlight_zoom.state} 字段）。
 *
 * <p>用于 {@code drc_ai_info_push} 推送中 {@code ai_spotlight_zoom.state} 字段，
 * 标识当前 AI 跟随所处的阶段。
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue} 与 {@link JsonCreator} 实现 int 值与枚举双向绑定。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 状态上报</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_info_push Data 表 ai_spotlight_zoom.state 枚举值 {0:空闲, 1:等待选择, 2:等待确认, 3:跟随中}")
public enum AiTrackState {

    IDLE(0, "空闲"),
    WAITING_SELECT(1, "等待选择"),
    WAITING_CONFIRM(2, "等待确认"),
    TRACKING(3, "跟随中");

    private static final Map<Integer, AiTrackState> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiTrackState::code, Function.identity()));

    private final int code;
    private final String description;

    AiTrackState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiTrackState fromCode(int code) {
        AiTrackState s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 AI 跟随状态: " + code);
        }
        return s;
    }
}
