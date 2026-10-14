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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 {@link DroneAuthorityInfo}（Dock OSD {@code drone_authority_info} 结构）的类型安全解析。
 */
class DroneAuthorityInfoTest {

    @Test
    @DisplayName("drone_authority_info — control_source/locked/payloads[]")
    void testParse() {
        String json = "{\"control_source\":\"A\",\"locked\":true,"
                + "\"payloads\":[{\"control_source\":\"B\",\"payload_index\":\"1-1-0\",\"sn\":\"PAY-1\"}]}";
        DroneAuthorityInfo info = MessageCodec.fromJson(json, DroneAuthorityInfo.class);
        assertEquals("A", info.controlSource());
        assertEquals(Boolean.TRUE, info.locked());
        assertEquals(1, info.payloads().size());
        DroneAuthorityInfo.PayloadAuthority payload = info.payloads().get(0);
        assertEquals("B", payload.controlSource());
        assertEquals("1-1-0", payload.payloadIndex());
        assertEquals("PAY-1", payload.sn());
    }

    @Test
    @DisplayName("control_source 可为空字符串（未获取）")
    void testEmptyControlSource() {
        DroneAuthorityInfo info = MessageCodec.fromJson("{\"control_source\":\"\"}", DroneAuthorityInfo.class);
        assertEquals("", info.controlSource());
    }
}
