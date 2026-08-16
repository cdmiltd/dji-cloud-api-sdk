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

package ltd.cdmi.dji.cloudapi.sdk.flow;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

import java.util.List;

/**
 * Pilot 上云注册流程定义。
 *
 * <p>Pilot 模式（遥控器 RC 作为网关）注册流程与机场类似，同为 5 步序列：
 * config → airport_bind_status → airport_organization_get → airport_organization_bind → update_topo。
 * 与 {@link DockRegistrationFlow} 的差异：
 * <ul>
 *   <li>update_topo 通过 {@code thing/product/{gateway_sn}/status} 上报（机场为 {@code sys/product/{gateway_sn}/status}）</li>
 *   <li>RC Plus 2 的 update_topo 子设备不上报 domain/index 字段</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/feature-set/pilot-feature-set/pilot-access-to-cloud.html">Pilot 接入第三方平台</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/feature-set/pilot-feature-set/pilot-access-to-cloud.html")
@Verified(basis = "DJI 官方文档 Pilot 接入流程：config → airport_bind_status → airport_organization_get → airport_organization_bind → update_topo，注册步骤与机场一致")
public final class PilotRegistrationFlow {

    private PilotRegistrationFlow() {
    }

    /**
     * 步骤 1：config — 获取配置（Pilot 模式与机场一致）。
     * <p>请求 data 结构：{@code {config_type:"json", config_scope:"product"}}。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/config.html")
    @Verified(basis = "Pilot 模式 config 与机场一致：method=config，data={config_type:json, config_scope:product}")
    public static final RegistrationStep CONFIG = new RegistrationStep(
            "config",
            "获取配置（Pilot 模式与机场一致），超时重试 3 次间隔 3 秒",
            RegistrationStep.ChannelType.REQUESTS,
            3, 3, 3
    );

    /**
     * 步骤 2：airport_bind_status — 查询设备绑定状态（Pilot 模式与机场一致）。
     * <p>请求 data 结构：{@code {devices:[{sn}]}}。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "Pilot 模式 airport_bind_status 与机场一致：data={devices:[{sn}]}")
    public static final RegistrationStep AIRPORT_BIND_STATUS = new RegistrationStep(
            "airport_bind_status",
            "查询设备绑定状态（Pilot 模式与机场一致）",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 3：airport_organization_get — 查询组织信息（Pilot 模式与机场一致）。
     * <p>请求 data 结构：{@code {device_binding_code, organization_id}}。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "Pilot 模式 airport_organization_get 与机场一致：data={device_binding_code, organization_id}")
    public static final RegistrationStep AIRPORT_ORGANIZATION_GET = new RegistrationStep(
            "airport_organization_get",
            "查询组织信息（Pilot 模式与机场一致）",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 4：airport_organization_bind — 绑定到组织（Pilot 模式与机场一致）。
     * <p>请求 data 结构：{@code {bind_devices:[{sn, device_model_key, device_callsign, organization_id, device_binding_code}]}}。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "Pilot 模式 airport_organization_bind 与机场一致：data={bind_devices:[{sn, device_model_key, device_callsign, organization_id, device_binding_code}]}")
    public static final RegistrationStep AIRPORT_ORGANIZATION_BIND = new RegistrationStep(
            "airport_organization_bind",
            "绑定到组织（Pilot 模式与机场一致）",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 5：update_topo — 上线通知（Pilot 模式差异）。
     * <p>通过 {@code thing/product/{gateway_sn}/status} 上报（非机场的 sys/product）。
     * RC Plus 2 的子设备不上报 domain/index 字段；超时不停止上线流程。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/device.html")
    @Verified(basis = "DJI 官方文档 update_topo：method=update_topo；Pilot 模式使用 thing/product 前缀的 status 通道")
    @Inferred(
            reason = "RC Plus 2 的 update_topo 子设备不上报 domain/index 字段，未在 DJI 官方文档中明确核实",
            verifyPoint = "真机抓包确认 RC Plus 2 update_topo sub_devices 是否省略 domain/index 字段"
    )
    public static final RegistrationStep UPDATE_TOPO = new RegistrationStep(
            "update_topo",
            "上线通知（Pilot 模式 thing/product/{sn}/status）；RC Plus 2 子设备不上报 domain/index 字段，超时不停止上线流程",
            RegistrationStep.ChannelType.STATUS,
            3, 0, 0
    );

    /** 步骤列表（按执行顺序） */
    private static final List<RegistrationStep> STEPS = List.of(
            CONFIG,
            AIRPORT_BIND_STATUS,
            AIRPORT_ORGANIZATION_GET,
            AIRPORT_ORGANIZATION_BIND,
            UPDATE_TOPO
    );

    /**
     * 返回注册流程步骤列表（按执行顺序）。
     *
     * @return 不可变步骤列表
     */
    public static List<RegistrationStep> steps() {
        return STEPS;
    }

    /**
     * 注册流程总步数。
     *
     * @return 总步数（5）
     */
    public static int totalSteps() {
        return STEPS.size();
    }
}
