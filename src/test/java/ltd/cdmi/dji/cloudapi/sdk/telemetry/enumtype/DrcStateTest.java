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
 * 验证 {@link DrcState} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 3 个 drc_state 数值（0-2）能通过
 *       {@link DrcState#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 3 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class DrcStateTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 3 个数值（0-2）")
    void testFromCodeAllValues() {
        assertEquals(DrcState.DISCONNECTED, DrcState.fromCode(0));
        assertEquals(DrcState.CONNECTING, DrcState.fromCode(1));
        assertEquals(DrcState.CONNECTED, DrcState.fromCode(2));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> DrcState.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> DrcState.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> DrcState.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> DrcState.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 3（未连接 + 连接中 + 已连接）")
    void testTotalCount() {
        assertEquals(3, DrcState.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→DISCONNECTED, 1→CONNECTING, 2→CONNECTED")
    void testCodeRoundTrip() {
        assertEquals(0, DrcState.DISCONNECTED.code());
        assertEquals(1, DrcState.CONNECTING.code());
        assertEquals(2, DrcState.CONNECTED.code());
        // 双向闭环
        for (DrcState s : DrcState.values()) {
            assertEquals(s, DrcState.fromCode(s.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("未连接", DrcState.DISCONNECTED.description());
        assertEquals("连接中", DrcState.CONNECTING.description());
        assertEquals("已连接", DrcState.CONNECTED.description());
        for (DrcState s : DrcState.values()) {
            assertTrue(!s.description().isBlank());
        }
    }
}
