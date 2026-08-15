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
 * 验证 {@link NetworkType} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 2 个 network_state.type 数值
 *       （1=4G, 2=以太网）能通过 {@link NetworkType#fromCode(int)} 反查到枚举常量</li>
 *   <li><strong>值域从 1 开始（无 0）</strong>：0 / 3 / -1 等越界值均抛出
 *       {@link IllegalArgumentException}，确保 0 值不混入（与其他从 0 开始的枚举区分）</li>
 * </ol>
 */
class NetworkTypeTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 2 个数值（1, 2）— 值域从 1 开始")
    void testFromCodeAllValues() {
        assertEquals(NetworkType.CELLULAR_4G, NetworkType.fromCode(1));
        assertEquals(NetworkType.ETHERNET, NetworkType.fromCode(2));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含 0，值域从 1 开始无 0）")
    void testFromCodeUnknownThrows() {
        // 值域从 1 开始，0 不属于合法值
        assertThrows(IllegalArgumentException.class, () -> NetworkType.fromCode(0));
        assertThrows(IllegalArgumentException.class, () -> NetworkType.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> NetworkType.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> NetworkType.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 2（4G + 以太网）")
    void testTotalCount() {
        assertEquals(2, NetworkType.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：1→CELLULAR_4G, 2→ETHERNET")
    void testCodeRoundTrip() {
        assertEquals(1, NetworkType.CELLULAR_4G.code());
        assertEquals(2, NetworkType.ETHERNET.code());
        // 双向闭环
        for (NetworkType t : NetworkType.values()) {
            assertEquals(t, NetworkType.fromCode(t.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("4G", NetworkType.CELLULAR_4G.description());
        assertEquals("以太网", NetworkType.ETHERNET.description());
        for (NetworkType t : NetworkType.values()) {
            assertTrue(!t.description().isBlank());
        }
    }
}
