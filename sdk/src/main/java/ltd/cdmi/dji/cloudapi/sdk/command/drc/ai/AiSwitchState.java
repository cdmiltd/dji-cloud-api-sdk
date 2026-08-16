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
 * DJI AI 功能开关枚举（{@code on} / {@code identify_on} / {@code spotlight_zoom_on} 字段）。
 *
 * <p>用于 {@code drc_ai_identify_set}（识别开关）、{@code drc_ai_spotlight_zoom_set}（跟随开关）
 * 以及 {@code drc_ai_info_push} 推送中的 {@code identify_on} 与 {@code spotlight_zoom_on} 字段。
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue}（序列化：枚举 → int code）与
 * {@link JsonCreator}（反序列化：int code → 枚举）实现 DJI 协议 int 值与枚举类型的双向绑定。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 识别开关设置</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_identify_set / drc_ai_spotlight_zoom_set / drc_ai_info_push Data 表 on/identify_on/spotlight_zoom_on 枚举值 {0:关闭, 1:开启}")
public enum AiSwitchState {

    OFF(0, "关闭"),
    ON(1, "开启");

    private static final Map<Integer, AiSwitchState> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiSwitchState::code, Function.identity()));

    private final int code;
    private final String description;

    AiSwitchState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiSwitchState fromCode(int code) {
        AiSwitchState s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 AI 开关状态: " + code);
        }
        return s;
    }
}
