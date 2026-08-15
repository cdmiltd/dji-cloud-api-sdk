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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code in_flight_wayline_deliver} 指令请求 data：空中下发航线。
 *
 * <p>飞行器处于空中飞行时，下发文件体积较小的航线。
 * simulator 解析所有请求字段并记录日志，回复 {@code {result:0}}。
 *
 * <p>字段集依据 DJI Dock3 文档 + simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/WaylineTaskSimulator.java#L620-L642">
 * WaylineTaskSimulator.handleInFlightWaylineDeliver</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html">
 * DJI Dock3 航线管理 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/wayline.html")
@Verified(basis = "DJI Dock3 wayline.html in_flight_wayline_deliver Data 表 + simulator WaylineTaskSimulator.handleInFlightWaylineDeliver 已对接 hivemind 验证")
public record InFlightWaylineDeliverRequest(
        /** 空中航线 ID */
        String inFlightWaylineId,
        /** 航线文件信息 */
        InFlightWaylineFile file,
        /** 失控行为（0=无动作, 1=返航, ...；可选，未提供时为 null） */
        Integer outOfControlAction,
        /** RC 丢失时是否退出航线（可选，未提供时为 null） */
        Integer exitWaylineWhenRcLost,
        /** 返航高度（米，相对起飞点；可选） */
        Integer rthAltitude,
        /** 返航模式（可选） */
        Integer rthMode,
        /** 航线精度类型（可选） */
        Integer waylinePrecisionType
) {
    /**
     * 空中航线文件信息。
     *
     * @param url        航线文件下载 URL
     * @param fingerprint 航线文件指纹（MD5）
     */
    public record InFlightWaylineFile(
            String url,
            String fingerprint
    ) {}
}
