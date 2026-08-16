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
 * DJI Cloud API {@code status} 通道的 method 名称枚举。
 *
 * <p>{@code status} 通道用于设备拓扑上下线通知（设备→云）。
 * 机场上云用 {@code sys/product/{sn}/status}，Pilot 上云用 {@code thing/product/{sn}/status}。
 *
 * <p>本枚举对应 SDK design doc §4.4 的 StatusMethod 表（数量 1）。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html">
 * DJI Cloud API 连接与 Topic 划分</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html")
@Verified(basis = "simulator DockOnlineService/PilotRegistrationFlow 已对接 hivemind 验证")
public enum StatusMethod {

    /** 设备拓扑上下线通知（设备→云，data 含 sub_type 区分上线 0/下线 1） */
    UPDATE_TOPO("update_topo", "设备拓扑上下线通知");

    private final String methodName;
    private final String description;

    StatusMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "update_topo" */
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
    public static Optional<StatusMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (StatusMethod m : values()) {
            if (m.methodName.equals(methodName)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
