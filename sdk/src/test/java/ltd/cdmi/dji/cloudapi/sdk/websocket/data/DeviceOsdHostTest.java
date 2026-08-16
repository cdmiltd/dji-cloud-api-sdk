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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DeviceOsdHost} device_osd.host 子结构 record 测试。
 *
 * <p>验证位置/姿态/速度遥测字段的 snake_case ↔ camelCase 双向映射：
 * {@code attitude_head} / {@code horizontal_speed} / {@code vertical_speed}。
 */
class DeviceOsdHostTest {

    private static final String JSON = "{"
            + "\"latitude\":22.5,"
            + "\"longitude\":113.9,"
            + "\"height\":50.0,"
            + "\"attitude_head\":180.0,"
            + "\"elevation\":100.0,"
            + "\"horizontal_speed\":10.0,"
            + "\"vertical_speed\":0.0}";

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record（attitude_head/horizontal_speed/vertical_speed）")
    void testDeserialize() {
        DeviceOsdHost host = MessageCodec.fromJson(JSON, DeviceOsdHost.class);
        assertEquals(22.5, host.latitude(), 0.001);
        assertEquals(113.9, host.longitude(), 0.001);
        assertEquals(50.0, host.height(), 0.001);
        assertEquals(180.0, host.attitudeHead(), 0.001);
        assertEquals(100.0, host.elevation(), 0.001);
        assertEquals(10.0, host.horizontalSpeed(), 0.001);
        assertEquals(0.0, host.verticalSpeed(), 0.001);
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON")
    void testSerialize() {
        DeviceOsdHost host = new DeviceOsdHost(22.5, 113.9, 50.0, 180.0, 100.0, 10.0, 0.0);
        String json = MessageCodec.toJson(host);
        assertTrue(json.contains("\"latitude\":22.5"));
        assertTrue(json.contains("\"longitude\":113.9"));
        assertTrue(json.contains("\"height\":50.0"));
        assertTrue(json.contains("\"attitude_head\":180.0"));
        assertTrue(json.contains("\"elevation\":100.0"));
        assertTrue(json.contains("\"horizontal_speed\":10.0"));
        assertTrue(json.contains("\"vertical_speed\":0.0"));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        DeviceOsdHost original = new DeviceOsdHost(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        String json = MessageCodec.toJson(original);
        DeviceOsdHost round = MessageCodec.fromJson(json, DeviceOsdHost.class);
        assertEquals(original, round);
    }

    @Test
    @DisplayName("FAIL_ON_UNKNOWN_PROPERTIES=false：未知字段（battery/gimbal_pitch 等）不报错")
    void testUnknownFieldTolerated() {
        String json = "{\"latitude\":1.0,\"battery\":80,\"gimbal_pitch\":-30.0}";
        DeviceOsdHost host = MessageCodec.fromJson(json, DeviceOsdHost.class);
        assertEquals(1.0, host.latitude(), 0.001);
        // 未知字段忽略，未提供字段为默认值 0.0
        assertEquals(0.0, host.longitude(), 0.001);
    }

    @Test
    @DisplayName("缺失字段反序列化为 double 默认值 0.0")
    void testMissingFieldDefaultsToZero() {
        DeviceOsdHost host = MessageCodec.fromJson("{\"latitude\":1.0}", DeviceOsdHost.class);
        assertEquals(1.0, host.latitude(), 0.001);
        assertEquals(0.0, host.longitude(), 0.001);
        assertEquals(0.0, host.height(), 0.001);
        assertEquals(0.0, host.attitudeHead(), 0.001);
    }
}
