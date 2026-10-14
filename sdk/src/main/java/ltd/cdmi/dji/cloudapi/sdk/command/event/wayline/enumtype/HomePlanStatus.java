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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code multi_dock_home_info.plan_status}（enum_int）。
 *
 * <p>蛙跳场景下多机场返航规划状态。协议 record 中该字段保持 {@code Integer}，
 * 本枚举提供反查/描述助手。命名避开与 {@code FlighttaskProgressStatus} 的歧义。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock2/Dock3 wayline.html multi_dock_home_info.plan_status enum_int 4 值 {0,1,2,3}")
public enum HomePlanStatus {

    /** 规划失败或正在规划中 */
    PLANNING_OR_FAILED(0, "规划失败或正在规划中"),
    /** 规划路径不可达 */
    PATH_UNREACHABLE(1, "规划路径不可达"),
    /** 规划路径因电量不可达 */
    PATH_UNREACHABLE_LOW_BATTERY(2, "规划路径因电量不可达"),
    /** 目标可达 */
    TARGET_REACHABLE(3, "目标可达");

    private static final Map<Integer, HomePlanStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(HomePlanStatus::code, Function.identity()));

    private final int code;
    private final String description;

    HomePlanStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }

    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static HomePlanStatus fromCode(int code) {
        HomePlanStatus s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 plan_status: " + code);
        }
        return s;
    }
}
