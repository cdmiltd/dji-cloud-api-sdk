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

package ltd.cdmi.dji.cloudapi.sdk.websocket.data;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DeviceOsdPushData} device_osd 推送 data 结构测试。
 *
 * <p>验证 {@code {sn, host: {...}}} 结构的序列化/反序列化，含嵌套 {@link DeviceOsdHost} 子结构。
 */
class DeviceOsdPushDataTest {

    private static final String JSON = "{"
            + "\"sn\":\"1ZND001\","
            + "\"host\":{"
            + "\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0,"
            + "\"attitude_head\":180.0,\"elevation\":100.0,"
            + "\"horizontal_speed\":10.0,\"vertical_speed\":0.0}}";

    @Test
    @DisplayName("反序列化：嵌套 host 子结构正确映射")
    void testDeserialize() {
        DeviceOsdPushData data = MessageCodec.fromJson(JSON, DeviceOsdPushData.class);
        assertEquals("1ZND001", data.sn());
        assertNotNull(data.host());
        assertEquals(22.5, data.host().latitude(), 0.001);
        assertEquals(113.9, data.host().longitude(), 0.001);
        assertEquals(50.0, data.host().height(), 0.001);
        assertEquals(180.0, data.host().attitudeHead(), 0.001);
        assertEquals(100.0, data.host().elevation(), 0.001);
        assertEquals(10.0, data.host().horizontalSpeed(), 0.001);
        assertEquals(0.0, data.host().verticalSpeed(), 0.001);
    }

    @Test
    @DisplayName("序列化：嵌套 host 子结构输出 snake_case")
    void testSerialize() {
        DeviceOsdHost host = new DeviceOsdHost(22.5, 113.9, 50.0, 180.0, 100.0, 10.0, 0.0);
        DeviceOsdPushData data = new DeviceOsdPushData("SN1", host);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"sn\":\"SN1\""));
        assertTrue(json.contains("\"host\":"));
        assertTrue(json.contains("\"attitude_head\":180.0"));
        assertTrue(json.contains("\"horizontal_speed\":10.0"));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        DeviceOsdHost host = new DeviceOsdHost(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        DeviceOsdPushData original = new DeviceOsdPushData("SN1", host);
        String json = MessageCodec.toJson(original);
        DeviceOsdPushData round = MessageCodec.fromJson(json, DeviceOsdPushData.class);
        assertEquals(original, round);
    }

    @Test
    @DisplayName("host 缺失时 host() 为 null")
    void testMissingHost() {
        String json = "{\"sn\":\"SN1\"}";
        DeviceOsdPushData data = MessageCodec.fromJson(json, DeviceOsdPushData.class);
        assertEquals("SN1", data.sn());
        assertNull(data.host());
    }

    @Test
    @DisplayName("record 访问器：sn/host")
    void testRecordAccessors() {
        DeviceOsdHost host = new DeviceOsdHost(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        DeviceOsdPushData data = new DeviceOsdPushData("SN", host);
        assertEquals("SN", data.sn());
        assertEquals(host, data.host());
    }
}
