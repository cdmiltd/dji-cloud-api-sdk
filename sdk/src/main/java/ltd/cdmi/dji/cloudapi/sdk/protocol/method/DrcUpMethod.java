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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API DRC 上行（{@code drc/up}）推送方法枚举。
 *
 * <p>DRC 上行通道用于设备→云的状态推送（与 {@link DrcMethod} 的 drc/down 云→设备控制指令方向相反）。
 * 这些推送是 DRC 模式下设备实时上报相机/飞行器/避障/链路/PSDK 状态的核心通道。
 *
 * <p>本枚举覆盖 12 个 drc/up 推送 method（全部 @Verified）：
 * <ul>
 *   <li><b>simulator 已对接验证 10 个</b>：{@link #OSD_INFO_PUSH} / {@link #HSI_INFO_PUSH} /
 *       {@link #DELAY_INFO_PUSH} / {@link #DRC_DRONE_STATE_PUSH} / {@link #DRC_CAMERA_STATE_PUSH} /
 *       {@link #DRC_CAMERA_OSD_INFO_PUSH} / {@link #DRC_PSDK_STATE_INFO} /
 *       {@link #DRC_PSDK_FLOATING_WINDOW_TEXT} / {@link #DRC_SPEAKER_PLAY_PROGRESS} /
 *       {@link #DRC_PSDK_UI_RESOURCE}</li>
 *   <li><b>DJI 文档验证 1 个</b>：{@link #DRC_CAMERA_PHOTO_INFO_PUSH}（simulator 不实现，
 *       按 DJI Dock2/Dock3 remote-control 文档 Data 表验证）</li>
 *   <li><b>v1.16 新增 1 个</b>：{@link #DRC_AI_INFO_PUSH}（Dock3 AI 状态推送，
 *       按 DJI v1.16 remote-control 文档 Data 表验证）</li>
 * </ul>
 *
 * <p><b>信封差异</b>：DRC 上行消息信封为 {@code {method, data, seq}}
 * （DRC 通道特有，无 tid/bid），与 services/events 的 {@code {method, data, tid, bid, timestamp}} 不同。
 * {@code seq} 在 {@code drc_camera_osd_info_push} 与 data 同级，{@code delay_info_push/hsi_info_push/osd_info_push}
 * 文档示例含 {@code timestamp} 字段（simulator 已同时包含 seq 与 timestamp 以覆盖所有场景）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock DRC 通道</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator 已对接 hivemind 验证 10 个 drc/up 推送 method；drc_camera_photo_info_push 按 DJI Dock2/Dock3 remote-control 文档 Data 表验证；drc_ai_info_push 按 DJI v1.16 remote-control 文档 Data 表验证")
public enum DrcUpMethod {

    /** 高频 OSD 信息推送（飞行器位置/姿态/速度/云台角度） */
    OSD_INFO_PUSH("osd_info_push", "DRC OSD 信息推送"),

    /** HSI 避障信息推送（六向距离+开关+工作状态） */
    HSI_INFO_PUSH("hsi_info_push", "HSI 避障信息推送"),

    /** 图传链路延时信息推送（sdr_cmd_delay + 各路码流延时） */
    DELAY_INFO_PUSH("delay_info_push", "链路延迟信息推送"),

    /** 飞行器状态推送（mode_code/stealth_state/night_lights_state/landing_*） */
    DRC_DRONE_STATE_PUSH("drc_drone_state_push", "飞行器状态推送"),

    /** 相机状态推送（payload_index + camera_state + media_storage） */
    DRC_CAMERA_STATE_PUSH("drc_camera_state_push", "相机状态推送"),

    /** 摄像头 OSD 推送（payload_index + wide/zoom/ir_lense + measure_target + liveview） */
    DRC_CAMERA_OSD_INFO_PUSH("drc_camera_osd_info_push", "相机 OSD 推送"),

    /**
     * PSDK 状态信息推送（探照灯/喊话器等 PSDK 设备状态复用同一 method，
     * 通过 {@code psdk_index} 区分设备，{@code light}/{@code speaker} 等子结构承载设备特有字段）。
     */
    DRC_PSDK_STATE_INFO("drc_psdk_state_info", "PSDK 状态信息推送"),

    /** PSDK 浮窗文本推送（{@code psdk_index} + {@code floating_window_text}） */
    DRC_PSDK_FLOATING_WINDOW_TEXT("drc_psdk_floating_window_text", "PSDK 浮窗文本推送"),

    /** 喊话器播放进度推送（{@code psdk_index} + {@code result} + {@code status} + {@code progress} + {@code md5}） */
    DRC_SPEAKER_PLAY_PROGRESS("drc_speaker_play_progress", "喊话器播放进度推送"),

    /** PSDK UI 资源包推送（{@code psdk_index} + {@code psdk_ready} + {@code object_key}） */
    DRC_PSDK_UI_RESOURCE("drc_psdk_ui_resource", "PSDK UI 资源包推送"),

    /**
     * 拍照信息推送（Dock-to-Cloud DRC 上行）。
     *
     * <p>用于全景拍照等持续拍照场景的进度上报，data 含 countdown_time/result/status/progress/ext。
     * simulator 不实现此 method，按 DJI Dock2/Dock3 remote-control 文档 Data 表验证。
     */
    DRC_CAMERA_PHOTO_INFO_PUSH("drc_camera_photo_info_push", "拍照信息推送"),

    /**
     * AI 状态推送（v1.16 新增，Dock3 专属）。
     *
     * <p>推送 AI 目标识别功能相关状态，包括识别/跟随开关、跟随状态与原因、
     * 可用模型列表、当前选中模型与参数、航线 AI 状态等。
     */
    DRC_AI_INFO_PUSH("drc_ai_info_push", "AI 状态推送");

    private final String methodName;
    private final String description;

    DrcUpMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "osd_info_push" */
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
     * @param methodName method 字符串值（如 "osd_info_push"）
     * @return 匹配的枚举；未匹配返回 {@link Optional#empty()}
     */
    public static Optional<DrcUpMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (DrcUpMethod v : values()) {
            if (v.methodName.equals(methodName)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
