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
 * 验证 {@link StatusMethod} 枚举的 method 字符串映射与反查。
 *
 * <p><b>核心证明</b>：status 通道的 1 个 method 字符串（{@code update_topo}）
 * 能通过 {@link StatusMethod#fromMethodName(String)} 反查到枚举常量，
 * 说明 SDK 已覆盖 simulator 对接 hivemind 验证的 status 推送方法。
 */
class StatusMethodTest {

    @Test
    @DisplayName("fromMethodName 反查 update_topo")
    void testFromMethodNameUpdateTopo() {
        assertEquals(StatusMethod.UPDATE_TOPO, StatusMethod.fromMethodName("update_topo").orElseThrow());
    }

    @Test
    @DisplayName("fromMethodName 未匹配返回空 Optional")
    void testFromMethodNameUnknownReturnsEmpty() {
        assertTrue(StatusMethod.fromMethodName("unknown_method").isEmpty());
        assertTrue(StatusMethod.fromMethodName("").isEmpty());
        assertTrue(StatusMethod.fromMethodName(null).isEmpty());
    }

    @Test
    @DisplayName("枚举总数应为 1（update_topo）")
    void testTotalCount() {
        assertEquals(1, StatusMethod.values().length);
    }

    @Test
    @DisplayName("methodName 与 description 非空")
    void testMethodNameAndDescription() {
        for (StatusMethod m : StatusMethod.values()) {
            assertTrue(!m.methodName().isBlank(),
                    "枚举 " + m.name() + " 的 methodName 不应为空");
            assertTrue(!m.description().isBlank(),
                    "枚举 " + m.name() + " 的 description 不应为空");
        }
    }

    @Test
    @DisplayName("methodName 双向映射：枚举 → 字符串 → 枚举闭环")
    void testMethodNameRoundTrip() {
        for (StatusMethod m : StatusMethod.values()) {
            String name = m.methodName();
            assertEquals(m, StatusMethod.fromMethodName(name).orElseThrow(),
                    "methodName=" + name + " 反查应回到原枚举 " + m);
        }
    }
}
