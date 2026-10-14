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
 * 相机曝光模式（{@code drc_camera_exposure_mode_set.exposure_mode}，enum_int）。
 *
 * <p>与 {@code drc_camera_osd_info_push.wide_exposure_mode} 值域完全一致，可共享。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_exposure_mode_set exposure_mode enum_int {1:自动, 2:快门优先, 3:光圈优先, 4:手动}")
public enum ExposureMode {

    AUTO(1, "自动"),
    SHUTTER_PRIORITY(2, "快门优先"),
    APERTURE_PRIORITY(3, "光圈优先"),
    MANUAL(4, "手动");

    private static final Map<Integer, ExposureMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(ExposureMode::code, Function.identity()));

    private final int code;
    private final String description;

    ExposureMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExposureMode fromCode(int code) {
        ExposureMode e = BY_CODE.get(code);
        if (e == null) {
            throw new IllegalArgumentException("未知的 exposure_mode: " + code);
        }
        return e;
    }
}
