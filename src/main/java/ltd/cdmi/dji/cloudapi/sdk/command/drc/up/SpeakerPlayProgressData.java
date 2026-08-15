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

/**
 * {@code drc_speaker_play_progress} 推送数据：喊话器播放进度。
 *
 * <p>simulator 仅在 {@code speaker_playing=true} 时推送，播放立即完成（status=success, percent=100）。
 * 字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L327-L339">
 * DeviceSimulator.publishPsdkAndAiEvents 喊话器进度部分</a> 已对接 hivemind 验证。
 *
 * <p>status 枚举为 {@code {failed, in_progress, success}}（DRC 通道约束，与 PSDK events 通道
 * 的 {@code {failed, ok, in_progress}} 不同，详见 simulator PsdkSimulator 注释）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_speaker_play_progress</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.publishPsdkAndAiEvents 喊话器进度部分已对接 hivemind 验证")
public record SpeakerPlayProgressData(
        /** PSDK 设备索引 */
        int psdkIndex,
        /** 结果码（0=成功） */
        int result,
        /** 播放状态（枚举字符串：{@code failed}/{@code in_progress}/{@code success}） */
        String status,
        /** 播放进度 */
        Progress progress,
        /** 当前播放文件 MD5（空串表示无文件） */
        String md5
) {
    /**
     * 播放进度子结构。
     *
     * @param stepKey 步骤标识（如 {@code play}）
     * @param percent 进度百分比（0-100）
     */
    public record Progress(
            String stepKey,
            int percent
    ) {}
}
