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
 * 相机对焦模式（{@code drc_camera_focus_mode_set.focus_mode}，enum_int）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_focus_mode_set focus_mode enum_int {0:MF, 1:AFS, 2:AFC}")
public enum FocusMode {

    MF(0, "手动对焦"),
    AFS(1, "单次自动对焦"),
    AFC(2, "连续自动对焦");

    private static final Map<Integer, FocusMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(FocusMode::code, Function.identity()));

    private final int code;
    private final String description;

    FocusMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FocusMode fromCode(int code) {
        FocusMode f = BY_CODE.get(code);
        if (f == null) {
            throw new IllegalArgumentException("未知的 focus_mode: " + code);
        }
        return f;
    }
}
