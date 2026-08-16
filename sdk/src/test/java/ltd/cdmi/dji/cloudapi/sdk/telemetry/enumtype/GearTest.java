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
 * 验证 {@link Gear} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI 飞行器 properties 文档定义的 10 个 gear 数值（0-9）能通过 {@link Gear#fromCode(int)} 反查</li>
 *   <li><strong>历史缺陷防回归</strong>：早期 Gear 曾仅定义部分档位，现完整覆盖 10 档（A/P/NAV/FPV/FARM/S/F/M/G/T）</li>
 *   <li>-1 / 10 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class GearTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 10 个数值（0-9）— 历史缺陷防回归")
    void testFromCodeAllValues() {
        assertEquals(Gear.A, Gear.fromCode(0));
        assertEquals(Gear.P, Gear.fromCode(1));
        assertEquals(Gear.NAV, Gear.fromCode(2));
        assertEquals(Gear.FPV, Gear.fromCode(3));
        assertEquals(Gear.FARM, Gear.fromCode(4));
        assertEquals(Gear.S, Gear.fromCode(5));
        assertEquals(Gear.F, Gear.fromCode(6));
        assertEquals(Gear.M, Gear.fromCode(7));
        assertEquals(Gear.G, Gear.fromCode(8));
        assertEquals(Gear.T, Gear.fromCode(9));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> Gear.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> Gear.fromCode(10));
        assertThrows(IllegalArgumentException.class, () -> Gear.fromCode(11));
        assertThrows(IllegalArgumentException.class, () -> Gear.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 10（A + P + NAV + FPV + FARM + S + F + M + G + T）")
    void testTotalCount() {
        assertEquals(10, Gear.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0-9 → A/P/NAV/FPV/FARM/S/F/M/G/T")
    void testCodeRoundTrip() {
        assertEquals(0, Gear.A.code());
        assertEquals(1, Gear.P.code());
        assertEquals(2, Gear.NAV.code());
        assertEquals(3, Gear.FPV.code());
        assertEquals(4, Gear.FARM.code());
        assertEquals(5, Gear.S.code());
        assertEquals(6, Gear.F.code());
        assertEquals(7, Gear.M.code());
        assertEquals(8, Gear.G.code());
        assertEquals(9, Gear.T.code());
        // 双向闭环
        for (Gear g : Gear.values()) {
            assertEquals(g, Gear.fromCode(g.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("A档（姿态）", Gear.A.description());
        assertEquals("P档（定位）", Gear.P.description());
        assertEquals("NAV档（导航）", Gear.NAV.description());
        assertEquals("FPV档（第一人称视角）", Gear.FPV.description());
        assertEquals("FARM档（农业）", Gear.FARM.description());
        assertEquals("S档（运动）", Gear.S.description());
        assertEquals("F档", Gear.F.description());
        assertEquals("M档", Gear.M.description());
        assertEquals("G档", Gear.G.description());
        assertEquals("T档", Gear.T.description());
        for (Gear g : Gear.values()) {
            assertTrue(!g.description().isBlank());
        }
    }
}
