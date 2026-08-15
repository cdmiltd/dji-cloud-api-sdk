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
 * 验证 {@link AirConditionerState} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 16 个 air_conditioner_state 数值（0-15）能通过
 *       {@link AirConditionerState#fromCode(int)} 反查</li>
 *   <li>-1 / 16 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 *   <li>状态序列完整：空闲 → 准备模式 → 工作模式 → 退出模式 → 空闲</li>
 * </ol>
 */
class AirConditionerStateTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 16 个数值（0-15）")
    void testFromCodeAllValues() {
        assertEquals(AirConditionerState.IDLE, AirConditionerState.fromCode(0));
        assertEquals(AirConditionerState.COOLING, AirConditionerState.fromCode(1));
        assertEquals(AirConditionerState.HEATING, AirConditionerState.fromCode(2));
        assertEquals(AirConditionerState.DEHUMIDIFICATION, AirConditionerState.fromCode(3));
        assertEquals(AirConditionerState.COOLING_EXIT, AirConditionerState.fromCode(4));
        assertEquals(AirConditionerState.HEATING_EXIT, AirConditionerState.fromCode(5));
        assertEquals(AirConditionerState.DEHUMIDIFICATION_EXIT, AirConditionerState.fromCode(6));
        assertEquals(AirConditionerState.COOLING_READY, AirConditionerState.fromCode(7));
        assertEquals(AirConditionerState.HEATING_READY, AirConditionerState.fromCode(8));
        assertEquals(AirConditionerState.DEHUMIDIFICATION_READY, AirConditionerState.fromCode(9));
        assertEquals(AirConditionerState.AIR_COOLING_PREPARING, AirConditionerState.fromCode(10));
        assertEquals(AirConditionerState.AIR_COOLING_IN_PROGRESS, AirConditionerState.fromCode(11));
        assertEquals(AirConditionerState.AIR_COOLING_EXITING, AirConditionerState.fromCode(12));
        assertEquals(AirConditionerState.DEFOGGER_PREPARING, AirConditionerState.fromCode(13));
        assertEquals(AirConditionerState.DEFOGGER_IN_PROGRESS, AirConditionerState.fromCode(14));
        assertEquals(AirConditionerState.DEFOGGER_EXITING, AirConditionerState.fromCode(15));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> AirConditionerState.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> AirConditionerState.fromCode(16));
        assertThrows(IllegalArgumentException.class, () -> AirConditionerState.fromCode(17));
        assertThrows(IllegalArgumentException.class, () -> AirConditionerState.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 16（空闲 + 制冷/制热/除湿 + 退出 + 准备 + 风冷 + 除雾）")
    void testTotalCount() {
        assertEquals(16, AirConditionerState.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0-15 → IDLE-DEFOGGER_EXITING")
    void testCodeRoundTrip() {
        assertEquals(0, AirConditionerState.IDLE.code());
        assertEquals(15, AirConditionerState.DEFOGGER_EXITING.code());
        // 双向闭环
        for (AirConditionerState s : AirConditionerState.values()) {
            assertEquals(s, AirConditionerState.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("空闲模式", AirConditionerState.IDLE.description());
        assertEquals("制冷模式", AirConditionerState.COOLING.description());
        assertEquals("制热模式", AirConditionerState.HEATING.description());
        assertEquals("除湿模式", AirConditionerState.DEHUMIDIFICATION.description());
        assertEquals("制冷退出模式", AirConditionerState.COOLING_EXIT.description());
        assertEquals("制冷准备模式", AirConditionerState.COOLING_READY.description());
        assertEquals("风冷准备中", AirConditionerState.AIR_COOLING_PREPARING.description());
        assertEquals("除雾退出中", AirConditionerState.DEFOGGER_EXITING.description());
        for (AirConditionerState s : AirConditionerState.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
