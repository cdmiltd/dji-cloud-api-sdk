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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskStepCode} 枚举的不连续 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 8 个 flighttask_step_code 数值
 *       （0-5 为正常阶段, 255/256 为异常/未知状态）能通过 {@link FlighttaskStepCode#fromCode(int)} 反查</li>
 *   <li><strong>值域不连续</strong>：6-254（大段空隙）与 -1/257 均抛出
 *       {@link IllegalArgumentException}，确保中间空隙不混入</li>
 * </ol>
 */
class FlighttaskStepCodeTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 8 个数值（0-5 连续, 255/256 跳跃）— 值域不连续")
    void testFromCodeAllValues() {
        assertEquals(FlighttaskStepCode.TASK_PREPARING, FlighttaskStepCode.fromCode(0));
        assertEquals(FlighttaskStepCode.TASK_OPERATING, FlighttaskStepCode.fromCode(1));
        assertEquals(FlighttaskStepCode.STATE_RECOVERING, FlighttaskStepCode.fromCode(2));
        assertEquals(FlighttaskStepCode.CUSTOM_FLIGHT_AREA_UPDATING, FlighttaskStepCode.fromCode(3));
        assertEquals(FlighttaskStepCode.TERRAIN_OBSTACLES_UPDATING, FlighttaskStepCode.fromCode(4));
        assertEquals(FlighttaskStepCode.IDLE, FlighttaskStepCode.fromCode(5));
        assertEquals(FlighttaskStepCode.AIRCRAFT_ABNORMAL, FlighttaskStepCode.fromCode(255));
        assertEquals(FlighttaskStepCode.UNKNOWN, FlighttaskStepCode.fromCode(256));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含大段空隙 6-254）")
    void testFromCodeUnknownThrows() {
        // 5 与 255 之间的大段空隙
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(6));
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(100));
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(254));
        // 255 与 256 之间的空隙（无值）
        // 256 之后的边界外
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(257));
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> FlighttaskStepCode.fromCode(-2));
    }

    @Test
    @DisplayName("枚举总数应为 8（6 正常阶段 + 2 异常/未知状态）")
    void testTotalCount() {
        assertEquals(8, FlighttaskStepCode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0-5→正常阶段, 255→AIRCRAFT_ABNORMAL, 256→UNKNOWN")
    void testCodeRoundTrip() {
        assertEquals(0, FlighttaskStepCode.TASK_PREPARING.code());
        assertEquals(1, FlighttaskStepCode.TASK_OPERATING.code());
        assertEquals(2, FlighttaskStepCode.STATE_RECOVERING.code());
        assertEquals(3, FlighttaskStepCode.CUSTOM_FLIGHT_AREA_UPDATING.code());
        assertEquals(4, FlighttaskStepCode.TERRAIN_OBSTACLES_UPDATING.code());
        assertEquals(5, FlighttaskStepCode.IDLE.code());
        assertEquals(255, FlighttaskStepCode.AIRCRAFT_ABNORMAL.code());
        assertEquals(256, FlighttaskStepCode.UNKNOWN.code());
        // 双向闭环
        for (FlighttaskStepCode s : FlighttaskStepCode.values()) {
            assertEquals(s, FlighttaskStepCode.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("作业准备中", FlighttaskStepCode.TASK_PREPARING.description());
        assertEquals("飞行作业中", FlighttaskStepCode.TASK_OPERATING.description());
        assertEquals("任务空闲", FlighttaskStepCode.IDLE.description());
        assertEquals("飞行器异常", FlighttaskStepCode.AIRCRAFT_ABNORMAL.description());
        assertEquals("未知状态", FlighttaskStepCode.UNKNOWN.description());
        for (FlighttaskStepCode s : FlighttaskStepCode.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
