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

import java.util.Optional;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API DRC（Dock Remote Control）通道的 method 名称枚举。
 *
 * <p>DRC 通道用于云对设备的实时控制指令（{@code drc/down} 云→设备）与状态推送
 * （{@code drc/up} 设备→云），主要在 DRC 模式下使用。
 *
 * <p>本枚举当前覆盖 simulator {@code dji-method-catalog.json} 已盘点的 19 个 method + v1.16 新增 11 个 AI 方法：
 * <ul>
 *   <li><b>三 Dock 共有（4 个）</b>：{@link #DRONE_EMERGENCY_STOP} /
 *       {@link #STICK_CONTROL} / {@link #DRONE_CONTROL} / {@link #HEART_BEAT}</li>
 *   <li><b>Dock2 专属（2 个，Dock3 文档未找到待真机验证）</b>：{@link #DRC_FORCE_LANDING} /
 *       {@link #DRC_EMERGENCY_LANDING}</li>
 *   <li><b>Dock3 独有（13 个）</b>：{@link #DRC_CAMERA_NIGHT_MODE_SET} /
 *       {@link #DRC_CAMERA_DENOISE_LEVEL_SET} / {@link #DRC_CAMERA_NIGHT_VISION_ENABLE} /
 *       {@link #DRC_INFRARED_FILL_LIGHT_ENABLE} / {@link #DRC_LIGHT_BRIGHTNESS_SET} /
 *       {@link #DRC_LIGHT_MODE_SET} / {@link #DRC_LIGHT_FINE_TUNING_SET} /
 *       {@link #DRC_LIGHT_CALIBRATION} / {@link #DRC_SPEAKER_PLAY_MODE_SET} /
 *       {@link #DRC_SPEAKER_TTS_SET} / {@link #DRC_SPEAKER_PLAY_VOLUME_SET} /
 *       {@link #DRC_SPEAKER_PLAY_STOP} / {@link #DRC_SPEAKER_REPLAY}</li>
 *   <li><b>Dock3 AI 目标识别（11 个，v1.16 新增）</b>：{@link #DRC_AI_MODEL_SELECT} /
 *       {@link #DRC_AI_IDENTIFY_SET} / {@link #DRC_AI_IDENTIFY_SCORE_MODE_SET} /
 *       {@link #DRC_AI_IDENTIFY_SCORE_SET} / {@link #DRC_AI_IDENTIFY_SCORE_RESET} /
 *       {@link #DRC_AI_IDENTIFY_FILTER_SET} / {@link #DRC_AI_SPOTLIGHT_ZOOM_SET} /
 *       {@link #DRC_AI_SPOTLIGHT_ZOOM_TRACK} / {@link #DRC_AI_SPOTLIGHT_ZOOM_SELECT} /
 *       {@link #DRC_AI_SPOTLIGHT_ZOOM_CONFIRM} / {@link #DRC_AI_SPOTLIGHT_ZOOM_STOP}</li>
 * </ul>
 *
 * <p>当前共 42 个 method（19 simulator catalog 盘点 + 11 v1.16 AI 新增 + 1 Pilot-to-Cloud DRC 专属 + 11 DRC 状态与相机参数控制）。Pilot-to-Cloud DRC 通道中与 services 通道
 * 同名的方法（如 {@code camera_mode_switch} / {@code camera_aim} / {@code camera_focal_length_set} /
 * {@code gimbal_reset} 等）已在 {@link ServiceMethod} 中定义且有专用 POJO，本枚举不重复定义——
 * 由调用方根据 topic（{@code drc/down} vs {@code services})判断通道归属。
 * 原 11 个 DRC 上行推送方法（{@code osd_info_push} 等）已迁移至 {@link DrcUpMethod} 枚举。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock DRC 通道</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator dji-method-catalog.json drc_down 已对接 hivemind 盘点 19 个 method；v1.16 新增 11 个 AI 方法按 DJI 文档 Data 表核实；Pilot-to-Cloud drc_live_lens_change 按 DJI Pilot-to-Cloud drc.html 文档核实")
public enum DrcMethod {

    // ===== 三 Dock 共有（4 个） =====

    /** 飞行器紧急停止（DJI Dock2 remote-control 文档确认，drc_force_landing 描述中引用） */
    DRONE_EMERGENCY_STOP("drone_emergency_stop", "飞行器紧急停止"),

    /** 摇杆控制（实时推送控制量） */
    STICK_CONTROL("stick_control", "摇杆控制"),

    /** 飞行器控制（综合控制指令） */
    DRONE_CONTROL("drone_control", "飞行器控制"),

    /**
     * DRC 心跳（保持 DRC 连接活跃）。
     *
     * <p>协议格式与发起方未在 DJI 文档明确，标 {@link Inferred}。
     */
    @Inferred(
            reason = "heart_beat 协议格式与发起方未在 DJI 文档明确",
            verifyPoint = "待真机/文档确认 heart_beat 的发起方（设备 or 云）与字段结构"
    )
    HEART_BEAT("heart_beat", "DRC 心跳"),

    // ===== Dock2 专属（2 个，Dock3 文档未找到待真机验证） =====

    /** DRC 强制降落（DJI Dock2 remote-control 文档确认） */
    DRC_FORCE_LANDING("drc_force_landing", "DRC 强制降落"),

    /** DRC 紧急降落（DJI Dock2 remote-control 文档确认） */
    DRC_EMERGENCY_LANDING("drc_emergency_landing", "DRC 紧急降落"),

    // ===== Dock3 独有（13 个） =====

    /** 相机夜景模式设置 */
    DRC_CAMERA_NIGHT_MODE_SET("drc_camera_night_mode_set", "相机夜景模式设置"),

    /** 相机降噪等级设置 */
    DRC_CAMERA_DENOISE_LEVEL_SET("drc_camera_denoise_level_set", "相机降噪等级设置"),

    /** 相机夜视启用 */
    DRC_CAMERA_NIGHT_VISION_ENABLE("drc_camera_night_vision_enable", "相机夜视启用"),

    /** 红外补光启用 */
    DRC_INFRARED_FILL_LIGHT_ENABLE("drc_infrared_fill_light_enable", "红外补光启用"),

    /** 补光灯亮度设置 */
    DRC_LIGHT_BRIGHTNESS_SET("drc_light_brightness_set", "补光灯亮度设置"),

    /** 补光灯模式设置 */
    DRC_LIGHT_MODE_SET("drc_light_mode_set", "补光灯模式设置"),

    /** 补光灯微调设置 */
    DRC_LIGHT_FINE_TUNING_SET("drc_light_fine_tuning_set", "补光灯微调设置"),

    /** 补光灯校准 */
    DRC_LIGHT_CALIBRATION("drc_light_calibration", "补光灯校准"),

    /** 喊话器播放模式设置 */
    DRC_SPEAKER_PLAY_MODE_SET("drc_speaker_play_mode_set", "喊话器播放模式设置"),

    /** 喊话器 TTS 设置 */
    DRC_SPEAKER_TTS_SET("drc_speaker_tts_set", "喊话器 TTS 设置"),

    /** 喊话器播放音量设置 */
    DRC_SPEAKER_PLAY_VOLUME_SET("drc_speaker_play_volume_set", "喊话器播放音量设置"),

    /** 喊话器停止播放 */
    DRC_SPEAKER_PLAY_STOP("drc_speaker_play_stop", "喊话器停止播放"),

    /** 喊话器重新播放 */
    DRC_SPEAKER_REPLAY("drc_speaker_replay", "喊话器重新播放"),

    // ===== Dock3 AI 目标识别（11 个，v1.16 新增） =====

    /** AI 模型选择（v1.16 新增，Dock3 专属） */
    DRC_AI_MODEL_SELECT("drc_ai_model_select", "AI 模型选择"),

    /** AI 识别开关设置（v1.16 新增，Dock3 专属） */
    DRC_AI_IDENTIFY_SET("drc_ai_identify_set", "AI 识别开关设置"),

    /** 设置 AI 识别置信度模式（v1.16 新增，Dock3 专属） */
    DRC_AI_IDENTIFY_SCORE_MODE_SET("drc_ai_identify_score_mode_set", "设置 AI 识别置信度模式"),

    /** 设置 AI 识别置信度（v1.16 新增，Dock3 专属） */
    DRC_AI_IDENTIFY_SCORE_SET("drc_ai_identify_score_set", "设置 AI 识别置信度"),

    /** 重置 AI 识别置信度（v1.16 新增，Dock3 专属） */
    DRC_AI_IDENTIFY_SCORE_RESET("drc_ai_identify_score_reset", "重置 AI 识别置信度"),

    /** 设置 AI 识别目标过滤列表（v1.16 新增，Dock3 专属） */
    DRC_AI_IDENTIFY_FILTER_SET("drc_ai_identify_filter_set", "设置 AI 识别目标过滤列表"),

    /** AI 跟随开关设置（v1.16 新增，Dock3 专属） */
    DRC_AI_SPOTLIGHT_ZOOM_SET("drc_ai_spotlight_zoom_set", "AI 跟随开关设置"),

    /** AI 识别目标跟随（v1.16 新增，Dock3 专属） */
    DRC_AI_SPOTLIGHT_ZOOM_TRACK("drc_ai_spotlight_zoom_track", "AI 识别目标跟随"),

    /** AI 框选目标跟随（v1.16 新增，Dock3 专属） */
    DRC_AI_SPOTLIGHT_ZOOM_SELECT("drc_ai_spotlight_zoom_select", "AI 框选目标跟随"),

    /** AI 框选目标跟随确认（v1.16 新增，Dock3 专属） */
    DRC_AI_SPOTLIGHT_ZOOM_CONFIRM("drc_ai_spotlight_zoom_confirm", "AI 框选目标跟随确认"),

    /** 停止目标跟随（v1.16 新增，Dock3 专属） */
    DRC_AI_SPOTLIGHT_ZOOM_STOP("drc_ai_spotlight_zoom_stop", "停止目标跟随"),

    // ===== Pilot-to-Cloud DRC 专属（1 个） =====

    /**
     * DRC 镜头切换（RC Plus 2 专属）。
     *
     * <p>Pilot-to-Cloud DRC 通道指令，与 Service 通道的 {@code live_lens_change} 名字不同
     *（前缀 {@code drc_}），非同名方法，需在本枚举独立定义。
     *
     * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/live.html">
     * DJI RC Plus 2 live 通道</a>
     */
    DRC_LIVE_LENS_CHANGE("drc_live_lens_change", "DRC 镜头切换（RC Plus 2 专属）"),

    // ===== DRC 状态与相机参数控制（11 个） =====

    /** DRC 初始状态订阅（设备进入 DRC 模式后，平台订阅初始状态） */
    DRC_INITIAL_STATE_SUBSCRIBE("drc_initial_state_subscribe", "DRC 初始状态订阅"),

    /** 隐蔽模式设置（保存到设备状态，影响 drc_drone_state_push） */
    DRC_STEALTH_STATE_SET("drc_stealth_state_set", "隐蔽模式设置"),

    /** 夜航灯设置（保存到设备状态，影响 drc_drone_state_push） */
    DRC_NIGHT_LIGHTS_STATE_SET("drc_night_lights_state_set", "夜航灯设置"),

    /**
     * 镜头去畸变设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法（{@code camera_dewarping_set}），
     * 若存在则应迁移至 {@link ServiceMethod}，本枚举标记为 DRC 专属待验证。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道也存在 camera_dewarping_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_CAMERA_DEWARPING_SET("drc_camera_dewarping_set", "镜头去畸变设置"),

    /**
     * 机械快门设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道也存在 camera_mechanical_shutter_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_CAMERA_MECHANICAL_SHUTTER_SET("drc_camera_mechanical_shutter_set", "机械快门设置"),

    /**
     * ISO 设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道也存在 camera_iso_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_CAMERA_ISO_SET("drc_camera_iso_set", "ISO 设置"),

    /**
     * 相机快门设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道也存在 camera_shutter_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_CAMERA_SHUTTER_SET("drc_camera_shutter_set", "相机快门设置"),

    /**
     * 相机光圈设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道也存在 camera_aperture_value_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_CAMERA_APERTURE_VALUE_SET("drc_camera_aperture_value_set", "相机光圈设置"),

    /**
     * 定时拍照间隔设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道是否存在 interval_photo_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_INTERVAL_PHOTO_SET("drc_interval_photo_set", "定时拍照间隔设置"),

    /**
     * 视频分辨率设置。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道是否存在 video_resolution_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_VIDEO_RESOLUTION_SET("drc_video_resolution_set", "视频分辨率设置"),

    /**
     * 红外联动变焦（仅 M3TD）。
     * <p>{@code @Inferred}：未确认是否在 Services 通道也存在同名方法。
     */
    @Inferred(
            reason = "未确认是否在 Services 通道是否存在 linkage_zoom_set",
            verifyPoint = "待 DJI 文档补充确认 Services 通道是否存在同名方法"
    )
    DRC_LINKAGE_ZOOM_SET("drc_linkage_zoom_set", "红外联动变焦");

    private final String methodName;
    private final String description;

    DrcMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "drc_force_landing" */
    public String methodName() {
        return methodName;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }

    /**
     * 按 method 字符串反查枚举。
     *
     * <p>method 名随固件升级可能扩展，未知 method 是正常情况，故返回 {@link Optional}
     * 而非抛异常（差异化错误处理策略见 SDK design doc §3.5）。
     *
     * @param methodName method 字符串值
     * @return 匹配的枚举；未匹配返回 {@link Optional#empty()}
     */
    public static Optional<DrcMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (DrcMethod m : values()) {
            if (m.methodName.equals(methodName)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
