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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 相机照片格式（photo_format）。
 *
 * <p>photo_format 标识相机拍摄的照片保存格式，v1.16.1 新增，用于支持红外照片保存为 DLT-664 格式。
 * 仅 Dock3 带 IR 相机时推送，旧固件可能不含此字段（反序列化为 null）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_camera_state_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "DJI Dock3 DRC drc_camera_state_push photo_format 枚举定义（7=RJPEG, 16=DLT664）")
public enum PhotoFormat {

    RJPEG(7, "常规 JPEG（含红外元数据）"),
    DLT664(16, "DLT-664 红外照片格式（DJI 私有格式）");

    private static final Map<Integer, PhotoFormat> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(PhotoFormat::code, Function.identity()));

    private final int code;
    private final String description;

    PhotoFormat(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static PhotoFormat fromCode(int code) {
        PhotoFormat m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 photo_format: " + code);
        }
        return m;
    }
}