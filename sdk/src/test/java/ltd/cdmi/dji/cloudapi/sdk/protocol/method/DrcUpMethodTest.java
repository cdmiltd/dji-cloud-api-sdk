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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcUpMethod} 枚举的 method 字符串映射与反查。
 *
 * <p><b>核心证明</b>：simulator 推送的 10 个 drc/up method 字符串
 * （{@code osd_info_push} 等）能通过 {@link DrcUpMethod#fromMethodName(String)} 反查到枚举常量，
 * 说明 SDK 已覆盖 simulator 对接 hivemind 验证的所有 drc/up 推送方法。
 */
class DrcUpMethodTest {

    @Test
    @DisplayName("fromMethodName 反查 10 个 simulator 已实现 method")
    void testFromMethodNameSimulatorMethods() {
        // simulator DeviceSimulator.publishDrcEvents + publishPsdkAndAiEvents 中实际推送的 10 个 method
        assertEquals(DrcUpMethod.OSD_INFO_PUSH, DrcUpMethod.fromMethodName("osd_info_push").orElseThrow());
        assertEquals(DrcUpMethod.HSI_INFO_PUSH, DrcUpMethod.fromMethodName("hsi_info_push").orElseThrow());
        assertEquals(DrcUpMethod.DELAY_INFO_PUSH, DrcUpMethod.fromMethodName("delay_info_push").orElseThrow());
        assertEquals(DrcUpMethod.DRC_DRONE_STATE_PUSH, DrcUpMethod.fromMethodName("drc_drone_state_push").orElseThrow());
        assertEquals(DrcUpMethod.DRC_CAMERA_STATE_PUSH, DrcUpMethod.fromMethodName("drc_camera_state_push").orElseThrow());
        assertEquals(DrcUpMethod.DRC_CAMERA_OSD_INFO_PUSH, DrcUpMethod.fromMethodName("drc_camera_osd_info_push").orElseThrow());
        assertEquals(DrcUpMethod.DRC_PSDK_STATE_INFO, DrcUpMethod.fromMethodName("drc_psdk_state_info").orElseThrow());
        assertEquals(DrcUpMethod.DRC_PSDK_FLOATING_WINDOW_TEXT, DrcUpMethod.fromMethodName("drc_psdk_floating_window_text").orElseThrow());
        assertEquals(DrcUpMethod.DRC_SPEAKER_PLAY_PROGRESS, DrcUpMethod.fromMethodName("drc_speaker_play_progress").orElseThrow());
        assertEquals(DrcUpMethod.DRC_PSDK_UI_RESOURCE, DrcUpMethod.fromMethodName("drc_psdk_ui_resource").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 反查 1 个 Pilot 专属 method（@Inferred）")
    void testFromMethodNamePilotMethod() {
        // Pilot 上云专属，simulator 不实现但 SDK 占位
        assertEquals(DrcUpMethod.DRC_CAMERA_PHOTO_INFO_PUSH, DrcUpMethod.fromMethodName("drc_camera_photo_info_push").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 反查 1 个 v1.16 新增 AI 状态推送 method")
    void testFromMethodNameAiMethod() {
        // v1.16 新增 Dock3 AI 状态推送
        assertEquals(DrcUpMethod.DRC_AI_INFO_PUSH, DrcUpMethod.fromMethodName("drc_ai_info_push").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 未匹配返回空 Optional")
    void testFromMethodNameUnknownReturnsEmpty() {
        assertTrue(DrcUpMethod.fromMethodName("unknown_method").isEmpty());
        assertTrue(DrcUpMethod.fromMethodName("").isEmpty());
        assertTrue(DrcUpMethod.fromMethodName(null).isEmpty());
    }

    @Test
    @DisplayName("枚举总数应为 12（10 个 simulator 实现 + 1 个 Pilot @Inferred + 1 个 v1.16 AI）")
    void testTotalCount() {
        assertEquals(12, DrcUpMethod.values().length);
    }

    @Test
    @DisplayName("methodName 与 description 非空")
    void testMethodNameAndDescription() {
        for (DrcUpMethod m : DrcUpMethod.values()) {
            assertTrue(!m.methodName().isBlank());
            assertTrue(!m.description().isBlank());
        }
    }
}
