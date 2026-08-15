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
 * 验证 {@link Rainfall} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 4 个 rainfall 数值
 *       （0=无雨, 1=小雨, 2=中雨, 3=大雨）能通过 {@link Rainfall#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 4 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保降雨等级语义不混入错误值</li>
 * </ol>
 */
class RainfallTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 4 个数值（0, 1, 2, 3）")
    void testFromCodeAllValues() {
        assertEquals(Rainfall.NO_RAIN, Rainfall.fromCode(0));
        assertEquals(Rainfall.LIGHT, Rainfall.fromCode(1));
        assertEquals(Rainfall.MODERATE, Rainfall.fromCode(2));
        assertEquals(Rainfall.HEAVY, Rainfall.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> Rainfall.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> Rainfall.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> Rainfall.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> Rainfall.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（无雨 + 小雨 + 中雨 + 大雨）")
    void testTotalCount() {
        assertEquals(4, Rainfall.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→NO_RAIN, 1→LIGHT, 2→MODERATE, 3→HEAVY")
    void testCodeRoundTrip() {
        assertEquals(0, Rainfall.NO_RAIN.code());
        assertEquals(1, Rainfall.LIGHT.code());
        assertEquals(2, Rainfall.MODERATE.code());
        assertEquals(3, Rainfall.HEAVY.code());
        // 双向闭环
        for (Rainfall r : Rainfall.values()) {
            assertEquals(r, Rainfall.fromCode(r.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("无雨", Rainfall.NO_RAIN.description());
        assertEquals("小雨", Rainfall.LIGHT.description());
        assertEquals("中雨", Rainfall.MODERATE.description());
        assertEquals("大雨", Rainfall.HEAVY.description());
        for (Rainfall r : Rainfall.values()) {
            assertTrue(!r.description().isBlank());
        }
    }
}
