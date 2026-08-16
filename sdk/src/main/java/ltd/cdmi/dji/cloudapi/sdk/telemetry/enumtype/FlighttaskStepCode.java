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
 * DJI 机场任务状态（flighttask_step_code）。
 *
 * <p>flighttask_step_code 表示机场在航线任务执行流程中的当前阶段，
 * 出现在机场 OSD 中（{@code thing/product/{device_sn}/osd}）。
 * 与飞行器的 {@link DroneModeCode}（mode_code）不同，
 * flighttask_step_code 反映的是<span style="color:var(--muted)">机场侧</span>的任务执行阶段。
 *
 * <p>注意值域不连续：0-5 为正常阶段，255 和 256 为异常/未知状态。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html">
 * DJI Dock3 设备属性 flighttask_step_code 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/properties.html")
@Verified(basis = "DJI Dock3 properties.html flighttask_step_code 枚举定义（0-5, 255, 256）")
public enum FlighttaskStepCode {

    /** 作业准备中 */
    TASK_PREPARING(0, "作业准备中"),

    /** 飞行作业中 */
    TASK_OPERATING(1, "飞行作业中"),

    /** 作业后状态恢复 */
    STATE_RECOVERING(2, "作业后状态恢复"),

    /** 自定义飞行区更新中 */
    CUSTOM_FLIGHT_AREA_UPDATING(3, "自定义飞行区更新中"),

    /** 地形障碍物更新中 */
    TERRAIN_OBSTACLES_UPDATING(4, "地形障碍物更新中"),

    /** 任务空闲 */
    IDLE(5, "任务空闲"),

    /** 飞行器异常 */
    AIRCRAFT_ABNORMAL(255, "飞行器异常"),

    /** 未知状态 */
    UNKNOWN(256, "未知状态");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<Integer, FlighttaskStepCode> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(FlighttaskStepCode::code, Function.identity()));

    private final int code;
    private final String description;

    FlighttaskStepCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回任务状态码数值。
     *
     * @return 状态码，如 {@code 1} 表示飞行作业中
     */
    public int code() {
        return code;
    }

    /**
     * 返回任务状态码的中文描述。
     *
     * @return 描述文本，如 {@code "飞行作业中"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据任务状态码数值查找对应的枚举值。
     *
     * @param code 状态码数值，如 {@code 1}
     * @return 对应的 {@link FlighttaskStepCode} 枚举值
     * @throws IllegalArgumentException 如果状态码不存在于已知枚举中
     */
    public static FlighttaskStepCode fromCode(int code) {
        FlighttaskStepCode step = BY_CODE.get(code);
        if (step == null) {
            throw new IllegalArgumentException("未知的 flighttask_step_code: " + code);
        }
        return step;
    }
}
