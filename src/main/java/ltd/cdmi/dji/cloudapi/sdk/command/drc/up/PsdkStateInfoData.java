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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.up;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_psdk_state_info} 推送数据：PSDK 设备状态信息（探照灯/喊话器复用同一 method）。
 *
 * <p>simulator 通过 {@code psdk_index} 区分设备类型：
 * <ul>
 *   <li>{@code psdk_index=1}：探照灯，{@code light} 子结构承载 work_mode/brightness/calibration_status/calibration_progress/left_value/right_value/wide_field_mode/light_gimbal_control</li>
 *   <li>{@code psdk_index=2}：喊话器，{@code speaker} 子结构承载 work_mode/play_mode/system_state/play_volume/play_file_name/play_file_md5/tts_volume/tts_type/tts_language/tts_speed</li>
 * </ul>
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L285-L325">
 * DeviceSimulator.publishPsdkAndAiEvents 探照灯+喊话器状态部分</a> 已对接 hivemind 验证。
 *
 * <p>顶层字段 ({@code psdk_index}/{@code psdk_type}/{@code psdk_name}/{@code psdk_sn}/
 * {@code psdk_version}/{@code psdk_lib_version}) 为 PSDK 设备通用元数据,
 * 与具体设备类型无关。{@code light}/{@code speaker} 子结构按设备类型选择出现其一,
 * 另一为 {@code null}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_psdk_state_info</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.publishPsdkAndAiEvents 探照灯+喊话器状态部分已对接 hivemind 验证")
public record PsdkStateInfoData(
        /** PSDK 设备索引（1=探照灯, 2=喊话器） */
        int psdkIndex,
        /** PSDK 设备类型（5=大疆自研） */
        int psdkType,
        /** PSDK 设备名称（如 {@code Searchlight}/{@code Speaker}） */
        String psdkName,
        /** PSDK 设备序列号 */
        String psdkSn,
        /** PSDK 固件版本 */
        String psdkVersion,
        /** PSDK 库版本 */
        String psdkLibVersion,
        /** 探照灯状态（{@code psdk_index=1} 时存在，否则为 {@code null}） */
        LightState light,
        /** 喊话器状态（{@code psdk_index=2} 时存在，否则为 {@code null}） */
        SpeakerState speaker
) {
    /**
     * 探照灯状态子结构（{@code psdk_index=1}）。
     *
     * @param workMode            工作模式
     * @param brightness          亮度
     * @param calibrationStatus   校准状态（0=校准完成）
     * @param calibrationProgress 校准进度（0-100）
     * @param leftValue           左侧角度
     * @param rightValue          右侧角度
     * @param wideFieldMode      广角模式
     * @param lightGimbalControl  云台联动控制
     */
    public record LightState(
            int workMode,
            int brightness,
            int calibrationStatus,
            int calibrationProgress,
            int leftValue,
            int rightValue,
            boolean wideFieldMode,
            boolean lightGimbalControl
    ) {}

    /**
     * 喊话器状态子结构（{@code psdk_index=2}）。
     *
     * @param workMode       工作模式（0=TTS 模式）
     * @param playMode       播放模式
     * @param systemState    系统状态（0=空闲, 2=播放中）
     * @param playVolume     播放音量
     * @param playFileName   播放文件名
     * @param playFileMd5    播放文件 MD5
     * @param ttsVolume      TTS 音量
     * @param ttsType        TTS 类型（0=男声）
     * @param ttsLanguage    TTS 语言（0=中文）
     * @param ttsSpeed       TTS 语速
     */
    public record SpeakerState(
            int workMode,
            int playMode,
            int systemState,
            int playVolume,
            String playFileName,
            String playFileMd5,
            int ttsVolume,
            int ttsType,
            int ttsLanguage,
            int ttsSpeed
    ) {}
}
