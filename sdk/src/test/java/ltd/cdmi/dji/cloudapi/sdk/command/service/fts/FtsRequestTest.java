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

package ltd.cdmi.dji.cloudapi.sdk.command.service.fts;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 FTS 相关 POJO（FtsServerType / FtsConfigRequest / DronePropellerStopV1Request）。
 */
class FtsRequestTest {

    @Test
    @DisplayName("FtsServerType：code 与 fromCode 双向")
    void testFtsServerType() {
        assertEquals(0, FtsServerType.DJI_FTS.code());
        assertEquals(1, FtsServerType.CUSTOM.code());
        assertEquals(FtsServerType.CUSTOM, FtsServerType.fromCode(1));
        assertThrows(IllegalArgumentException.class, () -> FtsServerType.fromCode(9));
    }

    @Test
    @DisplayName("FtsConfigRequest：fts_server_type + 条件必填 mqtt_broker")
    void testFtsConfig() {
        FtsConfigRequest req = new FtsConfigRequest(1,
                new FtsConfigRequest.MqttBroker("tcp://host:1883", "user", "pass"));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"fts_server_type\":1"), json);
        assertTrue(json.contains("\"mqtt_broker\""), json);
        assertTrue(json.contains("\"address\":\"tcp://host:1883\""), json);
        assertEquals(req, MessageCodec.fromJson(json, FtsConfigRequest.class));
    }

    @Test
    @DisplayName("FtsConfigRequest：ftsServerType 缺失抛 IllegalStateException")
    void testFtsConfigMissingType() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"mqtt_broker\":{}}", FtsConfigRequest.class));
    }

    @Test
    @DisplayName("DronePropellerStopV1Request：fts_verification_code")
    void testPropellerStop() {
        DronePropellerStopV1Request req = new DronePropellerStopV1Request("AB12CD34");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"fts_verification_code\":\"AB12CD34\""), json);
        assertEquals(req, MessageCodec.fromJson(json, DronePropellerStopV1Request.class));
    }
}
