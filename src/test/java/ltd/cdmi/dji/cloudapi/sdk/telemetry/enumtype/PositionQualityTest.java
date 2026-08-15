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
 * 验证 {@link PositionQuality} 枚举的不连续 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 6 个 position_state.quality 数值
 *       （1-5 为正常档位, 10 为 RTK fixed）能通过 {@link PositionQuality#fromCode(int)} 反查</li>
 *   <li><strong>值域不连续</strong>：0 / 6 / 7 / 8 / 9 / 11 等值均抛出
 *       {@link IllegalArgumentException}，确保 0 值与中间空隙不混入</li>
 * </ol>
 */
class PositionQualityTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 6 个数值（1-5 连续, 10 跳跃）— 值域不连续")
    void testFromCodeAllValues() {
        assertEquals(PositionQuality.LEVEL_1, PositionQuality.fromCode(1));
        assertEquals(PositionQuality.LEVEL_2, PositionQuality.fromCode(2));
        assertEquals(PositionQuality.LEVEL_3, PositionQuality.fromCode(3));
        assertEquals(PositionQuality.LEVEL_4, PositionQuality.fromCode(4));
        assertEquals(PositionQuality.LEVEL_5, PositionQuality.fromCode(5));
        assertEquals(PositionQuality.RTK_FIXED, PositionQuality.fromCode(10));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含 0、中间空隙 6-9、边界外 11）")
    void testFromCodeUnknownThrows() {
        // 0 不属于合法值（值域从 1 开始）
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(0));
        // 5 与 10 之间的空隙 6/7/8/9 不属于合法值
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(6));
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(8));
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(9));
        // 边界外
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(11));
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> PositionQuality.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 6（1档 + 2档 + 3档 + 4档 + 5档 + RTK fixed）")
    void testTotalCount() {
        assertEquals(6, PositionQuality.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：1-5→LEVEL_1-5, 10→RTK_FIXED")
    void testCodeRoundTrip() {
        assertEquals(1, PositionQuality.LEVEL_1.code());
        assertEquals(2, PositionQuality.LEVEL_2.code());
        assertEquals(3, PositionQuality.LEVEL_3.code());
        assertEquals(4, PositionQuality.LEVEL_4.code());
        assertEquals(5, PositionQuality.LEVEL_5.code());
        assertEquals(10, PositionQuality.RTK_FIXED.code());
        // 双向闭环
        for (PositionQuality q : PositionQuality.values()) {
            assertEquals(q, PositionQuality.fromCode(q.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("1档", PositionQuality.LEVEL_1.description());
        assertEquals("2档", PositionQuality.LEVEL_2.description());
        assertEquals("3档", PositionQuality.LEVEL_3.description());
        assertEquals("4档", PositionQuality.LEVEL_4.description());
        assertEquals("5档", PositionQuality.LEVEL_5.description());
        assertEquals("RTK fixed", PositionQuality.RTK_FIXED.description());
        for (PositionQuality q : PositionQuality.values()) {
            assertTrue(!q.description().isBlank());
        }
    }
}
