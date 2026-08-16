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
 * {@code drc_psdk_ui_resource} 推送数据：PSDK UI 资源包。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L341-L346">
 * DeviceSimulator.publishPsdkAndAiEvents UI 资源部分</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_psdk_ui_resource</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.publishPsdkAndAiEvents UI 资源部分已对接 hivemind 验证")
public record PsdkUiResourceData(
        /** PSDK 设备索引（0/1/2...） */
        int psdkIndex,
        /** PSDK 就绪状态（1=就绪，0=未就绪） */
        int psdkReady,
        /** 资源对象 key（对象存储路径，如 {@code psdk_config/0/ui_resource.tar.gz}） */
        String objectKey
) {}
