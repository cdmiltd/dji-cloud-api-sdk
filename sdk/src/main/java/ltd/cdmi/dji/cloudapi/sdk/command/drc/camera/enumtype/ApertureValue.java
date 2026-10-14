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
 * 相机光圈值（{@code drc_camera_aperture_value_set.aperture_value}，enum_int）。
 *
 * <p>编码规则：光圈值 = F 值 × 100（如 {@code 280}=F2.8、{@code 440}=F4.4、{@code 25600}=F256），
 * {@code 0}=F_AUTO。与 {@code drc_camera_osd_info_push.wide_aperture_value} 值域一致，可共享。
 *
 * <p><b>标 @Inferred</b>：文档值域为 60+ 档，报告仅给出锚点，完整档位待补。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_camera_aperture_value_set aperture_value enum_int（0=F_AUTO, 50=F0.5, 70=F0.7, 280=F2.8, 440=F4.4, 25600=F256）")
@Inferred(
        reason = "文档光圈值域为 60+ 档，报告仅摘录锚点，未给出完整档位表",
        verifyPoint = "按 DJI 文档原文补全 60+ 档完整光圈枚举值域"
)
public enum ApertureValue {

    F_AUTO(0, "自动"),
    F0_5(50, "F0.5"),
    F0_7(70, "F0.7"),
    F2_8(280, "F2.8"),
    F4_4(440, "F4.4"),
    F256(25600, "F256");

    private static final Map<Integer, ApertureValue> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(ApertureValue::code, Function.identity()));

    private final int code;
    private final String description;

    ApertureValue(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApertureValue fromCode(int code) {
        ApertureValue a = BY_CODE.get(code);
        if (a == null) {
            throw new IllegalArgumentException("未知的 aperture_value: " + code);
        }
        return a;
    }
}
