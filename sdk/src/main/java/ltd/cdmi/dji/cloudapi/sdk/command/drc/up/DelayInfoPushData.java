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
 * {@code delay_info_push} 推送数据：图传链路延时信息（sdr_cmd_delay + 各路码流延时）。
 *
 * <p>simulator 默认 sdr_cmd_delay=10ms，广角+变焦两路码流延时 60/80ms。
 * 字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L529-L538">
 * DeviceSimulator.buildDelayInfo()</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC delay_info_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.buildDelayInfo 已对接 hivemind 验证")
public record DelayInfoPushData(
        /** SDR 控制指令延迟（毫秒） */
        int sdrCmdDelay,
        /** 各路码流延时列表 */
        List<LiveviewDelay> liveviewDelayList
) {
    /**
     * 单路码流延时记录。
     *
     * @param videoId           码流 ID，格式 {@code <drone_sn>/<payload_index>/<stream_type>-<index>}
     * @param liveviewDelayTime 码流延时（毫秒）
     */
    public record LiveviewDelay(
            String videoId,
            int liveviewDelayTime
    ) {}
}
