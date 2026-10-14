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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drone_control} 指令（已废弃）失败回包 data。
 *
 * <p>DJI 文档 {@code drone_control} up Data = {@code {result, output{seq}}}，示例
 * {@code {"result": 319033, "output": {"seq": -1}}}（非 0 错误：无飞行控制权 / 无虚拟摇杆权限 / 序号不对）。
 *
 * <p>成功时不回复（仅异常时回包），故本 record 仅用于异常路径。
 *
 * <p><b>标 @Inferred</b>：{@code drone_control} 为已废弃指令，DJI 文档未详列其失败回包的
 * 完整字段与出现条件。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html">
 * DJI Dock3 远程控制</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.command.drc.flight.DroneControlRequest
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/remote-control.html")
@Verified(basis = "DJI DRC remote-control 文档 drone_control up Data 表 {result(int), output{seq(int)}}")
@Inferred(
        reason = "drone_control 已废弃，DJI 文档未详列出失败回包 output.seq 的完整取值与出现条件",
        verifyPoint = "真机验证 drone_control 失败回包是否严格为 {result, output{seq}}"
)
public record DroneControlReply(
        /** 返回码，非 0 表示失败 */
        Integer result,
        /** 失败详情 */
        Output output
) {

    public DroneControlReply {
        Objects.requireNonNull(result, "result 必填，DJI DRC 回复缺失 result 字段");
    }

    /**
     * 失败详情。
     *
     * @param seq 出现异常的指令序号
     */
    public record Output(
            Integer seq
    ) {}
}
