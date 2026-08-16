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
 * 验证 {@link NetworkQuality} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 6 个 network_state.quality 数值
 *       （0=无信号, 1=差, 2=较差, 3=一般, 4=较好, 5=好）能通过
 *       {@link NetworkQuality#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1 / 6 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保网络质量等级语义不混入错误值</li>
 * </ol>
 */
class NetworkQualityTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 6 个数值（0, 1, 2, 3, 4, 5）")
    void testFromCodeAllValues() {
        assertEquals(NetworkQuality.NO_SIGNAL, NetworkQuality.fromCode(0));
        assertEquals(NetworkQuality.POOR, NetworkQuality.fromCode(1));
        assertEquals(NetworkQuality.FAIR, NetworkQuality.fromCode(2));
        assertEquals(NetworkQuality.MODERATE, NetworkQuality.fromCode(3));
        assertEquals(NetworkQuality.GOOD, NetworkQuality.fromCode(4));
        assertEquals(NetworkQuality.EXCELLENT, NetworkQuality.fromCode(5));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> NetworkQuality.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> NetworkQuality.fromCode(6));
        assertThrows(IllegalArgumentException.class, () -> NetworkQuality.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> NetworkQuality.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 6（无信号 + 差 + 较差 + 一般 + 较好 + 好）")
    void testTotalCount() {
        assertEquals(6, NetworkQuality.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→NO_SIGNAL, 1→POOR, 2→FAIR, 3→MODERATE, 4→GOOD, 5→EXCELLENT")
    void testCodeRoundTrip() {
        assertEquals(0, NetworkQuality.NO_SIGNAL.code());
        assertEquals(1, NetworkQuality.POOR.code());
        assertEquals(2, NetworkQuality.FAIR.code());
        assertEquals(3, NetworkQuality.MODERATE.code());
        assertEquals(4, NetworkQuality.GOOD.code());
        assertEquals(5, NetworkQuality.EXCELLENT.code());
        // 双向闭环
        for (NetworkQuality q : NetworkQuality.values()) {
            assertEquals(q, NetworkQuality.fromCode(q.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("无信号", NetworkQuality.NO_SIGNAL.description());
        assertEquals("差", NetworkQuality.POOR.description());
        assertEquals("较差", NetworkQuality.FAIR.description());
        assertEquals("一般", NetworkQuality.MODERATE.description());
        assertEquals("较好", NetworkQuality.GOOD.description());
        assertEquals("好", NetworkQuality.EXCELLENT.description());
        for (NetworkQuality q : NetworkQuality.values()) {
            assertTrue(!q.description().isBlank());
        }
    }
}
