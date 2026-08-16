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
 * 验证 {@link DongleType} 枚举的不连续 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 2 个 dongle_type 数值（6=旧 Dongle, 10=支持 eSIM 的新 Dongle）
 *       能通过 {@link DongleType#fromCode(int)} 反查到枚举常量</li>
 *   <li><strong>值域不连续</strong>：仅 6 和 10，0/1/7/9/11 等常见猜测值均抛出
 *       {@link IllegalArgumentException}（防止早期推测的 0/1 错误值混入）</li>
 * </ol>
 */
class DongleTypeTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 2 个数值（6, 10）— 值域不连续")
    void testFromCodeAllValues() {
        assertEquals(DongleType.LEGACY, DongleType.fromCode(6));
        assertEquals(DongleType.ESIM_CAPABLE, DongleType.fromCode(10));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含 0/1/5/7/9/11 等常见错误猜测）")
    void testFromCodeUnknownThrows() {
        // 早期推测错误值 0/1（内置/外置加密狗）与 DJI 文档实际值 6/10 不符
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(0));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(1));
        // 中间值 7/8/9 不属于合法值
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(8));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(9));
        // 边界外
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(11));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> DongleType.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 2（旧 Dongle + 支持 eSIM 的新 Dongle）")
    void testTotalCount() {
        assertEquals(2, DongleType.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：6→LEGACY, 10→ESIM_CAPABLE")
    void testCodeRoundTrip() {
        assertEquals(6, DongleType.LEGACY.code());
        assertEquals(10, DongleType.ESIM_CAPABLE.code());
        // 双向闭环
        for (DongleType type : DongleType.values()) {
            assertEquals(type, DongleType.fromCode(type.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("旧 Dongle", DongleType.LEGACY.description());
        assertEquals("支持 eSIM 的新 Dongle", DongleType.ESIM_CAPABLE.description());
        for (DongleType type : DongleType.values()) {
            assertTrue(!type.description().isBlank());
        }
    }
}
