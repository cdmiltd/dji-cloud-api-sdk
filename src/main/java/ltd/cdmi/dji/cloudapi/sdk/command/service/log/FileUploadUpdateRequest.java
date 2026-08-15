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

package ltd.cdmi.dji.cloudapi.sdk.command.service.log;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code fileupload_update} 指令请求 data：更新日志上传状态（取消）。
 *
 * <p>simulator 解析 {@code status} 字段，若为 {@code "cancel"} 则取消正在进行的上传任务，
 * 返回 {@code {result:0}}。{@code module_list} 字段 simulator 未解析（用于设备端筛选）。
 *
 * <p>字段集依据 DJI Dock3 文档 + simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/RemoteLogSimulator.java#L192-L201">
 * RemoteLogSimulator.handleFileUploadUpdate</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html">
 * DJI Dock3 远程日志 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/log.html")
@Verified(basis = "DJI Dock3 log.html fileupload_update Data 表 + simulator RemoteLogSimulator.handleFileUploadUpdate 已对接 hivemind 验证")
public record FileUploadUpdateRequest(
        /** 上传状态（enum_string：cancel=取消） */
        String status,
        /** 日志所属模块列表（如 {@code ["0","3"]}，{@code "0"=飞行器, "3"=机场}） */
        List<String> moduleList
) {}
