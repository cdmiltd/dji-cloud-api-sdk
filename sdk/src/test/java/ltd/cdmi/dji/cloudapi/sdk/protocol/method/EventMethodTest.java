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

package ltd.cdmi.dji.cloudapi.sdk.protocol.method;

import ltd.cdmi.dji.cloudapi.sdk.protocol.envelope.EventEnvelope;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link EventMethod} 枚举的 {@code needReply} 属性正确性与 {@link EventEnvelope#of} 集成。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>枚举总数为 29（覆盖 DJI Cloud API events 通道所有 method）</li>
 *   <li>{@code needReply=1}（需平台回复）的事件共 18 个</li>
 *   <li>{@code needReply=0}（单向通知）的事件共 11 个</li>
 *   <li>{@link EventEnvelope#of} 从 {@link EventMethod#needReply()} 自动填充 {@code needReply}</li>
 * </ol>
 */
class EventMethodTest {

    // ==================== 枚举总数 ====================

    @Test
    @DisplayName("枚举总数应为 29")
    void testTotalCount() {
        assertEquals(29, EventMethod.values().length);
    }

    // ==================== needReply 属性验证 ====================

    @Test
    @DisplayName("need_reply=1 的事件共 18 个（需平台回复）")
    void testNeedReplyOne() {
        long count = java.util.Arrays.stream(EventMethod.values())
                .filter(m -> m.needReply() == 1)
                .count();
        assertEquals(18, count);
    }

    @Test
    @DisplayName("need_reply=0 的事件共 11 个（单向通知）")
    void testNeedReplyZero() {
        long count = java.util.Arrays.stream(EventMethod.values())
                .filter(m -> m.needReply() == 0)
                .count();
        assertEquals(11, count);
    }

    @Test
    @DisplayName("need_reply 取值仅允许 0 或 1")
    void testNeedReplyValueRange() {
        for (EventMethod m : EventMethod.values()) {
            assertTrue(m.needReply() == 0 || m.needReply() == 1,
                    m.name() + " needReply=" + m.needReply() + " 不合法");
        }
    }

    // ==================== needReply 代表性抽样验证 ====================

    @Test
    @DisplayName("need_reply=1 代表事件抽样验证")
    void testNeedReplyOneSpotCheck() {
        assertEquals(1, EventMethod.FLIGHTTASK_PROGRESS.needReply());
        assertEquals(1, EventMethod.FLY_TO_POINT_PROGRESS.needReply());
        assertEquals(1, EventMethod.OTA_PROGRESS.needReply());
        assertEquals(1, EventMethod.FILE_UPLOAD_CALLBACK.needReply());
        assertEquals(1, EventMethod.DEVICE_EXIT_HOMING_NOTIFY.needReply());
        assertEquals(1, EventMethod.OBSTACLE_AVOIDANCE_NOTIFY.needReply());
        assertEquals(1, EventMethod.JOYSTICK_INVALID_NOTIFY.needReply());
        assertEquals(1, EventMethod.SPEAKER_TTS_PLAY_START_PROGRESS.needReply());
        assertEquals(1, EventMethod.AIRSENSE_WARNING.needReply(), "DJI Dock2 airsense.html Example 明确 need_reply:1");
    }

    @Test
    @DisplayName("need_reply=0 代表事件抽样验证")
    void testNeedReplyZeroSpotCheck() {
        assertEquals(0, EventMethod.FLIGHTTASK_READY.needReply());
        assertEquals(0, EventMethod.RETURN_HOME_INFO.needReply());
        assertEquals(0, EventMethod.FLIGHT_AREAS_DRONE_LOCATION.needReply());
        assertEquals(0, EventMethod.CLOUD_CONTROL_AUTH_NOTIFY.needReply());
        assertEquals(0, EventMethod.FILEUPLOAD_PROGRESS.needReply(), "DJI Dock2 log.html Example 明确 need_reply:0");
        assertEquals(0, EventMethod.HMS.needReply());
        assertEquals(0, EventMethod.PSDK_FLOATING_WINDOW_TEXT.needReply());
        assertEquals(0, EventMethod.DRC_STATUS_NOTIFY.needReply());
    }

    // ==================== EventEnvelope.of 集成验证 ====================

    @Test
    @DisplayName("EventEnvelope.of — need_reply=1 事件自动填充")
    void testEnvelopeOfNeedReplyOne() {
        EventEnvelope env = EventEnvelope.of(
                "tid-1", "bid-1", 1700000000000L,
                EventMethod.FLIGHTTASK_PROGRESS, null, "dock-sn");
        assertEquals("flighttask_progress", env.method());
        assertEquals(1, env.needReply());
        assertEquals("dock-sn", env.gateway());
    }

    @Test
    @DisplayName("EventEnvelope.of — need_reply=0 事件自动填充")
    void testEnvelopeOfNeedReplyZero() {
        EventEnvelope env = EventEnvelope.of(
                "tid-2", "bid-2", 1700000000000L,
                EventMethod.HMS, null, null);
        assertEquals("hms", env.method());
        assertEquals(0, env.needReply());
        assertEquals(null, env.gateway());
    }

    // ==================== fromMethodName 反查 ====================

    @Test
    @DisplayName("fromMethodName — 全部 29 个 method 可反查")
    void testFromMethodNameAll() {
        for (EventMethod m : EventMethod.values()) {
            assertEquals(m, EventMethod.fromMethodName(m.methodName()).orElseThrow());
        }
    }

    @Test
    @DisplayName("fromMethodName — 未知 method 返回 empty")
    void testFromMethodNameUnknown() {
        assertTrue(EventMethod.fromMethodName("nonexistent_method").isEmpty());
        assertTrue(EventMethod.fromMethodName(null).isEmpty());
        assertTrue(EventMethod.fromMethodName("").isEmpty());
    }
}
