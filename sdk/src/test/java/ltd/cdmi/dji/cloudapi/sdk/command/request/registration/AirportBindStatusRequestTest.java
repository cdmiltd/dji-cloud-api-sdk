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

package ltd.cdmi.dji.cloudapi.sdk.command.request.registration;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.request.registration.AirportBindStatusRequest.Device;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link AirportBindStatusRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_bind_status 请求 data（devices 数组，每元素含 sn）
 * 能反序列化为嵌套 record；缺失 devices 或 sn 时构造器抛 NPE。
 */
class AirportBindStatusRequestTest {

    private static final String SAMPLE_JSON =
            "{\"devices\":[{\"sn\":\"SN001\"},{\"sn\":\"SN002\"}]}";

    @Test
    @DisplayName("反序列化：devices 数组 → List<Device>，sn 正确绑定")
    void testDeserialize() {
        AirportBindStatusRequest req = MessageCodec.fromJson(SAMPLE_JSON, AirportBindStatusRequest.class);
        List<Device> devices = req.devices();
        assertEquals(2, devices.size());
        assertEquals("SN001", devices.get(0).sn());
        assertEquals("SN002", devices.get(1).sn());
    }

    @Test
    @DisplayName("序列化：含 devices 数组的 record → JSON")
    void testSerialize() {
        AirportBindStatusRequest req = new AirportBindStatusRequest(
                List.of(new Device("SN001"), new Device("SN002")));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"devices\":"), "JSON 应含 devices，实际: " + json);
        assertTrue(json.contains("\"sn\":\"SN001\""), "JSON 应含 sn:SN001，实际: " + json);
        assertTrue(json.contains("\"sn\":\"SN002\""), "JSON 应含 sn:SN002，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持 devices 不变")
    void testRoundTrip() {
        AirportBindStatusRequest original = new AirportBindStatusRequest(
                List.of(new Device("SN001"), new Device("SN002")));
        String json = MessageCodec.toJson(original);
        AirportBindStatusRequest back = MessageCodec.fromJson(json, AirportBindStatusRequest.class);
        assertEquals(2, back.devices().size());
        assertEquals("SN001", back.devices().get(0).sn());
        assertEquals("SN002", back.devices().get(1).sn());
    }

    @Test
    @DisplayName("缺失 devices 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingDevicesThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AirportBindStatusRequest.class));
    }

    @Test
    @DisplayName("缺失 sn 字段：Device 构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingSnThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"devices\":[{}]}", AirportBindStatusRequest.class));
    }
}
