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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 {@link BatteryMaintenanceSwitchRequest} 的 action wire key 与往返闭环。
 */
class BatteryMaintenanceSwitchRequestTest {

    @Test
    @DisplayName("battery_maintenance_switch：action（enum_int）")
    void testAction() {
        BatteryMaintenanceSwitchRequest req = new BatteryMaintenanceSwitchRequest(1);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"action\":1"), json);
        assertEquals(req, MessageCodec.fromJson(json, BatteryMaintenanceSwitchRequest.class));
    }
}
