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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_camera_state_push} 推送数据：相机状态（payload_index + camera_state + media_storage）。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L386-L425">
 * DeviceSimulator.buildDrcCameraState()</a> 已对接 hivemind 验证。
 *
 * <p>{@code night_mode_settings} 子结构为 Dock3 特有，由 drc/down 通道的
 * {@code drc_camera_night_mode_set}/{@code drc_camera_denoise_level_set} 等指令动态控制。
 *
 * <p>{@code photo_format} 字段为 v1.16.1 新增，用于支持红外照片保存为 DLT-664 格式，
 * 仅 Dock3 带 IR 相机时推送，旧固件可能不含此字段（反序列化为 null）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_camera_state_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "DJI Dock3 DRC drc_camera_state_push 字段集（文档 Type 列）：photo_size enum_int{0:4K,4:8K}、interval_photo_interval Type=int（示例 2.5 按 Double 承载）、linkage_zoom_state 保持 int")
public record CameraStatePushData(
        /** 相机挂载索引（如 {@code 165-0-7}） */
        String payloadIndex,
        /** 相机状态子结构 */
        CameraState cameraState,
        /** 媒体存储子结构 */
        MediaStorage mediaStorage
) {
    /**
     * 相机状态子结构。
     *
     * @param cameraMode             相机模式（0=拍照, 1=录像, 2=智能低光, 3=全景拍照, 4=定时拍照）
     * @param intervalPhotoInterval  间隔拍照间隔（秒；文档 Type 列标注 int 但示例为 2.5，按 {@code Double} 承载以兼容小数，§5-27）
     * @param videoResolution        视频分辨率（枚举值）
     * @param linkageZoomState       联动变焦状态（{@code int} 承载，值域 {@code {0:关闭, 1:开启}}；文档 Type 标 bool 与示例 {@code 0} 冲突，以数字示例为准，§5-35）
     * @param photoSize              照片尺寸（enum_int：0=4K, 4=8K，§5-5）
     * @param recordTime             已录像时长（秒）
     * @param recordingState         录像状态（0=未录像, 1=录像中）
     * @param photoState             拍照状态（0=空闲, 1=拍照中）
     * @param remainPhotoNum         剩余可拍照数
     * @param remainRecordDuration   剩余可录像时长（秒）
     * @param nightModeSettings      夜景模式设置（Dock3 特有）
     * @param photoFormat            照片格式（v1.16.1 新增，enum_int：7=RJPEG, 16=DLT664；null 表示旧固件未推送）
     *                              @see ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.PhotoFormat
     */
    public record CameraState(
            int cameraMode,
            Double intervalPhotoInterval,
            String videoResolution,
            int linkageZoomState,
            Integer photoSize,
            int recordTime,
            int recordingState,
            int photoState,
            int remainPhotoNum,
            int remainRecordDuration,
            NightModeSettings nightModeSettings,
            Integer photoFormat
    ) {}

    /**
     * 夜景模式设置（Dock3 特有）。
     *
     * @param nightMode                  夜景模式（enum_int：0=关闭, 1=开启, 2=自动）
     * @param denoiseLevel               降噪等级（enum_int：0=关闭, 1=标准降噪, 2=增强降噪, 3=超强降噪）
     * @param nightVisionEnable          夜视启用
     * @param infraredFillLightEnable    红外补光启用
     * @param nightSceneModeSuggestion   夜景模式建议（值域 {@code {0:无建议, 1:建议开启夜景模式}}）
     * @param isWorking                  是否正在工作（值域 {@code {0:未工作, 1:工作中}}）
     */
    public record NightModeSettings(
            int nightMode,
            int denoiseLevel,
            boolean nightVisionEnable,
            boolean infraredFillLightEnable,
            int nightSceneModeSuggestion,
            int isWorking
    ) {}

    /**
     * 媒体存储子结构。
     *
     * @param photoStorageSettings 照片存储位置列表（取值 {@code {current, vision, ir}}，如 {@code ["current", "ir"]}）
     * @param videoStorageSettings 视频存储位置列表（取值 {@code {current, vision, ir}}，如 {@code ["current", "ir"]}）
     */
    public record MediaStorage(
            List<String> photoStorageSettings,
            List<String> videoStorageSettings
    ) {}
}
