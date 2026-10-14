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
 * 定时拍间隔（{@code drc_interval_photo_set.interval}）。
 *
 * <p>文档为 {@code enum_string}，键含小数 {@code "0.7"}（0.7/1/2/3/5/7/10/15/20/30/60 共 11 档，秒）。
 * 键类型在 Dock2（enum_string）与 Dock3（int）页口径不一 → 请求字段按 {@code Double} 承载
 * （见 {@link ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.DrcIntervalPhotoSetRequest}），本枚举供值域参考。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drc_interval_photo_set interval enum_string {0.7,1,2,3,5,7,10,15,20,30,60}（秒）")
@Inferred(
        reason = "interval 键类型在 Dock2(enum_string)/Dock3(int) 两页口径不一，wire 承载类型未统一确认",
        verifyPoint = "真机验证 drc_interval_photo_set.interval 的 wire 承载类型"
)
public enum IntervalPhoto {

    S0_7("0.7", "0.7 秒"),
    S1("1", "1 秒"),
    S2("2", "2 秒"),
    S3("3", "3 秒"),
    S5("5", "5 秒"),
    S7("7", "7 秒"),
    S10("10", "10 秒"),
    S15("15", "15 秒"),
    S20("20", "20 秒"),
    S30("30", "30 秒"),
    S60("60", "60 秒");

    private static final Map<String, IntervalPhoto> BY_VALUE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(IntervalPhoto::value, Function.identity()));

    private final String value;
    private final String description;

    IntervalPhoto(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String value() { return value; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static IntervalPhoto fromValue(String value) {
        IntervalPhoto i = BY_VALUE.get(value);
        if (i == null) {
            throw new IllegalArgumentException("未知的 interval: " + value);
        }
        return i;
    }
}
