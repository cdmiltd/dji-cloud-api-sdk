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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ServiceMethod} 枚举测试。
 */
class ServiceMethodTest {

    @Test
    @DisplayName("枚举数量：97 个 method")
    void testCount() {
        assertEquals(97, ServiceMethod.values().length);
    }

    @Test
    @DisplayName("methodName ↔ 枚举双向映射：cloud_control_release")
    void testCloudControlReleaseMapping() {
        assertEquals("cloud_control_release", ServiceMethod.CLOUD_CONTROL_RELEASE.methodName());
        assertEquals("释放云端控制权", ServiceMethod.CLOUD_CONTROL_RELEASE.description());
        assertEquals(Optional.of(ServiceMethod.CLOUD_CONTROL_RELEASE),
                ServiceMethod.fromMethodName("cloud_control_release"));
    }

    @Test
    @DisplayName("fromMethodName：已知值/未知值/null/空白")
    void testFromMethodName() {
        // 已知值
        assertTrue(ServiceMethod.fromMethodName("flighttask_execute").isPresent());
        assertTrue(ServiceMethod.fromMethodName("cloud_control_auth_request").isPresent());
        assertTrue(ServiceMethod.fromMethodName("cloud_control_release").isPresent());

        // 未知值
        assertTrue(ServiceMethod.fromMethodName("nonexistent_method").isEmpty());

        // null / 空白
        assertTrue(ServiceMethod.fromMethodName(null).isEmpty());
        assertTrue(ServiceMethod.fromMethodName("").isEmpty());
        assertTrue(ServiceMethod.fromMethodName("  ").isEmpty());
    }

    @Test
    @DisplayName("methodName 全局唯一：无重复")
    void testMethodNameUnique() {
        long distinctCount = java.util.Arrays.stream(ServiceMethod.values())
                .map(ServiceMethod::methodName)
                .distinct()
                .count();
        assertEquals(ServiceMethod.values().length, distinctCount, "methodName 不应有重复");
    }

    @Test
    @DisplayName("description 全部非空")
    void testDescriptionNonNull() {
        for (ServiceMethod m : ServiceMethod.values()) {
            assertNotNull(m.description(), m.name() + " 的 description 不应为 null");
            assertFalse(m.description().isBlank(), m.name() + " 的 description 不应为空白");
        }
    }
}
