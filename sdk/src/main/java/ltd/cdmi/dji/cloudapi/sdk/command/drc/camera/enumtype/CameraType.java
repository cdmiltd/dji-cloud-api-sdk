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
 * DRC 相机类型枚举（{@code camera_type}）。
 *
 * <p>文档：{@code drc_camera_aim} 支持 {@code ir/wide/zoom}；{@code drc_camera_aperture_value_set}
 * 支持 {@code wide/zoom}；{@code drc_camera_dewarping_set} 仅 {@code wide}。承载为 {@code enum_string}。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 camera_type enum_string {wide, zoom, ir}")
public enum CameraType {

    WIDE("wide", "广角相机"),
    ZOOM("zoom", "变焦相机"),
    IR("ir", "红外相机");

    private static final Map<String, CameraType> BY_VALUE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(CameraType::value, Function.identity()));

    private final String value;
    private final String description;

    CameraType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String value() { return value; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CameraType fromValue(String value) {
        CameraType c = BY_VALUE.get(value);
        if (c == null) {
            throw new IllegalArgumentException("未知的 camera_type: " + value);
        }
        return c;
    }
}
