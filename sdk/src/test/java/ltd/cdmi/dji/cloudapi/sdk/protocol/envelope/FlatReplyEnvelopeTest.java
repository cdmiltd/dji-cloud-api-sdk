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

package ltd.cdmi.dji.cloudapi.sdk.protocol.envelope;

import java.util.Map;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 {@link FlatReplyEnvelope} 的 wire key、兼容构造器与静态工厂。
 */
class FlatReplyEnvelopeTest {

    @Test
    @DisplayName("6 参构造：data 为扁平 result map，gateway 序列化")
    void testSixArgConstructor() {
        FlatReplyEnvelope envelope = new FlatReplyEnvelope("tid-1", "bid-1", 1700000000000L,
                "unlock_license_switch", Map.of("result", 0, "license_id", 7), "GW-SN");
        String json = MessageCodec.toJson(envelope);
        assertTrue(json.contains("\"tid\":\"tid-1\""), json);
        assertTrue(json.contains("\"gateway\":\"GW-SN\""), json);
        assertTrue(json.contains("\"result\":0"), json);
        assertTrue(json.contains("\"license_id\":7"), json);
        assertEquals(envelope, MessageCodec.fromJson(json, FlatReplyEnvelope.class));
    }

    @Test
    @DisplayName("5 参兼容构造：gateway 置 null")
    void testFiveArgConstructor() {
        FlatReplyEnvelope envelope = new FlatReplyEnvelope("t", "b", 1L, "m", Map.of("result", 0));
        assertNull(envelope.gateway());
        assertTrue(MessageCodec.toJson(envelope).contains("\"result\":0"));
    }

    @Test
    @DisplayName("of 工厂：data=replyData，method 透传")
    void testFactory() {
        FlatReplyEnvelope envelope = FlatReplyEnvelope.of("t", "b", "unlock_license_list", Map.of("result", 0));
        assertEquals("unlock_license_list", envelope.method());
        assertEquals("t", envelope.tid());
        assertNull(envelope.gateway());
    }
}
