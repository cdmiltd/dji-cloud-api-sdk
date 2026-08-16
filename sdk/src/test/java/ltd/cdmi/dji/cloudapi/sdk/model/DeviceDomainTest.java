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

package ltd.cdmi.dji.cloudapi.sdk.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link DeviceDomain} 枚举的值映射与反查。
 */
class DeviceDomainTest {

    @Test
    @DisplayName("枚举总数应为 3（飞行器 + 遥控器 + 机场）")
    void testTotalCount() {
        assertEquals(3, DeviceDomain.values().length);
    }

    @Test
    @DisplayName("value 与 DJI 文档 domain 值一一对应")
    void testValueMapping() {
        assertEquals(0, DeviceDomain.AIRCRAFT.value());
        assertEquals(2, DeviceDomain.CONTROLLER.value());
        assertEquals(3, DeviceDomain.DOCK.value());
    }

    @Test
    @DisplayName("fromValue 反查：覆盖全部 3 个 domain")
    void testFromValue() {
        assertEquals(DeviceDomain.AIRCRAFT, DeviceDomain.fromValue(0));
        assertEquals(DeviceDomain.CONTROLLER, DeviceDomain.fromValue(2));
        assertEquals(DeviceDomain.DOCK, DeviceDomain.fromValue(3));
    }

    @Test
    @DisplayName("fromValue 未知 domain 值抛 IllegalArgumentException")
    void testFromValueUnknown() {
        assertThrows(IllegalArgumentException.class, () -> DeviceDomain.fromValue(1));
        assertThrows(IllegalArgumentException.class, () -> DeviceDomain.fromValue(-1));
        assertThrows(IllegalArgumentException.class, () -> DeviceDomain.fromValue(99));
    }
}
