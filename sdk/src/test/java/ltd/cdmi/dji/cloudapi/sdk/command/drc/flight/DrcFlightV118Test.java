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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 DRC 飞行类 POJO（夜航灯/隐蔽模式/异常回包）的 wire key 与往返闭环。
 */
class DrcFlightV118Test {

    @Test
    @DisplayName("drc_night_lights_state_set：night_lights_state")
    void testNightLights() {
        DrcNightLightsStateSetRequest req = new DrcNightLightsStateSetRequest(1);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"night_lights_state\":1"), json);
        assertEquals(req, MessageCodec.fromJson(json, DrcNightLightsStateSetRequest.class));
    }

    @Test
    @DisplayName("drc_stealth_state_set：stealth_state")
    void testStealth() {
        DrcStealthStateSetRequest req = new DrcStealthStateSetRequest(0);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"stealth_state\":0"), json);
        assertEquals(req, MessageCodec.fromJson(json, DrcStealthStateSetRequest.class));
    }

    @Test
    @DisplayName("DroneControlReply：result + output{seq}")
    void testDroneControlReply() {
        DroneControlReply reply = new DroneControlReply(1, new DroneControlReply.Output(7));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":1"), json);
        assertTrue(json.contains("\"seq\":7"), json);
        assertEquals(reply, MessageCodec.fromJson(json, DroneControlReply.class));
    }
}
