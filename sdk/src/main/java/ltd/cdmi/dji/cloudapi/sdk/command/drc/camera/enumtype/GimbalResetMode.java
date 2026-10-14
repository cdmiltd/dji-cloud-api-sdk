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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 云台复位模式（{@code drc_gimbal_reset.reset_mode}，enum_int 0–3）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_gimbal_reset reset_mode enum_int {0:回中, 1:向下, 2:偏航回中, 3:俯仰向下}")
public enum GimbalResetMode {

    RETURN_CENTER(0, "回中"),
    DOWN(1, "向下"),
    YAW_RETURN_CENTER(2, "偏航回中"),
    PITCH_DOWN(3, "俯仰向下");

    private static final Map<Integer, GimbalResetMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(GimbalResetMode::code, Function.identity()));

    private final int code;
    private final String description;

    GimbalResetMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GimbalResetMode fromCode(int code) {
        GimbalResetMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 reset_mode: " + code);
        }
        return m;
    }
}
