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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DockModeCode} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI 机场 properties 文档定义的 6 个 mode_code 数值（0-5）能通过
 *       {@link DockModeCode#fromCode(int)} 反查</li>
 *   <li><strong>拆分防回归</strong>：与 {@link DroneModeCode}（飞行器模式码）是不同枚举，值域仅 0-5</li>
 *   <li>-1 / 6 / 255 等越界值均抛出 {@link IllegalArgumentException}</li>
 * </ol>
 */
class DockModeCodeTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 6 个数值（0-5）")
    void testFromCodeAllValues() {
        assertEquals(DockModeCode.IDLE, DockModeCode.fromCode(0));
        assertEquals(DockModeCode.LOCAL_DEBUG, DockModeCode.fromCode(1));
        assertEquals(DockModeCode.REMOTE_DEBUG, DockModeCode.fromCode(2));
        assertEquals(DockModeCode.FIRMWARE_UPGRADING, DockModeCode.fromCode(3));
        assertEquals(DockModeCode.WORKING, DockModeCode.fromCode(4));
        assertEquals(DockModeCode.CALIBRATION_PENDING, DockModeCode.fromCode(5));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> DockModeCode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> DockModeCode.fromCode(6));
        assertThrows(IllegalArgumentException.class, () -> DockModeCode.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> DockModeCode.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 6（空闲中 + 现场调试 + 远程调试 + 固件升级中 + 作业中 + 待标定）")
    void testTotalCount() {
        assertEquals(6, DockModeCode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0-5 → IDLE-CALIBRATION_PENDING")
    void testCodeRoundTrip() {
        assertEquals(0, DockModeCode.IDLE.code());
        assertEquals(1, DockModeCode.LOCAL_DEBUG.code());
        assertEquals(2, DockModeCode.REMOTE_DEBUG.code());
        assertEquals(3, DockModeCode.FIRMWARE_UPGRADING.code());
        assertEquals(4, DockModeCode.WORKING.code());
        assertEquals(5, DockModeCode.CALIBRATION_PENDING.code());
        // 双向闭环
        for (DockModeCode m : DockModeCode.values()) {
            assertEquals(m, DockModeCode.fromCode(m.code()));
        }
    }

    @Test
    @DisplayName("与 DroneModeCode 是不同枚举，class 不相等")
    void testNotDroneModeCode() {
        assertNotEquals(DockModeCode.class, DroneModeCode.class);
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("空闲中", DockModeCode.IDLE.description());
        assertEquals("现场调试", DockModeCode.LOCAL_DEBUG.description());
        assertEquals("远程调试", DockModeCode.REMOTE_DEBUG.description());
        assertEquals("固件升级中", DockModeCode.FIRMWARE_UPGRADING.description());
        assertEquals("作业中", DockModeCode.WORKING.description());
        assertEquals("待标定", DockModeCode.CALIBRATION_PENDING.description());
        for (DockModeCode m : DockModeCode.values()) {
            assertTrue(!m.description().isBlank());
        }
    }
}
