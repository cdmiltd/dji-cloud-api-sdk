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
 * 相机 ISO 值（{@code drc_camera_iso_set.iso_value}，enum_int）。
 *
 * <p>与 {@code drc_camera_osd_info_push.wide_iso} 的 index 2–11 映射一致；本表无 index 1
 * （{@code ISO_AUTO} = 0，直接接 {@code ISO_50} = 2），并新增 index 12/13（51200/102400）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_iso_set iso_value enum_int {0:ISO_AUTO,2:ISO_50,3:ISO_100,...,13:ISO_102400,255:ISO_FIXED}")
public enum IsoValue {

    ISO_AUTO(0, "自动"),
    ISO_50(2, "ISO 50"),
    ISO_100(3, "ISO 100"),
    ISO_200(4, "ISO 200"),
    ISO_400(5, "ISO 400"),
    ISO_800(6, "ISO 800"),
    ISO_1600(7, "ISO 1600"),
    ISO_3200(8, "ISO 3200"),
    ISO_6400(9, "ISO 6400"),
    ISO_12800(10, "ISO 12800"),
    ISO_25600(11, "ISO 25600"),
    ISO_51200(12, "ISO 51200"),
    ISO_102400(13, "ISO 102400"),
    ISO_FIXED(255, "固定");

    private static final Map<Integer, IsoValue> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(IsoValue::code, Function.identity()));

    private final int code;
    private final String description;

    IsoValue(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static IsoValue fromCode(int code) {
        IsoValue i = BY_CODE.get(code);
        if (i == null) {
            throw new IllegalArgumentException("未知的 iso_value: " + code);
        }
        return i;
    }
}
