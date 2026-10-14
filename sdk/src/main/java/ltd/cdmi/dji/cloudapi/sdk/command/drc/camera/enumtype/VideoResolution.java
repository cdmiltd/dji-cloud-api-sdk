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
 * DRC 视频分辨率枚举（{@code drc_video_resolution_set.video_resolution}）。
 *
 * <p><b>文档矛盾</b>：Data 表 Type 标 {@code enum_string}（{@code "0":"19201080"/"1":"38402160"}），
 * 但 Example 为数字 {@code "video_resolution": 0}（A80 第三次复现）。按 {@code Integer} 承载
 * （{@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.DrcVideoResolutionSetRequest}），本枚举供值域参考。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_video_resolution_set video_resolution 值域 {0:19201080(FHD), 1:38402160(UHD)}")
@Inferred(
        reason = "文档 Data 表 Type 标 enum_string 而 Example 为数字，未明确 wire 承载类型",
        verifyPoint = "真机验证 drc_video_resolution_set 的 video_resolution 实际 wire 类型（字符串 or 数字）"
)
public enum VideoResolution {

    FHD_1080P(0, "19201080（FHD 1080P）"),
    UHD_4K(1, "38402160（UHD 4K）");

    private static final Map<Integer, VideoResolution> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(VideoResolution::code, Function.identity()));

    private final int code;
    private final String description;

    VideoResolution(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VideoResolution fromCode(int code) {
        VideoResolution v = BY_CODE.get(code);
        if (v == null) {
            throw new IllegalArgumentException("未知的 video_resolution: " + code);
        }
        return v;
    }
}
