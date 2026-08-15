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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

import java.util.List;

/**
 * 机场上云注册流程定义。
 *
 * <p>从模拟器 {@code DockOnlineService.online()} 提取的 5 步注册/上线序列：
 * <ol>
 *   <li>{@code config} — 获取 License 校验参数（超时 3 秒，重试 3 次，间隔 3 秒）</li>
 *   <li>{@code airport_bind_status} — 查询设备绑定状态（超时 3 秒，不重试）</li>
 *   <li>{@code airport_organization_get} — 查询组织信息（超时 3 秒，不重试）</li>
 *   <li>{@code airport_organization_bind} — 绑定到组织（超时 3 秒，不重试）</li>
 *   <li>{@code update_topo} — 上线通知（通过 status 通道发送，超时不停止流程，对齐 DJI 行为）</li>
 * </ol>
 *
 * <p>每步的 method、通道与 data 结构均经 DJI 官方文档核实，详见各步骤常量上的
 * {@link DocUrl} 与 {@link Verified}。超时与重试参数为模拟器实现策略，非 DJI 协议规定。
 *
 * <p>注册时序对齐 AGENTS.md：每一步指令无条件执行，不根据绑定状态跳过；
 * 绑定码错误（result=210229）停止注册；config 回复的 app_license 需与本地配置比对一致。
 */
public final class DockRegistrationFlow {

    private DockRegistrationFlow() {
    }

    /**
     * 步骤 1：config — 获取 License 校验参数。
     * <p>请求 data 结构：{@code {config_type:"json", config_scope:"product"}}，
     * 回复 data 含 {@code app_id / app_key / app_license}。
     * <p>config 请求超时重试 3 次（间隔 3 秒），全失败才停止注册。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock2/config.html")
    @Verified(basis = "DJI 官方文档「获取配置」：method=config，topic=thing/product/{sn}/requests，data={config_type:json, config_scope:product}，回复 data 含 app_id/app_key/app_license")
    public static final RegistrationStep CONFIG = new RegistrationStep(
            "config",
            "获取 License 校验参数（app_id/app_key/app_license），超时重试 3 次间隔 3 秒，全失败才停止注册",
            RegistrationStep.ChannelType.REQUESTS,
            3, 3, 3
    );

    /**
     * 步骤 2：airport_bind_status — 查询设备绑定状态。
     * <p>请求 data 结构：{@code {devices:[{sn:dockSn},{sn:droneSn}]}}。
     * <p>result≠0 表示请求级错误停止注册；result=0 时不根据 bind_status 内容跳过后续步骤（每步无条件执行）。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "DJI 官方文档「获取设备绑定信息」：method=airport_bind_status，topic=thing/product/{sn}/requests，data={devices:[{sn}]}，回复 data.result 非 0 表示错误")
    public static final RegistrationStep AIRPORT_BIND_STATUS = new RegistrationStep(
            "airport_bind_status",
            "查询设备绑定状态，result≠0 表示请求级错误；result=0 时不根据 bind_status 内容跳过后续步骤",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 3：airport_organization_get — 查询组织信息。
     * <p>请求 data 结构：{@code {device_binding_code:code, organization_id:orgId}}。
     * <p>hivemind 据此校验绑定码，result≠0（如 210229 绑定码错误）停止注册。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "DJI 官方文档「查询设备绑定对应的组织信息」：method=airport_organization_get，topic=thing/product/{sn}/requests，data={device_binding_code, organization_id}，绑定码错误返回 210229"
    )
    public static final RegistrationStep AIRPORT_ORGANIZATION_GET = new RegistrationStep(
            "airport_organization_get",
            "查询组织信息，hivemind 据此校验绑定码，result≠0（如 210229 绑定码错误）停止注册",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 4：airport_organization_bind — 绑定到组织。
     * <p>请求 data 结构：{@code {bind_devices:[{sn, device_model_key, device_callsign, organization_id, device_binding_code}, ...]}}。
     * <p>result≠0 停止注册；result=0 但 output.err_infos 非空表示设备级失败，透传第一个 err_code。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/organization.html")
    @Verified(basis = "DJI 官方文档「设备绑定到组织」：method=airport_organization_bind，topic=thing/product/{sn}/requests，data={bind_devices:[{sn, device_model_key, device_callsign, organization_id, device_binding_code}]}，result=0 但 output.err_infos 非空表示设备级失败"
    )
    public static final RegistrationStep AIRPORT_ORGANIZATION_BIND = new RegistrationStep(
            "airport_organization_bind",
            "绑定到组织，result≠0 停止注册；result=0 但 output.err_infos 非空表示设备级失败，透传第一个 err_code",
            RegistrationStep.ChannelType.REQUESTS,
            3, 0, 0
    );

    /**
     * 步骤 5：update_topo — 上线通知。
     * <p>通过 status 通道发送（{@code sys/product/{sn}/status}），不走 requests 通道。
     * 注册成功后通知平台设备拓扑；超时不停止上线流程，对齐 DJI 时序图行为。
     */
    @DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/device.html")
    @Verified(basis = "DJI 官方文档「设备拓扑更新」：method=update_topo，topic=sys/product/{gateway_sn}/status，envelope 含 tid/bid/method/timestamp/data，超时不停止上线流程（对齐 DJI 时序图）"
    )
    public static final RegistrationStep UPDATE_TOPO = new RegistrationStep(
            "update_topo",
            "上线通知（status 通道），注册成功后通知平台设备拓扑；超时不停止上线流程，对齐 DJI 行为",
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
