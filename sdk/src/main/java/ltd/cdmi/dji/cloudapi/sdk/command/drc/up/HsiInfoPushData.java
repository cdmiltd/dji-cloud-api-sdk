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
 * {@code hsi_info_push} 推送数据：六向避障信息（距离+开关+工作状态）。
 *
 * <p>simulator 默认所有避障开关启用且正常工作，{@code around_distances} 上报空数组（无障碍物）。
 * 字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L500-L523">
 * DeviceSimulator.buildHsiInfo()</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC hsi_info_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.buildHsiInfo 已对接 hivemind 验证")
public record HsiInfoPushData(
        /** 上方距离（米，整数） */
        int upDistance,
        /** 下方距离（米，整数） */
        int downDistance,
        /** 上方避障开关 */
        boolean upEnable,
        /** 上方避障工作状态 */
        boolean upWork,
        /** 下方避障开关 */
        boolean downEnable,
        /** 下方避障工作状态 */
        boolean downWork,
        /** 左方避障开关 */
        boolean leftEnable,
        /** 左方避障工作状态 */
        boolean leftWork,
        /** 右方避障开关 */
        boolean rightEnable,
        /** 右方避障工作状态 */
        boolean rightWork,
        /** 前方避障开关 */
        boolean frontEnable,
        /** 前方避障工作状态 */
        boolean frontWork,
        /** 后方避障开关 */
        boolean backEnable,
        /** 后方避障工作状态 */
        boolean backWork,
        /** 垂直方向避障开关（综合上/下） */
        boolean verticalEnable,
        /** 垂直方向避障工作状态 */
        boolean verticalWork,
        /** 水平方向避障开关（综合左/右/前/后） */
        boolean horizontalEnable,
        /** 水平方向避障工作状态 */
        boolean horizontalWork,
        /**
         * 360° 周围距离数组，每 30° 一个值（共 12 个），空数组表示任意角度无障碍物。
         *
         * <p>DJI 文档明确：空数组代表无障碍物。
         */
        List<Integer> aroundDistances
) {}
