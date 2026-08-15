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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

/**
 * {@code drc_camera_photo_info_push} 推送数据：拍照信息上报（Dock-to-Cloud DRC 上行）。
 *
 * <p>DRC 通道上行消息信封为 {@code {method, data, seq}}（无 tid/bid），
 * 本 record 对应 {@code data} 部分。
 *
 * <p>用于全景拍照等持续拍照场景的进度上报，{@code status} 标识整体状态，
 * {@code progress} 含步骤码与百分比，{@code ext.camera_mode} 类型化绑定 {@link CameraMode}。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Progress} — 进度（current_step/percent）</li>
 *   <li>{@link Ext} — 扩展内容（camera_mode，类型化枚举 {@link CameraMode}）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html">
 * DJI Dock3 指令飞行 — 拍照信息上报</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.DrcUpMethod#DRC_CAMERA_PHOTO_INFO_PUSH
 * @see CameraMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "DJI Dock2/Dock3 remote-control 文档 drc_camera_photo_info_push Data 表")
public record CameraPhotoInfoPushData(
        /** 定时拍倒计时 */
        Integer countdownTime,
        /** 拍照结果，非 0 代表错误 */
        Integer result,
        /** 状态（enum_string：fail=失败/in_progress=执行中/ok=完成） */
        String status,
        /** 进度 */
        Progress progress,
        /** 扩展内容 */
        Ext ext
) {
    /** progress 字段，步进进度。 */
    public record Progress(
            /** 执行步骤（enum_int：3000=全景图拍摄未开始或已结束/3002=全景图正在拍摄/3005=全景图合成中） */
            Integer currentStep,
            /** 进度值（0-100） */
            Integer percent
    ) {}

    /** ext 字段，扩展内容。 */
    public record Ext(
            /** 当前相机模式（类型化枚举，Jackson 自动绑定 int↔enum，见 {@link CameraMode}） */
            CameraMode cameraMode
    ) {}
}
