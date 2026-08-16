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
 * 验证 {@link SourceType} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 4 个 source_type 数值
 *       （0=未标定, 1=自收敛标定, 2=手动标定, 3=网络RTK标定）能通过
 *       {@link SourceType#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 4 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保标定类型语义不混入错误值</li>
 * </ol>
 */
class SourceTypeTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 4 个数值（0, 1, 2, 3）")
    void testFromCodeAllValues() {
        assertEquals(SourceType.UNCALIBRATED, SourceType.fromCode(0));
        assertEquals(SourceType.SELF_CONVERGE, SourceType.fromCode(1));
        assertEquals(SourceType.MANUAL, SourceType.fromCode(2));
        assertEquals(SourceType.NETWORK_RTK, SourceType.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> SourceType.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> SourceType.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> SourceType.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> SourceType.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（未标定 + 自收敛标定 + 手动标定 + 网络RTK标定）")
    void testTotalCount() {
        assertEquals(4, SourceType.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→UNCALIBRATED, 1→SELF_CONVERGE, 2→MANUAL, 3→NETWORK_RTK")
    void testCodeRoundTrip() {
        assertEquals(0, SourceType.UNCALIBRATED.code());
        assertEquals(1, SourceType.SELF_CONVERGE.code());
        assertEquals(2, SourceType.MANUAL.code());
        assertEquals(3, SourceType.NETWORK_RTK.code());
        // 双向闭环
        for (SourceType t : SourceType.values()) {
            assertEquals(t, SourceType.fromCode(t.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("未标定", SourceType.UNCALIBRATED.description());
        assertEquals("自收敛标定", SourceType.SELF_CONVERGE.description());
        assertEquals("手动标定", SourceType.MANUAL.description());
        assertEquals("网络RTK标定", SourceType.NETWORK_RTK.description());
        for (SourceType t : SourceType.values()) {
            assertTrue(!t.description().isBlank());
        }
    }
}
