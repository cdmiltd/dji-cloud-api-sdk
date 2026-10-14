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
 * 相机机械/电子快门值（{@code drc_camera_shutter_set.shutter_value}，enum_int 0–60）。
 *
 * <p>文档值域 {@code {0..59, 60:Auto}}，60 为自动。
 *
 * <p><b>标 @Inferred</b>：报告仅给出 {@code 60=Auto} 与范围，未列出 0–59 各档快门速度映射。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_shutter_set shutter_value enum_int 0–60（60=Auto）")
@Inferred(
        reason = "文档给出快门值范围 0–59 与 60=Auto，但未列出 0–59 各档快门速度映射",
        verifyPoint = "按 DJI 文档原文补全 0–59 快门速度档位映射"
)
public enum ShutterValue {

    AUTO(60, "自动");

    private static final Map<Integer, ShutterValue> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(ShutterValue::code, Function.identity()));

    private final int code;
    private final String description;

    ShutterValue(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ShutterValue fromCode(int code) {
        ShutterValue s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 shutter_value: " + code);
        }
        return s;
    }
}
