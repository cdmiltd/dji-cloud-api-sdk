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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 机场电池存储模式（battery_store_mode）。
 *
 * <p>battery_store_mode 表示机场电池在空闲时的电量保持策略，出现在机场 OSD 中
 * （{@link ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#BATTERY_STORE_MODE}）。
 * DJI 文档将该字段命名为"电池运行模式"，本质上是电池空闲状态下的存储策略。
 *
 * <p><b>注意枚举值从 1 开始，没有 0 值</b>。0 不属于合法枚举值，反查会抛
 * {@link IllegalArgumentException}。
 *
 * <p>两种模式的差异（DJI 文档原文）：
 * <ul>
 *   <li>{@link #PLANNING} 计划模式：适合规律作业场景，无任务时电池电量保持在 55%~60%，电池寿命较长</li>
 *   <li>{@link #STANDBY} 待命模式：适合应急作业场景，无任务时电池电量保持在 90%~95%，电池寿命较短</li>
 * </ul>
 *
 * <p><b>Jackson 绑定</b>：本枚举是 SDK 中首个用作 POJO 字段类型的遥测枚举。通过
 * {@link JsonValue}（序列化：枚举 → int code）与 {@link JsonCreator}（反序列化：
 * int code → 枚举）实现 DJI 协议 int 值与枚举类型的双向绑定，可作为 record 字段
 * 类型直接使用，如 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryStoreModeSwitchRequest#mode()}。
 * 其他 enumtype 枚举（如 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.DroneModeCode}）
 * 仅作为 Javadoc 引用，未做 Jackson 绑定，因 DJI OSD record 中对应字段仍用 {@code Integer}
 * 以兼容不同机型字段集差异；而本枚举对应字段为枚举值固定的 services 请求参数，可直接类型化。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">DJI Dock3 机场设备属性</a>（battery_store_mode 枚举定义）
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.OsdField#BATTERY_STORE_MODE
 * @see ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryStoreModeSwitchRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Cloud API 官方文档机场 battery_store_mode 枚举定义（1=计划模式, 2=待命模式，Dock3 properties 文档明确列出）")
public enum BatteryStoreMode {

    /** 计划模式：适合规律作业场景，无任务时电池电量保持在 55%~60%，电池寿命较长 */
    PLANNING(1, "计划模式"),

    /** 待命模式：适合应急作业场景，无任务时电池电量保持在 90%~95%，电池寿命较短 */
    STANDBY(2, "待命模式");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, BatteryStoreMode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(BatteryStoreMode::code, Function.identity()));

    private final int code;
    private final String description;

    BatteryStoreMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回枚举码数值。
     *
     * <p>同时作为 Jackson 序列化入口（{@link JsonValue}）：枚举 → int code，
     * 用于将 POJO 字段序列化为 DJI 协议的 int 值（如 {@code "mode": 1}）。
     *
     * @return 枚举码，{@code 1} 表示计划模式，{@code 2} 表示待命模式
     */
    @JsonValue
    public int code() {
        return code;
    }

    /**
     * 返回枚举码的中文描述。
     *
     * @return 描述文本，如 {@code "计划模式"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据枚举码数值查找对应的枚举值。
     *
     * <p>同时作为 Jackson 反序列化入口（{@link JsonCreator}）：int code → 枚举，
     * 用于将 DJI 协议的 int 值（如 {@code "mode": 1}）反序列化为枚举类型。
     *
     * <p><b>注意</b>：合法 code 为 1 和 2，0 不属于合法枚举值。
     *
     * @param code 枚举码数值，{@code 1} 或 {@code 2}
     * @return 对应的 {@link BatteryStoreMode} 枚举值
     * @throws IllegalArgumentException 如果枚举码不存在于已知枚举中（包括 {@code 0}）
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BatteryStoreMode fromCode(int code) {
        BatteryStoreMode mode = BY_CODE.get(code);
        if (mode == null) {
            throw new IllegalArgumentException("未知的 battery_store_mode: " + code);
        }
        return mode;
    }
}

