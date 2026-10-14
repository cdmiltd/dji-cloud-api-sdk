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

package ltd.cdmi.dji.cloudapi.sdk.command.peripheral;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * O4 地面站外设结构（{@link PeripheralInfo} / {@link PeripheralCapability}）解析测试。
 */
class PeripheralInfoTest {

    @Test
    @DisplayName("PeripheralInfo：snake_case 映射（device_sn/ip_addr/need_auth/main_device）")
    void testParsePeripheralInfo() {
        String json = "{\"device_sn\":\"sn1\",\"name\":\"cam\",\"port\":0,\"type\":1,"
                + "\"ip_addr\":\"192.168.1.10\",\"need_auth\":1,\"main_device\":1}";

        PeripheralInfo info = MessageCodec.fromJson(json, PeripheralInfo.class);

        assertEquals("sn1", info.deviceSn());
        assertEquals("cam", info.name());
        assertEquals(0, info.port());
        assertEquals(1, info.type());
        assertEquals("192.168.1.10", info.ipAddr());
        assertEquals(1, info.needAuth());
        assertEquals(1, info.mainDevice());
    }

    @Test
    @DisplayName("PeripheralInfo：可选字段缺席时为 null")
    void testPeripheralInfoOptionalAbsent() {
        PeripheralInfo info = MessageCodec.fromJson("{\"device_sn\":\"sn1\"}", PeripheralInfo.class);

        assertEquals("sn1", info.deviceSn());
        assertNull(info.port());
        assertNull(info.needAuth());
        assertNull(info.mainDevice());
    }

    @Test
    @DisplayName("PeripheralCapability：rid/adsb/dock_control 三组能力")
    void testParsePeripheralCapability() {
        String json = "{\"sn\":\"sn1\","
                + "\"rid\":{\"supported\":1,\"version\":\"1.0\"},"
                + "\"adsb\":{\"supported\":0,\"version\":\"\"},"
                + "\"dock_control\":{\"supported\":1,\"version\":\"2.0\"}}";

        PeripheralCapability cap = MessageCodec.fromJson(json, PeripheralCapability.class);

        assertEquals("sn1", cap.sn());
        assertEquals(1, cap.rid().supported());
        assertEquals("1.0", cap.rid().version());
        assertEquals(0, cap.adsb().supported());
        assertEquals(1, cap.dockControl().supported());
        assertEquals("2.0", cap.dockControl().version());
    }
}
