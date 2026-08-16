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
 * DJI 直播码流质量（video_quality）。
 *
 * <p>video_quality 表示直播码流的质量等级，出现在机场 State 的 live_capacity 结构体中。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 video_quality 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html video_quality 枚举定义（0-4）")
public enum VideoQuality {

    AUTO(0, "自适应"),
    SMOOTH(1, "流畅"),
    STANDARD(2, "标清"),
    HIGH(3, "高清"),
    ULTRA(4, "超清");

    private static final Map<Integer, VideoQuality> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(VideoQuality::code, Function.identity()));

    private final int code;
    private final String description;

    VideoQuality(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static VideoQuality fromCode(int code) {
        VideoQuality q = BY_CODE.get(code);
        if (q == null) {
            throw new IllegalArgumentException("未知的 video_quality: " + code);
        }
        return q;
    }
}
