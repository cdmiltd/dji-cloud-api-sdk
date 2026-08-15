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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link RequestsMethod} 枚举的 method 字符串映射与反查。
 *
 * <p><b>核心证明</b>：requests 通道的 8 个 method 字符串（{@code config} 等）
 * 能通过 {@link RequestsMethod#fromMethodName(String)} 反查到枚举常量，
 * 说明 SDK 已覆盖 simulator 对接 hivemind 验证的所有 requests 推送方法。
 */
class RequestsMethodTest {

    @Test
    @DisplayName("fromMethodName 反查 8 个 simulator 已实现 method")
    void testFromMethodNameSimulatorMethods() {
        assertEquals(RequestsMethod.CONFIG, RequestsMethod.fromMethodName("config").orElseThrow());
        assertEquals(RequestsMethod.AIRPORT_BIND_STATUS, RequestsMethod.fromMethodName("airport_bind_status").orElseThrow());
        assertEquals(RequestsMethod.AIRPORT_ORGANIZATION_GET, RequestsMethod.fromMethodName("airport_organization_get").orElseThrow());
        assertEquals(RequestsMethod.AIRPORT_ORGANIZATION_BIND, RequestsMethod.fromMethodName("airport_organization_bind").orElseThrow());
        assertEquals(RequestsMethod.STORAGE_CONFIG_GET, RequestsMethod.fromMethodName("storage_config_get").orElseThrow());
        assertEquals(RequestsMethod.FLIGHTTASK_PROGRESS_GET, RequestsMethod.fromMethodName("flighttask_progress_get").orElseThrow());
        assertEquals(RequestsMethod.FLIGHTTASK_RESOURCE_GET, RequestsMethod.fromMethodName("flighttask_resource_get").orElseThrow());
        assertEquals(RequestsMethod.FLIGHT_AREAS_GET, RequestsMethod.fromMethodName("flight_areas_get").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 未匹配返回空 Optional")
    void testFromMethodNameUnknownReturnsEmpty() {
        assertTrue(RequestsMethod.fromMethodName("unknown_method").isEmpty());
        assertTrue(RequestsMethod.fromMethodName("").isEmpty());
        assertTrue(RequestsMethod.fromMethodName(null).isEmpty());
    }

    @Test
    @DisplayName("枚举总数应为 8（注册 4 + 配置 1 + 任务 2 + 飞行区 1）")
    void testTotalCount() {
        assertEquals(8, RequestsMethod.values().length);
    }

    @Test
    @DisplayName("methodName 与 description 非空")
    void testMethodNameAndDescription() {
        for (RequestsMethod m : RequestsMethod.values()) {
            assertTrue(!m.methodName().isBlank(),
                    "枚举 " + m.name() + " 的 methodName 不应为空");
            assertTrue(!m.description().isBlank(),
                    "枚举 " + m.name() + " 的 description 不应为空");
        }
    }

    @Test
    @DisplayName("methodName 双向映射：枚举 → 字符串 → 枚举闭环")
    void testMethodNameRoundTrip() {
        for (RequestsMethod m : RequestsMethod.values()) {
            String name = m.methodName();
            assertEquals(m, RequestsMethod.fromMethodName(name).orElseThrow(),
                    "methodName=" + name + " 反查应回到原枚举 " + m);
        }
    }
}
