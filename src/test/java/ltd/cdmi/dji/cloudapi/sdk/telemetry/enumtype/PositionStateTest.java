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
 * 验证 {@link PositionState} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI 机场 properties 文档定义的 4 个 position_state.is_fixed 数值（0-3）能通过
 *       {@link PositionState#fromCode(int)} 反查</li>
 *   <li><strong>修复验证 #2</strong>：{@code fromCode} 方法存在且正常工作
 *       （早期 PositionState 缺失 fromCode 方法，现已修复）</li>
 *   <li>-1 / 4 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class PositionStateTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 4 个数值（0-3）")
    void testFromCodeAllValues() {
        assertEquals(PositionState.NOT_STARTED, PositionState.fromCode(0));
        assertEquals(PositionState.CONVERGING, PositionState.fromCode(1));
        assertEquals(PositionState.CONVERGED, PositionState.fromCode(2));
        assertEquals(PositionState.CONVERGE_FAILED, PositionState.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> PositionState.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> PositionState.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> PositionState.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> PositionState.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（未开始 + 收敛中 + 收敛成功 + 收敛失败）")
    void testTotalCount() {
        assertEquals(4, PositionState.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→NOT_STARTED, 1→CONVERGING, 2→CONVERGED, 3→CONVERGE_FAILED")
    void testCodeRoundTrip() {
        assertEquals(0, PositionState.NOT_STARTED.code());
        assertEquals(1, PositionState.CONVERGING.code());
        assertEquals(2, PositionState.CONVERGED.code());
        assertEquals(3, PositionState.CONVERGE_FAILED.code());
        // 双向闭环
        for (PositionState s : PositionState.values()) {
            assertEquals(s, PositionState.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("fromCode 方法存在（修复验证 #2）")
    void testFromCodeMethodExists() {
        Method fromCode = assertDoesNotThrow(
                () -> PositionState.class.getMethod("fromCode", int.class));
        assertNotNull(fromCode);
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("未开始", PositionState.NOT_STARTED.description());
        assertEquals("收敛中", PositionState.CONVERGING.description());
        assertEquals("收敛成功", PositionState.CONVERGED.description());
        assertEquals("收敛失败", PositionState.CONVERGE_FAILED.description());
        for (PositionState s : PositionState.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
