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
 * 验证 {@link CoverState} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 4 个 cover_state 数值（0-3）能通过
 *       {@link CoverState#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 4 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class CoverStateTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 4 个数值（0-3）")
    void testFromCodeAllValues() {
        assertEquals(CoverState.CLOSED, CoverState.fromCode(0));
        assertEquals(CoverState.OPENED, CoverState.fromCode(1));
        assertEquals(CoverState.HALF_OPEN, CoverState.fromCode(2));
        assertEquals(CoverState.ABNORMAL, CoverState.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> CoverState.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> CoverState.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> CoverState.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> CoverState.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（关闭 + 打开 + 半开 + 异常）")
    void testTotalCount() {
        assertEquals(4, CoverState.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→CLOSED, 1→OPENED, 2→HALF_OPEN, 3→ABNORMAL")
    void testCodeRoundTrip() {
        assertEquals(0, CoverState.CLOSED.code());
        assertEquals(1, CoverState.OPENED.code());
        assertEquals(2, CoverState.HALF_OPEN.code());
        assertEquals(3, CoverState.ABNORMAL.code());
        // 双向闭环
        for (CoverState s : CoverState.values()) {
            assertEquals(s, CoverState.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("关闭", CoverState.CLOSED.description());
        assertEquals("打开", CoverState.OPENED.description());
        assertEquals("半开", CoverState.HALF_OPEN.description());
        assertEquals("舱盖状态异常", CoverState.ABNORMAL.description());
        for (CoverState s : CoverState.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
