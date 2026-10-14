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
 * {@code return_home_info.last_point_type}（enum_int）。
 *
 * <p>标识返航规划轨迹末点相对返航点的位置，用于平台侧返航进度显示判定。
 * 文档字段类型为 {@code enum_int}，协议 record 中该字段保持 {@code Integer}，
 * 本枚举提供反查/描述助手（见 AGENTS §6.5「enum_int → 反查枚举助手 + 字段保持 Integer」惯例）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock2/Dock3 wayline.html return_home_info.last_point_type enum_int 2 值 {0,1}")
public enum LastPointType {

    /** 最后一点在返航点上空 */
    ABOVE_HOME(0, "最后一点在返航点上空"),
    /** 不在返航点上空 */
    NOT_ABOVE_HOME(1, "不在返航点上空");

    private static final Map<Integer, LastPointType> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(LastPointType::code, Function.identity()));

    private final int code;
    private final String description;

    LastPointType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }

    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LastPointType fromCode(int code) {
        LastPointType t = BY_CODE.get(code);
        if (t == null) {
            throw new IllegalArgumentException("未知的 last_point_type: " + code);
        }
        return t;
    }
}
