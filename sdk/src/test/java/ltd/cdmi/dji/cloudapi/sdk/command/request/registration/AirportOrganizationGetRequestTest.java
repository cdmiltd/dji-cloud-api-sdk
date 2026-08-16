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

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link AirportOrganizationGetRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_organization_get 请求 data（device_binding_code + organization_id）
 * 能反序列化为 record；缺失任一必填字段时构造器抛 NPE。
 */
class AirportOrganizationGetRequestTest {

    private static final String SAMPLE_JSON =
            "{\"device_binding_code\":\"CODE123\",\"organization_id\":\"ORG001\"}";

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase 字段正确绑定")
    void testDeserialize() {
        AirportOrganizationGetRequest req = MessageCodec.fromJson(SAMPLE_JSON, AirportOrganizationGetRequest.class);
        assertEquals("CODE123", req.deviceBindingCode());
        assertEquals("ORG001", req.organizationId());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 device_binding_code 与 organization_id")
    void testSerialize() {
        AirportOrganizationGetRequest req = new AirportOrganizationGetRequest("CODE123", "ORG001");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"device_binding_code\":\"CODE123\""), "JSON 应含 device_binding_code，实际: " + json);
        assertTrue(json.contains("\"organization_id\":\"ORG001\""), "JSON 应含 organization_id，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        AirportOrganizationGetRequest original = new AirportOrganizationGetRequest("CODE123", "ORG001");
        String json = MessageCodec.toJson(original);
        AirportOrganizationGetRequest back = MessageCodec.fromJson(json, AirportOrganizationGetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 device_binding_code 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingBindingCodeThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"organization_id\":\"ORG001\"}", AirportOrganizationGetRequest.class));
    }

    @Test
    @DisplayName("缺失 organization_id 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingOrgIdThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"device_binding_code\":\"CODE\"}", AirportOrganizationGetRequest.class));
    }
}
