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
 * DRC 照片分辨率枚举（{@code drc_camera_photo_size_set.photo_size}）。
 *
 * <p>值域依据 DJI 文档（Dock2/Dock3 统一，无机型差异）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_photo_size_set photo_size 值域 {0:R4K, 4:R8K}")
public enum PhotoResolution {

    R4K(0, "4K 分辨率"),
    R8K(4, "8K 分辨率");

    private static final Map<Integer, PhotoResolution> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PhotoResolution::code, Function.identity()));

    private final int code;
    private final String description;

    PhotoResolution(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PhotoResolution fromCode(int code) {
        PhotoResolution p = BY_CODE.get(code);
        if (p == null) {
            throw new IllegalArgumentException("未知的 photo_size: " + code);
        }
        return p;
    }
}
