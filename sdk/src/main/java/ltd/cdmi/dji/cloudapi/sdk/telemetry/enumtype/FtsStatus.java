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
 * DJI 快传（FTS）状态（fts_info.fts_status）。
 *
 * <p>fts_status 表示飞行器快传（File Transfer System）当前状态，出现在飞行器 OSD 的
 * fts_info 结构体中（M4D/M4TD，pushMode=0）。注意值域不连续：0-4 为正常状态，99 为未知错误。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html">
 * DJI M4D/M4TD 设备属性 fts_info.fts_status 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI M4D/M4TD 设备属性页 fts_info.fts_status 枚举定义（0-4, 99）")
public enum FtsStatus {

    /** 未连接 */
    DISCONNECTED(0, "未连接"),

    /** 已就绪 */
    READY(1, "已就绪"),

    /** 正在连接服务器 */
    CONNECTING_SERVER(2, "正在连接服务器"),

    /** 已触发停桨 */
    PROPELLER_STOP_TRIGGERED(3, "已触发停桨"),

    /** 未连接 dongle */
    DONGLE_DISCONNECTED(4, "未连接 dongle"),

    /** 未知错误 */
    UNKNOWN_ERROR(99, "未知错误");

    private static final Map<Integer, FtsStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(FtsStatus::code, Function.identity()));

    private final int code;
    private final String description;

    FtsStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    /**
     * 按状态码反查枚举。
     *
     * @param code fts_status 状态码（0-4, 99）
     * @return 对应枚举值
     * @throws IllegalArgumentException 状态码未知
     */
    public static FtsStatus fromCode(int code) {
        FtsStatus s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 fts_status: " + code);
        }
        return s;
    }
}
