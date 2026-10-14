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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcMethod} 枚举的 method 字符串映射与反查。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC 下行 30 个 method（19 个 simulator catalog + 11 个 v1.16 AI 新增）字符串
 *       能通过 {@link DrcMethod#fromMethodName(String)} 反查到枚举常量</li>
 *   <li>{@link DrcMethod#HEART_BEAT} 标注 {@link Inferred}，reason/verifyPoint 非空</li>
 *   <li>所有 methodName 唯一无重复，methodName/description 非空</li>
 * </ol>
 */
class DrcMethodTest {

    @Test
    @DisplayName("枚举总数应为 68（42 + v1.18 新增 26 个 drc_ 相机/云台方法）")
    void testTotalCount() {
        assertEquals(68, DrcMethod.values().length);
    }

    @Test
    @DisplayName("v1.18 新增 drc_ 相机/云台方法反查（抽样）")
    void testV118NewDrcMethods() {
        assertEquals(DrcMethod.DRC_CAMERA_PHOTO_TAKE, DrcMethod.fromMethodName("drc_camera_photo_take").orElseThrow());
        assertEquals(DrcMethod.DRC_CAMERA_RECORDING_START, DrcMethod.fromMethodName("drc_camera_recording_start").orElseThrow());
        assertEquals(DrcMethod.DRC_CAMERA_MODE_SWITCH, DrcMethod.fromMethodName("drc_camera_mode_switch").orElseThrow());
        assertEquals(DrcMethod.DRC_GIMBAL_RESET, DrcMethod.fromMethodName("drc_gimbal_reset").orElseThrow());
        assertEquals(DrcMethod.DRC_IR_METERING_MODE_SET, DrcMethod.fromMethodName("drc_ir_metering_mode_set").orElseThrow());
        assertEquals(DrcMethod.DRC_PSDK_WIDGET_VALUE_SET, DrcMethod.fromMethodName("drc_psdk_widget_value_set").orElseThrow());
        assertEquals(DrcMethod.DRC_VIDEO_STORAGE_SET, DrcMethod.fromMethodName("drc_video_storage_set").orElseThrow());
    }

    @Test
    @DisplayName("methodName 与 description 非空")
    void testMethodNameAndDescription() {
        for (DrcMethod m : DrcMethod.values()) {
            assertTrue(!m.methodName().isBlank());
            assertTrue(!m.description().isBlank());
        }
    }

    @Test
    @DisplayName("fromMethodName 反查已知 method（三 Dock 共有 + Dock2/Dock3 专属）")
    void testFromMethodNameKnownMethods() {
        // 三 Dock 共有（4 个）
        assertEquals(DrcMethod.DRONE_EMERGENCY_STOP, DrcMethod.fromMethodName("drone_emergency_stop").orElseThrow());
        assertEquals(DrcMethod.STICK_CONTROL, DrcMethod.fromMethodName("stick_control").orElseThrow());
        assertEquals(DrcMethod.DRONE_CONTROL, DrcMethod.fromMethodName("drone_control").orElseThrow());
        assertEquals(DrcMethod.HEART_BEAT, DrcMethod.fromMethodName("heart_beat").orElseThrow());
        // Dock2 专属（2 个）
        assertEquals(DrcMethod.DRC_FORCE_LANDING, DrcMethod.fromMethodName("drc_force_landing").orElseThrow());
        assertEquals(DrcMethod.DRC_EMERGENCY_LANDING, DrcMethod.fromMethodName("drc_emergency_landing").orElseThrow());
        // Dock3 独有（13 个，抽样验证）
        assertEquals(DrcMethod.DRC_CAMERA_NIGHT_MODE_SET, DrcMethod.fromMethodName("drc_camera_night_mode_set").orElseThrow());
        assertEquals(DrcMethod.DRC_LIGHT_BRIGHTNESS_SET, DrcMethod.fromMethodName("drc_light_brightness_set").orElseThrow());
        assertEquals(DrcMethod.DRC_SPEAKER_TTS_SET, DrcMethod.fromMethodName("drc_speaker_tts_set").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 反查 11 个 v1.16 新增 AI method")
    void testFromMethodNameAiMethods() {
        // v1.16 新增 Dock3 AI 目标识别（11 个）
        assertEquals(DrcMethod.DRC_AI_MODEL_SELECT, DrcMethod.fromMethodName("drc_ai_model_select").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_IDENTIFY_SET, DrcMethod.fromMethodName("drc_ai_identify_set").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_IDENTIFY_SCORE_MODE_SET, DrcMethod.fromMethodName("drc_ai_identify_score_mode_set").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_IDENTIFY_SCORE_SET, DrcMethod.fromMethodName("drc_ai_identify_score_set").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_IDENTIFY_SCORE_RESET, DrcMethod.fromMethodName("drc_ai_identify_score_reset").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_IDENTIFY_FILTER_SET, DrcMethod.fromMethodName("drc_ai_identify_filter_set").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_SPOTLIGHT_ZOOM_SET, DrcMethod.fromMethodName("drc_ai_spotlight_zoom_set").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_SPOTLIGHT_ZOOM_TRACK, DrcMethod.fromMethodName("drc_ai_spotlight_zoom_track").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_SPOTLIGHT_ZOOM_SELECT, DrcMethod.fromMethodName("drc_ai_spotlight_zoom_select").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_SPOTLIGHT_ZOOM_CONFIRM, DrcMethod.fromMethodName("drc_ai_spotlight_zoom_confirm").orElseThrow());
        assertEquals(DrcMethod.DRC_AI_SPOTLIGHT_ZOOM_STOP, DrcMethod.fromMethodName("drc_ai_spotlight_zoom_stop").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 未匹配/null/空字符串返回空 Optional")
    void testFromMethodNameUnknownReturnsEmpty() {
        assertTrue(DrcMethod.fromMethodName("unknown_method").isEmpty());
        assertTrue(DrcMethod.fromMethodName("").isEmpty());
        assertTrue(DrcMethod.fromMethodName(null).isEmpty());
    }

    @Test
    @DisplayName("所有 methodName 唯一无重复")
    void testUniqueMethodNames() {
        Set<String> seen = new HashSet<>();
        for (DrcMethod m : DrcMethod.values()) {
            assertTrue(seen.add(m.methodName()),
                    () -> "Duplicate methodName: " + m.methodName());
        }
        assertEquals(DrcMethod.values().length, seen.size());
        // 稳健性：用流式断言再校验一次总数一致
        long distinctCount = Arrays.stream(DrcMethod.values()).map(DrcMethod::methodName).distinct().count();
        assertEquals(DrcMethod.values().length, distinctCount);
    }

    @Test
    @DisplayName("HEART_BEAT 升格 @Verified，basis 非空")
    void testHeartBeatVerified() throws NoSuchFieldException {
        Verified ver = DrcMethod.class.getField("HEART_BEAT").getAnnotation(Verified.class);
        assertNotNull(ver, "HEART_BEAT 必须标注 @Verified");
        assertTrue(!ver.basis().isBlank(), "basis 不能为空");
    }
}
