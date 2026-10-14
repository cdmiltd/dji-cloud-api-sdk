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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link Dock2HomePositionIsValid} 枚举的 2 值 code 反查（Dock2 值域）。
 */
class Dock2HomePositionIsValidTest {

    @Test
    @DisplayName("fromCode 反查 DJI Dock2 文档定义的 2 个数值（0=无效, 1=有效）")
    void testFromCodeAllValues() {
        assertEquals(Dock2HomePositionIsValid.INVALID, Dock2HomePositionIsValid.fromCode(0));
        assertEquals(Dock2HomePositionIsValid.VALID, Dock2HomePositionIsValid.fromCode(1));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（0-1 之外）")
    void testFromCodeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> Dock2HomePositionIsValid.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> Dock2HomePositionIsValid.fromCode(2));
        assertThrows(IllegalArgumentException.class, () -> Dock2HomePositionIsValid.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> Dock2HomePositionIsValid.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 2（无效 + 有效）")
    void testTotalCount() {
        assertEquals(2, Dock2HomePositionIsValid.values().length);
    }
}
