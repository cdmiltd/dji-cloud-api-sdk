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
 * {@code osd_info_push} 推送数据：DRC 模式下飞行器高频 OSD 信息（位置/姿态/速度/云台角度）。
 *
 * <p>simulator 推送频率 0.5Hz（与 services OSD 一致）。
 * 字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L552-L565">
 * DeviceSimulator.buildOsdInfo()</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC osd_info_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.buildOsdInfo 已对接 hivemind 验证")
public record OsdInfoPushData(
        /** 飞行器航向角（度） */
        double attitudeHead,
        /** 纬度（WGS84） */
        double latitude,
        /** 经度（WGS84） */
        double longitude,
        /** 高度（相对起飞点，米） */
        double height,
        /** X 轴速度（m/s，东向为正） */
        double speedX,
        /** Y 轴速度（m/s，北向为正） */
        double speedY,
        /** Z 轴速度（m/s，上为正） */
        double speedZ,
        /** 云台俯仰角（度） */
        double gimbalPitch,
        /** 云台横滚角（度） */
        double gimbalRoll,
        /** 云台航向角（度） */
        double gimbalYaw
) {}
