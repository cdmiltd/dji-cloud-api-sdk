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
import ltd.cdmi.dji.cloudapi.sdk.command.request.registration.AirportOrganizationBindRequest.BindDevice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link AirportOrganizationBindRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_organization_bind 请求 data（bind_devices 数组，每元素含
 * sn/device_model_key/device_callsign/organization_id/device_binding_code）
 * 能反序列化为嵌套 record；缺失任一必填字段时构造器抛 NPE。
 */
class AirportOrganizationBindRequestTest {

    private static final String SAMPLE_JSON =
            "{\"bind_devices\":[{\"sn\":\"SN001\",\"device_model_key\":\"3-1-0\","
            + "\"device_callsign\":\"CS001\",\"organization_id\":\"ORG001\","
            + "\"device_binding_code\":\"CODE123\"}]}";

    @Test
    @DisplayName("反序列化：bind_devices 数组 → List<BindDevice>，全部字段正确绑定")
    void testDeserialize() {
        AirportOrganizationBindRequest req = MessageCodec.fromJson(SAMPLE_JSON, AirportOrganizationBindRequest.class);
        List<BindDevice> devices = req.bindDevices();
        assertEquals(1, devices.size());
        BindDevice d = devices.get(0);
        assertEquals("SN001", d.sn());
        assertEquals("3-1-0", d.deviceModelKey());
        assertEquals("CS001", d.deviceCallsign());
        assertEquals("ORG001", d.organizationId());
        assertEquals("CODE123", d.deviceBindingCode());
    }

    @Test
    @DisplayName("序列化：含 bind_devices 数组的 record → JSON（snake_case）")
    void testSerialize() {
        BindDevice d = new BindDevice("SN001", "3-1-0", "CS001", "ORG001", "CODE123");
        AirportOrganizationBindRequest req = new AirportOrganizationBindRequest(List.of(d));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"bind_devices\":"), "JSON 应含 bind_devices，实际: " + json);
        assertTrue(json.contains("\"device_model_key\":\"3-1-0\""), "JSON 应含 device_model_key，实际: " + json);
        assertTrue(json.contains("\"device_callsign\":\"CS001\""), "JSON 应含 device_callsign，实际: " + json);
        assertTrue(json.contains("\"device_binding_code\":\"CODE123\""), "JSON 应含 device_binding_code，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        BindDevice d = new BindDevice("SN001", "3-1-0", "CS001", "ORG001", "CODE123");
        AirportOrganizationBindRequest original = new AirportOrganizationBindRequest(List.of(d));
        String json = MessageCodec.toJson(original);
        AirportOrganizationBindRequest back = MessageCodec.fromJson(json, AirportOrganizationBindRequest.class);
        assertEquals(1, back.bindDevices().size());
        BindDevice bd = back.bindDevices().get(0);
        assertEquals("SN001", bd.sn());
        assertEquals("3-1-0", bd.deviceModelKey());
        assertEquals("CODE123", bd.deviceBindingCode());
    }

    @Test
    @DisplayName("缺失 bind_devices 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingBindDevicesThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AirportOrganizationBindRequest.class));
    }

    @Test
    @DisplayName("缺失 sn 字段：BindDevice 构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingSnThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"bind_devices\":[{\"device_model_key\":\"3-1-0\",\"device_callsign\":\"CS\","
                        + "\"organization_id\":\"O\",\"device_binding_code\":\"C\"}]}",
                        AirportOrganizationBindRequest.class));
    }
}
