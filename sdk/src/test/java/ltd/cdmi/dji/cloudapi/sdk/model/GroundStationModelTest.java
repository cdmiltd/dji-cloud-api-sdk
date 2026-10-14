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
 * 验证 {@link GroundStationModel}（O4 地面站型号）的 (type, subType) / modelKey 反查。
 */
class GroundStationModelTest {

    @Test
    @DisplayName("O4 地面站的 domain/type/subType 与 DJI O4 文档一致")
    void testO4Identity() {
        DeviceModel model = GroundStationModel.O4.toModel();
        assertEquals(8, model.domain(), "domain 应为地面站(8)");
        assertEquals(135, model.type());
        assertEquals(0, model.subType());
    }

    @Test
    @DisplayName("fromType 反查 (135, 0) → O4")
    void testFromType() {
        assertEquals(GroundStationModel.O4, GroundStationModel.fromType(135, 0));
    }

    @Test
    @DisplayName("fromModelKey 反查 '8-135-0' → O4")
    void testFromModelKey() {
        assertEquals(GroundStationModel.O4, GroundStationModel.fromModelKey("8-135-0"));
    }

    @Test
    @DisplayName("未知 type/subType 抛 IllegalArgumentException")
    void testFromTypeUnknown() {
        assertThrows(IllegalArgumentException.class, () -> GroundStationModel.fromType(0, 0));
    }

    @Test
    @DisplayName("非地面站 domain 或格式错误的 modelKey 抛 IllegalArgumentException")
    void testFromModelKeyInvalid() {
        assertThrows(IllegalArgumentException.class, () -> GroundStationModel.fromModelKey("3-135-0"));
        assertThrows(IllegalArgumentException.class, () -> GroundStationModel.fromModelKey("8-135"));
        assertThrows(IllegalArgumentException.class, () -> GroundStationModel.fromModelKey(null));
    }
}
