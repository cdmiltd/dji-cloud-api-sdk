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
 * 夜景模式（{@code drc_camera_night_mode_set.mode}，enum_int）。
 *
 * <p>注意：与 {@code drc_ir_metering_mode_set.mode} 值域不同，不可共用。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_night_mode_set mode enum_int {0:关闭, 1:开启, 2:自动}")
public enum NightMode {

    CLOSE(0, "关闭"),
    OPEN(1, "开启"),
    AUTO(2, "自动");

    private static final Map<Integer, NightMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(NightMode::code, Function.identity()));

    private final int code;
    private final String description;

    NightMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static NightMode fromCode(int code) {
        NightMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 night mode: " + code);
        }
        return m;
    }
}
