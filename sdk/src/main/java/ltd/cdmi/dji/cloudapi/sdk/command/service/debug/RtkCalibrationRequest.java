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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;

/**
 * rtk_calibration services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code rtk_calibration} 服务（services 通道，云→设备）的 data。
 * 用于一键 RTK 标定，Dock3 独有，Job 类指令（有进度事件 + need_reply=1）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#RTK_CALIBRATION}
 *
 * <p>字段依据：DJI Dock3 cmd 文档 events 部分（L462-L522）已确认输出结构
 * （ext.devices 数组 + progress.current_step），但 services 请求字段部分被截断。
 * 字段名 {@code cali_type} 来自 coverage-review.html §1.3 描述。
 *
 * <p>标 @Inferred：services 请求字段名/类型待 DJI 文档确认，simulator 未解析请求参数
 * （{@code RemoteDebugSimulator.handle} 注释"当前未使用，预留参数解析"）。
 *
 * <p>events 进度结构（@Verified，simulator 已验证）：
 * <ul>
 *   <li>output.ext.devices[] — 标定设备结果（sn/type/module/result/status）</li>
 *   <li>output.progress.current_step — 固定值 1</li>
 *   <li>need_reply=1（DJI 文档明确）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html">
 * DJI Dock3 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/cmd.html")
@Inferred(
    reason = "DJI Dock3 cmd 文档 services 请求字段被截断，字段名来自 coverage-review.html §1.3 描述；simulator 未解析请求参数",
    verifyPoint = "真机验证 cali_type 字段名、类型和枚举值"
)
public record RtkCalibrationRequest(
    /** 标定类型（enum_int，1=手动标定，待真机验证） */
    int caliType
) {}
