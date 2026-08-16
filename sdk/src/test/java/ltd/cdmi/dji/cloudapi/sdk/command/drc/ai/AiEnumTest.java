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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 AI 相关枚举的 code 映射与 Jackson 双向绑定。
 */
class AiEnumTest {

    @Test
    @DisplayName("AiSwitchState: code 映射 0=OFF, 1=ON")
    void testAiSwitchState() {
        assertEquals(0, AiSwitchState.OFF.code());
        assertEquals(1, AiSwitchState.ON.code());
        assertEquals(AiSwitchState.OFF, AiSwitchState.fromCode(0));
        assertEquals(AiSwitchState.ON, AiSwitchState.fromCode(1));
        assertThrows(IllegalArgumentException.class, () -> AiSwitchState.fromCode(2));
    }

    @Test
    @DisplayName("AiScoreMode: code 映射 0=INVALID, 1=COUNT, 2=SEARCH_RESCUE, 3=CUSTOM")
    void testAiScoreMode() {
        assertEquals(0, AiScoreMode.INVALID.code());
        assertEquals(1, AiScoreMode.COUNT.code());
        assertEquals(2, AiScoreMode.SEARCH_RESCUE.code());
        assertEquals(3, AiScoreMode.CUSTOM.code());
        assertEquals(AiScoreMode.CUSTOM, AiScoreMode.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> AiScoreMode.fromCode(4));
    }

    @Test
    @DisplayName("AiTrackState: code 映射 0=IDLE, 1=WAITING_SELECT, 2=WAITING_CONFIRM, 3=TRACKING")
    void testAiTrackState() {
        assertEquals(AiTrackState.IDLE, AiTrackState.fromCode(0));
        assertEquals(AiTrackState.TRACKING, AiTrackState.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> AiTrackState.fromCode(4));
    }

    @Test
    @DisplayName("AiTrackStateReason: 正常原因 0-15 + 退出原因 160-168，共 25 个")
    void testAiTrackStateReason() {
        assertEquals(25, AiTrackStateReason.values().length);
        assertEquals(AiTrackStateReason.NORMAL, AiTrackStateReason.fromCode(0));
        assertEquals(AiTrackStateReason.GPS_SIGNAL_WEAK, AiTrackStateReason.fromCode(15));
        assertEquals(AiTrackStateReason.EXIT_NORMAL, AiTrackStateReason.fromCode(160));
        assertEquals(AiTrackStateReason.EXIT_RC_SIGNAL_LOST, AiTrackStateReason.fromCode(168));
        assertThrows(IllegalArgumentException.class, () -> AiTrackStateReason.fromCode(100));
    }

    @Test
    @DisplayName("AiImageSource: code 映射 1=WIDE, 2=ZOOM, 3=IR, 7=VISIBLE_LIGHT")
    void testAiImageSource() {
        assertEquals(AiImageSource.WIDE, AiImageSource.fromCode(1));
        assertEquals(AiImageSource.ZOOM, AiImageSource.fromCode(2));
        assertEquals(AiImageSource.IR, AiImageSource.fromCode(3));
        assertEquals(AiImageSource.VISIBLE_LIGHT, AiImageSource.fromCode(7));
        assertThrows(IllegalArgumentException.class, () -> AiImageSource.fromCode(4));
    }

    @Test
    @DisplayName("AiDigitalEffect: code 映射 0=WHITE_HOT, 1=BLACK_HOT, 2=RED_HOT")
    void testAiDigitalEffect() {
        assertEquals(AiDigitalEffect.WHITE_HOT, AiDigitalEffect.fromCode(0));
        assertEquals(AiDigitalEffect.BLACK_HOT, AiDigitalEffect.fromCode(1));
        assertEquals(AiDigitalEffect.RED_HOT, AiDigitalEffect.fromCode(2));
        assertThrows(IllegalArgumentException.class, () -> AiDigitalEffect.fromCode(3));
    }
}
