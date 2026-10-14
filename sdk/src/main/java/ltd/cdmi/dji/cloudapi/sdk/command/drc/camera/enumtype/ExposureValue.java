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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 相机曝光值（{@code drc_camera_exposure_set.exposure_value}）。
 *
 * <p>文档键 1–31 对应 {@code -5.0EV} … {@code 5.0EV}，255 为 {@code FIXED}；A14 标的该字段为 enum_int。
 *
 * <p><b>标 @Inferred</b>：报告未列出 1–31 各档 EV 映射，且承载类型（enum_string vs enum_int）
 * 存在文档矛盾。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_exposure_set exposure_value 值域 1–31（-5.0EV…5.0EV）+ 255(FIXED)")
@Inferred(
        reason = "文档 1–31 各档 EV 映射未逐项列出，且 Data 表 Type(enum_string) 与 Example(数字) 矛盾",
        verifyPoint = "按 DJI 文档原文补全 1–31 曝光补偿档位映射并确认承载类型"
)
public enum ExposureValue {

    FIXED(255, "固定（不调整）");

    private static final Map<Integer, ExposureValue> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(ExposureValue::code, Function.identity()));

    private final int code;
    private final String description;

    ExposureValue(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExposureValue fromCode(int code) {
        ExposureValue e = BY_CODE.get(code);
        if (e == null) {
            throw new IllegalArgumentException("未知的 exposure_value: " + code);
        }
        return e;
    }
}
