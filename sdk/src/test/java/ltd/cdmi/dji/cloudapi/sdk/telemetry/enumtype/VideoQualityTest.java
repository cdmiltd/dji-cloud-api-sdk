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
 * 验证 {@link VideoQuality} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 5 个 video_quality 数值
 *       （0=自适应, 1=流畅, 2=标清, 3=高清, 4=超清）能通过 {@link VideoQuality#fromCode(int)} 反查</li>
 *   <li>-1 / 5 / 255 等越界值均抛出 {@link IllegalArgumentException}，确保码流质量语义不混入错误值</li>
 * </ol>
 */
class VideoQualityTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock3 文档定义的 5 个数值（0, 1, 2, 3, 4）")
    void testFromCodeAllValues() {
        assertEquals(VideoQuality.AUTO, VideoQuality.fromCode(0));
        assertEquals(VideoQuality.SMOOTH, VideoQuality.fromCode(1));
        assertEquals(VideoQuality.STANDARD, VideoQuality.fromCode(2));
        assertEquals(VideoQuality.HIGH, VideoQuality.fromCode(3));
        assertEquals(VideoQuality.ULTRA, VideoQuality.fromCode(4));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> VideoQuality.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> VideoQuality.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> VideoQuality.fromCode(6));
        assertThrows(IllegalArgumentException.class, () -> VideoQuality.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 5（自适应 + 流畅 + 标清 + 高清 + 超清）")
    void testTotalCount() {
        assertEquals(5, VideoQuality.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→AUTO, 1→SMOOTH, 2→STANDARD, 3→HIGH, 4→ULTRA")
    void testCodeRoundTrip() {
        assertEquals(0, VideoQuality.AUTO.code());
        assertEquals(1, VideoQuality.SMOOTH.code());
        assertEquals(2, VideoQuality.STANDARD.code());
        assertEquals(3, VideoQuality.HIGH.code());
        assertEquals(4, VideoQuality.ULTRA.code());
        // 双向闭环
        for (VideoQuality q : VideoQuality.values()) {
            assertEquals(q, VideoQuality.fromCode(q.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("自适应", VideoQuality.AUTO.description());
        assertEquals("流畅", VideoQuality.SMOOTH.description());
        assertEquals("标清", VideoQuality.STANDARD.description());
        assertEquals("高清", VideoQuality.HIGH.description());
        assertEquals("超清", VideoQuality.ULTRA.description());
        for (VideoQuality q : VideoQuality.values()) {
            assertTrue(!q.description().isBlank());
        }
    }
}
