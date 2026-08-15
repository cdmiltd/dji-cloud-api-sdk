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

package ltd.cdmi.dji.cloudapi.sdk.protocol.method;

import java.util.Optional;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API {@code requests} 通道的 method 名称枚举。
 *
 * <p>{@code requests} 通道用于设备主动向云请求（设备→云，云通过 {@code requests_reply} 回复）。
 * 主要用于设备注册流程、存储配置查询、任务状态查询、飞行区查询等。
 *
 * <p>本枚举覆盖 simulator 已对接 hivemind 验证的 8 个 method：
 * <ul>
 *   <li><b>注册流程 4 个</b>：{@link #CONFIG}/{@link #AIRPORT_BIND_STATUS}/{@link #AIRPORT_ORGANIZATION_GET}/{@link #AIRPORT_ORGANIZATION_BIND}</li>
 *   <li><b>配置查询 1 个</b>：{@link #STORAGE_CONFIG_GET} — 存储配置获取（媒体上传前获取 STS 凭证）</li>
 *   <li><b>任务查询 2 个</b>：{@link #FLIGHTTASK_PROGRESS_GET} — 蛙跳任务进度查询、{@link #FLIGHTTASK_RESOURCE_GET} — 航线任务资源获取</li>
 *   <li><b>飞行区查询 1 个</b>：{@link #FLIGHT_AREAS_GET} — 自定义飞行区数据查询</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 requests 通道</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator DockOnlineService/MediaUploadSimulator/WaylineTaskSimulator/FlightAreaSimulator 已对接 hivemind 验证 8 个 method")
public enum RequestsMethod {

    // ===== 机场注册流程 =====

    /** 机场配置请求（注册流程第 1 步，请求机场配置参数） */
    CONFIG("config", "机场配置请求"),

    /** 机场绑定状态查询（注册流程第 2 步，查询 organization_id 与 binding_code 是否匹配） */
    AIRPORT_BIND_STATUS("airport_bind_status", "机场绑定状态查询"),

    /** 机场组织信息获取（注册流程第 3 步，获取可绑定的机场列表） */
    AIRPORT_ORGANIZATION_GET("airport_organization_get", "机场组织信息获取"),

    /** 机场组织绑定（注册流程第 4 步，将机场绑定到指定组织） */
    AIRPORT_ORGANIZATION_BIND("airport_organization_bind", "机场组织绑定"),

    // ===== 配置查询 =====

    /** 存储配置获取（媒体上传前获取 STS 凭证与 object_key_prefix，data.module=0 媒体/1 日志） */
    STORAGE_CONFIG_GET("storage_config_get", "存储配置获取"),

    // ===== 任务查询 =====

    /** 蛙跳任务进度查询（查询另一机场的任务执行状态，data 含 flight_id） */
    FLIGHTTASK_PROGRESS_GET("flighttask_progress_get", "蛙跳任务进度查询"),

    /** 航线任务资源获取（获取 flight_id 对应的航线文件 URL 和指纹，requests 通道） */
    FLIGHTTASK_RESOURCE_GET("flighttask_resource_get", "航线任务资源获取"),

    // ===== 自定义飞行区查询 =====

    /** 自定义飞行区数据查询（设备主动向云查询飞行区数据） */
    FLIGHT_AREAS_GET("flight_areas_get", "自定义飞行区数据查询");

    private final String methodName;
    private final String description;

    RequestsMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "config" */
    public String methodName() {
        return methodName;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }

    /**
     * 按 method 字符串反查枚举。
     *
     * <p>method 名随固件升级可能扩展，未知 method 是正常情况，故返回 {@link Optional}
     * 而非抛异常（差异化错误处理策略见 SDK design doc §3.5）。
     *
     * @param methodName method 字符串值
     * @return 匹配的枚举；未匹配返回 {@link Optional#empty()}
     */
    public static Optional<RequestsMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (RequestsMethod m : values()) {
            if (m.methodName.equals(methodName)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
