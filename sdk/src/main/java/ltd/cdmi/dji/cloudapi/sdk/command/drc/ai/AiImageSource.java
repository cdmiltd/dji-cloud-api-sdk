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
 * DJI AI 模型支持的图像源枚举（{@code image_source} 字段，enum_list 多选）。
 *
 * <p>用于 {@code drc_ai_info_push} 推送中 {@code selected_ai_model.image_source} 字段，
 * 标识当前选中模型支持的直播码流类型。
 *
 * <p><b>注意</b>：DJI 文档类型为 {@code enum_list}（多选枚举），JSON 中传输为 int 数组
 * （如 {@code [1, 2, 3]}）。本枚举作为数组元素的值域定义，POJO 字段类型使用 {@code List<Integer>}
 * 而非 {@code List<AiImageSource>}：{@code drc_ai_info_push} 是设备→云推送，SDK 须容忍比
 * SDK 发布更新的固件版本。若固件新增枚举值（如 {@code image_source=8}）而 SDK 未收录，
 * {@code List<Enum>} 会因 {@link #fromCode(int)} 抛 {@link IllegalArgumentException}
 * 导致整条消息反序列化失败；{@code List<Integer>} 保留未知值，仅影响枚举语义层。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制 - AI 状态上报</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI v1.16 remote-control 文档 drc_ai_info_push Data 表 image_source 枚举值 {1:广角, 2:变焦, 3:红外, 7:可见光}")
public enum AiImageSource {

    WIDE(1, "广角"),
    ZOOM(2, "变焦"),
    IR(3, "红外"),
    VISIBLE_LIGHT(7, "可见光");

    private static final Map<Integer, AiImageSource> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(AiImageSource::code, Function.identity()));

    private final int code;
    private final String description;

    AiImageSource(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AiImageSource fromCode(int code) {
        AiImageSource s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 AI 图像源: " + code);
        }
        return s;
    }
}
