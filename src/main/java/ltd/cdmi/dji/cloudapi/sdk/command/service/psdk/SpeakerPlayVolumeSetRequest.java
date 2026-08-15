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

package ltd.cdmi.dji.cloudapi.sdk.command.service.psdk;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code speaker_play_volume_set} 指令请求 data：设置 PSDK 喊话器播放音量。
 *
 * <p>字段集依据 DJI Dock3 文档 + simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/PsdkSimulator.java#L212-L218">
 * PsdkSimulator.handleVolumeSet</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html">
 * DJI Dock3 PSDK services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html")
@Verified(basis = "DJI Dock3 psdk.html speaker_play_volume_set Data 表 + simulator PsdkSimulator.handleVolumeSet 已对接 hivemind 验证")
public record SpeakerPlayVolumeSetRequest(
        /** PSDK 设备索引 */
        int psdkIndex,
        /** 播放音量（0-100） */
        int playVolume
) {}
