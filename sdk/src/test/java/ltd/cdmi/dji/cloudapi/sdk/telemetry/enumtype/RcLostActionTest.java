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
 * 验证 {@link RcLostAction} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI M30 properties 文档定义的 3 个 rc_lost_action 数值（0-2）能通过
 *       {@link RcLostAction#fromCode(int)} 反查到枚举常量</li>
 *   <li><strong>与 out_of_control_action 区分</strong>：rc_lost_action 0=悬停/1=降落/2=返航，
 *       与航线任务的 out_of_control_action（0=返航/1=悬停/2=降落）值映射不同</li>
 *   <li>-1 / 3 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class RcLostActionTest {

    @Test
    @DisplayName("fromCode 反查 DJI M30 文档定义的 3 个数值（0-2）— 与 out_of_control_action 值映射不同")
    void testFromCodeAllValues() {
        assertEquals(RcLostAction.HOVERING, RcLostAction.fromCode(0));
        assertEquals(RcLostAction.LANDING, RcLostAction.fromCode(1));
        assertEquals(RcLostAction.RETURN_TO_HOME, RcLostAction.fromCode(2));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> RcLostAction.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> RcLostAction.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> RcLostAction.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> RcLostAction.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 3（悬停 + 降落 + 返航）")
    void testTotalCount() {
        assertEquals(3, RcLostAction.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→HOVERING, 1→LANDING, 2→RETURN_TO_HOME")
    void testCodeRoundTrip() {
        assertEquals(0, RcLostAction.HOVERING.code());
        assertEquals(1, RcLostAction.LANDING.code());
        assertEquals(2, RcLostAction.RETURN_TO_HOME.code());
        // 双向闭环
        for (RcLostAction a : RcLostAction.values()) {
            assertEquals(a, RcLostAction.fromCode(a.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("悬停", RcLostAction.HOVERING.description());
        assertEquals("降落", RcLostAction.LANDING.description());
        assertEquals("返航", RcLostAction.RETURN_TO_HOME.description());
        for (RcLostAction a : RcLostAction.values()) {
            assertTrue(!a.description().isBlank());
        }
    }
}
