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
 * 通用开关状态（enum_int 0/1）。
 *
 * <p>供夜航灯 {@code drc_night_lights_state_set.night_lights_state}、
 * 隐蔽模式 {@code drc_stealth_state_set.stealth_state}、
 * 机械快门 {@code drc_camera_mechanical_shutter_set.mechanical_shutter_state}
 * 等 0/1 开关型字段统一表达。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 0/1 开关型字段（night_lights_state / stealth_state / mechanical_shutter_state）")
public enum SwitchState {

    CLOSE(0, "关闭"),
    OPEN(1, "开启");

    private static final Map<Integer, SwitchState> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(SwitchState::code, Function.identity()));

    private final int code;
    private final String description;

    SwitchState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SwitchState fromCode(int code) {
        SwitchState s = BY_CODE.get(code);
        if (s == null) {
            throw new IllegalArgumentException("未知的 switch_state: " + code);
        }
        return s;
    }
}
