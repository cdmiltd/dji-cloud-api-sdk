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
 * 验证 {@link Dock3HomePositionIsValid} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 4 个 home_position_is_valid 数值
 *       （0=都无效, 1=都有效, 2=航向有效经纬度无效, 3=经纬度有效航向无效）能通过
 *       {@link Dock3HomePositionIsValid#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 4 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保有效性组合语义不混入错误值</li>
 * </ol>
 */
class Dock3HomePositionIsValidTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 4 个数值（0, 1, 2, 3）")
    void testFromCodeAllValues() {
        assertEquals(Dock3HomePositionIsValid.BOTH_INVALID, Dock3HomePositionIsValid.fromCode(0));
        assertEquals(Dock3HomePositionIsValid.BOTH_VALID, Dock3HomePositionIsValid.fromCode(1));
        assertEquals(Dock3HomePositionIsValid.HEADING_VALID_ONLY, Dock3HomePositionIsValid.fromCode(2));
        assertEquals(Dock3HomePositionIsValid.COORDINATE_VALID_ONLY, Dock3HomePositionIsValid.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> Dock3HomePositionIsValid.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> Dock3HomePositionIsValid.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> Dock3HomePositionIsValid.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> Dock3HomePositionIsValid.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（都无效 + 都有效 + 航向有效经纬度无效 + 经纬度有效航向无效）")
    void testTotalCount() {
        assertEquals(4, Dock3HomePositionIsValid.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→BOTH_INVALID, 1→BOTH_VALID, 2→HEADING_VALID_ONLY, 3→COORDINATE_VALID_ONLY")
    void testCodeRoundTrip() {
        assertEquals(0, Dock3HomePositionIsValid.BOTH_INVALID.code());
        assertEquals(1, Dock3HomePositionIsValid.BOTH_VALID.code());
        assertEquals(2, Dock3HomePositionIsValid.HEADING_VALID_ONLY.code());
        assertEquals(3, Dock3HomePositionIsValid.COORDINATE_VALID_ONLY.code());
        // 双向闭环
        for (Dock3HomePositionIsValid v : Dock3HomePositionIsValid.values()) {
            assertEquals(v, Dock3HomePositionIsValid.fromCode(v.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文（含航向/经纬度有效性组合语义）")
    void testDescription() {
        assertEquals("航向和经纬度坐标都无效", Dock3HomePositionIsValid.BOTH_INVALID.description());
        assertEquals("航向和经纬度坐标都有效", Dock3HomePositionIsValid.BOTH_VALID.description());
        assertEquals("航向有效，经纬度无效", Dock3HomePositionIsValid.HEADING_VALID_ONLY.description());
        assertEquals("经纬度有效，航向无效", Dock3HomePositionIsValid.COORDINATE_VALID_ONLY.description());
        for (Dock3HomePositionIsValid v : Dock3HomePositionIsValid.values()) {
            assertTrue(!v.description().isBlank());
        }
    }
}
