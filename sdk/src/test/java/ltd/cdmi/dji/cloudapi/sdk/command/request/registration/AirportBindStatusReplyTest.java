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
 * 验证 {@link AirportBindStatusReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_bind_status 回复 data（result=返回码 + output.bind_status[]）
 * 能反序列化为 record；缺失 result 时构造器抛 NPE。
 */
class AirportBindStatusReplyTest {

    @Test
    @DisplayName("反序列化：result=0（成功）+ output.bind_status[] → 各字段映射正确")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"bind_status\":[{\"sn\":\"SN-1\","
                + "\"is_device_bind_organization\":true,\"organization_id\":\"ORG-1\","
                + "\"organization_name\":\"OrgA\",\"device_callsign\":\"CALL\"}]}}";
        AirportBindStatusReply reply = MessageCodec.fromJson(json, AirportBindStatusReply.class);
        assertEquals(0, reply.result());
        AirportBindStatusReply.BindStatus status = reply.output().bindStatus().get(0);
        assertEquals("SN-1", status.sn());
        assertEquals(Boolean.TRUE, status.isDeviceBindOrganization());
        assertEquals("ORG-1", status.organizationId());
        assertEquals("OrgA", status.organizationName());
        assertEquals("CALL", status.deviceCallsign());
    }

    @Test
    @DisplayName("反序列化：非 0 result（错误返回码）")
    void testDeserializeError() {
        AirportBindStatusReply reply = MessageCodec.fromJson("{\"result\":1}", AirportBindStatusReply.class);
        assertEquals(1, reply.result());
    }

    @Test
    @DisplayName("序列化：含 output.bind_status → JSON 含 result 与 bind_status")
    void testSerialize() {
        AirportBindStatusReply.BindStatus status =
                new AirportBindStatusReply.BindStatus("SN-1", Boolean.TRUE, "ORG-1", "OrgA", "CALL");
        AirportBindStatusReply reply =
                new AirportBindStatusReply(0, new AirportBindStatusReply.Output(java.util.List.of(status)));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 \"result\":0，实际: " + json);
        assertTrue(json.contains("bind_status"), "JSON 应含 bind_status，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持 result/output 不变")
    void testRoundTrip() {
        AirportBindStatusReply.BindStatus status =
                new AirportBindStatusReply.BindStatus("SN-1", Boolean.FALSE, "ORG-1", "OrgA", "CALL");
        AirportBindStatusReply original =
                new AirportBindStatusReply(0, new AirportBindStatusReply.Output(java.util.List.of(status)));
        String json = MessageCodec.toJson(original);
        AirportBindStatusReply back = MessageCodec.fromJson(json, AirportBindStatusReply.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AirportBindStatusReply.class));
    }
}
