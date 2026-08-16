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
 * DJI AI 模型支持的红外调色板模式枚举（{@code digital_effect} 字段，enum_list 多选）。
 *
 * <p>用于 {@code drc_ai_info_push} 推送中 {@code selected_ai_model.digital_effect} 字段，
 * 标识当前选中模型支持的红外调色板类型。
 *
 * <p><b>注意</b>：DJI 文档类型为 {@code enum_list}（多选枚举），JSON 中传输为 int 数组
 * （如 {@code [0, 1, 2]}）。本枚举作为数组元素的值域定义，POJO 字段类型使用 {@code List<Integer>}
 * 而非 {@code List<AiDigitalEffect>}：{@code drc_ai_info_push} 是设备→云推送，SDK 须容忍比
 * SDK 发布更新的固件版本。若固件新增枚举值（如 {@code digital_effect=3}）而 SDK 未收录，
 * {@code List<Enum>} 会因 {@link #fromCode(int)} 抛 {@link IllegalArgumentException}
 * 导致整条消息反序列化失败；{@code List<Integer>} 保留未知值，仅影响枚举语义层。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 状态上报</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_info_push Data 表 digital_effect 枚举值 {0:白热, 1:黑热, 2:红热}")
public enum AiDigitalEffect {

    WHITE_HOT(0, "白热"),
    BLACK_HOT(1, "黑热"),
    RED_HOT(2, "红热");

    private static final Map<Integer, AiDigitalEffect> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiDigitalEffect::code, Function.identity()));

    private final int code;
    private final String description;

    AiDigitalEffect(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiDigitalEffect fromCode(int code) {
        AiDigitalEffect e = BY_CODE.get(code);
        if (e == null) {
            throw new IllegalArgumentException("未知的 AI 调色模式: " + code);
        }
        return e;
    }
}
