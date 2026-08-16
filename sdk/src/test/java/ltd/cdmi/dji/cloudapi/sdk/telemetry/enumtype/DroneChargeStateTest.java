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

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DroneChargeState} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI 机场 properties 文档定义的 2 个 drone_charge_state.state 数值（0-1）能通过
 *       {@link DroneChargeState#fromCode(int)} 反查</li>
 *   <li><strong>修复验证 #2</strong>：{@code fromCode} 方法存在且正常工作
 *       （早期 DroneChargeState 缺失 fromCode 方法，现已修复）</li>
 *   <li>-1 / 2 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class DroneChargeStateTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 2 个数值（0-1）")
    void testFromCodeAllValues() {
        assertEquals(DroneChargeState.IDLE, DroneChargeState.fromCode(0));
        assertEquals(DroneChargeState.CHARGING, DroneChargeState.fromCode(1));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> DroneChargeState.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> DroneChargeState.fromCode(2));
        assertThrows(IllegalArgumentException.class, () -> DroneChargeState.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> DroneChargeState.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 2（空闲 + 充电中）")
    void testTotalCount() {
        assertEquals(2, DroneChargeState.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→IDLE, 1→CHARGING")
    void testCodeRoundTrip() {
        assertEquals(0, DroneChargeState.IDLE.code());
        assertEquals(1, DroneChargeState.CHARGING.code());
        // 双向闭环
        for (DroneChargeState s : DroneChargeState.values()) {
            assertEquals(s, DroneChargeState.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("fromCode 方法存在（修复验证 #2）")
    void testFromCodeMethodExists() {
        Method fromCode = assertDoesNotThrow(
                () -> DroneChargeState.class.getMethod("fromCode", int.class));
        assertNotNull(fromCode);
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("空闲", DroneChargeState.IDLE.description());
        assertEquals("充电中", DroneChargeState.CHARGING.description());
        for (DroneChargeState s : DroneChargeState.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
