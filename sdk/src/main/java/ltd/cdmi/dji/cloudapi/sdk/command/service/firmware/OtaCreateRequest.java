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

package ltd.cdmi.dji.cloudapi.sdk.command.service.firmware;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code ota_create} 指令请求 data：发起固件升级任务。
 *
 * <p>simulator 解析设备列表后返回 {@code {result:0, output:{status:"in_progress"}}}
 * 并启动异步进度模拟（download_firmware 50% → upgrade_firmware 100% → ok）。
 * 进度通过 events 通道 {@code ota_progress} 异步上报。
 *
 * <p>字段集依据 DJI Dock3 文档 + simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/OtaSimulator.java#L112-L159">
 * OtaSimulator.handleOtaCreate</a> 已对接 hivemind 验证。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/firmware.html">
 * DJI Dock3 固件升级 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/firmware.html")
@Verified(basis = "DJI Dock3 firmware.html ota_create Data 表 + simulator OtaSimulator.handleOtaCreate 已对接 hivemind 验证")
public record OtaCreateRequest(
        /** 待升级设备列表 */
        List<OtaDevice> devices
) {
    /**
     * 单个待升级设备信息。
     *
     * @param sn                   设备序列号
     * @param productVersion       目标固件版本
     * @param firmwareUpgradeType  固件升级类型（enum_int：2=一致性升级, 3=普通升级, 4=PSDK升级）
     * @param fileUrl              固件文件 URL（可选，simulator 检测 has 后写入）
     * @param md5                  固件文件 MD5（可选）
     * @param fileSize             固件文件大小（字节，可选）
     * @param fileName             固件文件名（可选）
     */
    public record OtaDevice(
            String sn,
            String productVersion,
            int firmwareUpgradeType,
            String fileUrl,
            String md5,
            Long fileSize,
            String fileName
    ) {}
}
