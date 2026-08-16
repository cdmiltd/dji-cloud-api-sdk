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
 * 验证 {@link ModeCodeReason} 枚举的 code 反查与描述准确性。
 *
 * <p><b>核心证明</b>：DJI M3D/M30 设备属性文档定义的 24 个 mode_code_reason 数值
 * （0-23）能通过 {@link ModeCodeReason#fromCode(int)} 反查到枚举常量，
 * 且每个枚举的 code 与 description 与官方文档一致，确保 SDK 能正确将数值转换为开发者可读的语义。
 */
class ModeCodeReasonTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 24 个数值（0-23）")
    void testFromCodeAllValues() {
        assertEquals(ModeCodeReason.NO_MEANING, ModeCodeReason.fromCode(0));
        assertEquals(ModeCodeReason.LOW_BATTERY, ModeCodeReason.fromCode(1));
        assertEquals(ModeCodeReason.LOW_VOLTAGE, ModeCodeReason.fromCode(2));
        assertEquals(ModeCodeReason.CRITICAL_VOLTAGE, ModeCodeReason.fromCode(3));
        assertEquals(ModeCodeReason.RC_BUTTON_REQUEST, ModeCodeReason.fromCode(4));
        assertEquals(ModeCodeReason.APP_REQUEST, ModeCodeReason.fromCode(5));
        assertEquals(ModeCodeReason.RC_SIGNAL_LOSS, ModeCodeReason.fromCode(6));
        assertEquals(ModeCodeReason.EXTERNAL_TRIGGER, ModeCodeReason.fromCode(7));
        assertEquals(ModeCodeReason.ENTER_GEO_ZONE, ModeCodeReason.fromCode(8));
        assertEquals(ModeCodeReason.RETURN_TOO_CLOSE, ModeCodeReason.fromCode(9));
        assertEquals(ModeCodeReason.RETURN_TOO_FAR, ModeCodeReason.fromCode(10));
        assertEquals(ModeCodeReason.WAYPOINT_TAKEOFF, ModeCodeReason.fromCode(11));
        assertEquals(ModeCodeReason.RETURN_ABOVE_HOME, ModeCodeReason.fromCode(12));
        assertEquals(ModeCodeReason.SECOND_STAGE_DESCENT, ModeCodeReason.fromCode(13));
        assertEquals(ModeCodeReason.LOW_ALTITUDE_OVERRIDE, ModeCodeReason.fromCode(14));
        assertEquals(ModeCodeReason.NEARBY_FLIGHTS, ModeCodeReason.fromCode(15));
        assertEquals(ModeCodeReason.ALTITUDE_CONTROL_FAILURE, ModeCodeReason.fromCode(16));
        assertEquals(ModeCodeReason.INTELLIGENT_LOW_BATTERY, ModeCodeReason.fromCode(17));
        assertEquals(ModeCodeReason.AP_CONTROL, ModeCodeReason.fromCode(18));
        assertEquals(ModeCodeReason.HARDWARE_ABNORMAL, ModeCodeReason.fromCode(19));
        assertEquals(ModeCodeReason.ANTI_COLLISION_END, ModeCodeReason.fromCode(20));
        assertEquals(ModeCodeReason.RETURN_CANCELED, ModeCodeReason.fromCode(21));
        assertEquals(ModeCodeReason.RETURN_OBSTACLE, ModeCodeReason.fromCode(22));
        assertEquals(ModeCodeReason.HIGH_WIND, ModeCodeReason.fromCode(23));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> ModeCodeReason.fromCode(24));
        assertThrows(IllegalArgumentException.class, () -> ModeCodeReason.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> ModeCodeReason.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 24（覆盖文档 0-23 全部数值）")
    void testTotalCount() {
        assertEquals(24, ModeCodeReason.values().length);
    }

    @Test
    @DisplayName("code() 返回值连续无间隔（0 到 23）")
    void testCodeSequenceContiguous() {
        for (int expected = 0; expected <= 23; expected++) {
            assertEquals(expected, ModeCodeReason.fromCode(expected).code());
        }
    }

    @Test
    @DisplayName("description 非空且包含中文描述")
    void testDescriptionNonBlank() {
        for (ModeCodeReason reason : ModeCodeReason.values()) {
            assertTrue(!reason.description().isBlank());
            assertTrue(reason.description().length() >= 2);
        }
    }

    @Test
    @DisplayName("关键描述准确性：边界值与代表性枚举")
    void testKeyDescriptions() {
        assertEquals("无意义", ModeCodeReason.NO_MEANING.description());
        assertEquals("电池电量不足（返航、降落）", ModeCodeReason.LOW_BATTERY.description());
        assertEquals("电压严重过低（返航、降落）", ModeCodeReason.CRITICAL_VOLTAGE.description());
        assertEquals("执行航点任务时请求（起飞）", ModeCodeReason.WAYPOINT_TAKEOFF.description());
        assertEquals("距地面0.7m继续下降（降落）", ModeCodeReason.SECOND_STAGE_DESCENT.description());
        assertEquals("强制突破限低保护（降落）", ModeCodeReason.LOW_ALTITUDE_OVERRIDE.description());
        assertEquals("智能低电量返航后进入（降落）", ModeCodeReason.INTELLIGENT_LOW_BATTERY.description());
        assertEquals("返航取消（悬停）", ModeCodeReason.RETURN_CANCELED.description());
        assertEquals("返航时遇到障碍物（降落）", ModeCodeReason.RETURN_OBSTACLE.description());
        assertEquals("机场场景下大风触发（返航）", ModeCodeReason.HIGH_WIND.description());
    }

    @Test
    @DisplayName("与 DroneModeCode 的语义区分：reason 描述都包含触发动作（返航/降落/起飞/悬停/手动飞行）")
    void testDescriptionsContainActionVerbs() {
        // 除 NO_MEANING 外，所有 reason 都应说明触发的具体动作
        for (ModeCodeReason reason : ModeCodeReason.values()) {
            if (reason == ModeCodeReason.NO_MEANING) {
                continue;
            }
            String desc = reason.description();
            assertTrue(
                    desc.contains("返航") || desc.contains("降落") || desc.contains("起飞")
                            || desc.contains("悬停") || desc.contains("手动飞行"),
                    "reason=" + reason + " 描述缺少触发动作: " + desc);
        }
    }
}
