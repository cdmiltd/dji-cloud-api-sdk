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
import ltd.cdmi.dji.cloudapi.sdk.command.request.registration.AirportOrganizationBindReply.ErrorInfo;
import ltd.cdmi.dji.cloudapi.sdk.command.request.registration.AirportOrganizationBindReply.Output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link AirportOrganizationBindReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_organization_bind 回复 data（result + output{err_infos[{err_code,err_sn}]}）
 * 能反序列化为嵌套 record；result=0 但 output.err_infos 非空表示设备级失败。
 * 缺失 result 时构造器抛 NPE；output 为可空字段。
 */
class AirportOrganizationBindReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"err_infos\":[{\"err_code\":514001,\"err_sn\":\"SN001\"}]}}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 Output + ErrorInfo 列表")
    void testDeserialize() {
        AirportOrganizationBindReply reply = MessageCodec.fromJson(SAMPLE_JSON, AirportOrganizationBindReply.class);
        assertEquals(0, reply.result());
        List<ErrorInfo> errs = reply.output().errInfos();
        assertEquals(1, errs.size());
        assertEquals(514001, errs.get(0).errCode());
        assertEquals("SN001", errs.get(0).errSn());
    }

    @Test
    @DisplayName("序列化：含嵌套 err_infos 的 record → JSON（snake_case）")
    void testSerialize() {
        ErrorInfo err = new ErrorInfo(514001, "SN001");
        Output output = new Output(List.of(err));
        AirportOrganizationBindReply reply = new AirportOrganizationBindReply(0, output);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"err_infos\":"), "JSON 应含 err_infos，实际: " + json);
        assertTrue(json.contains("\"err_code\":514001"), "JSON 应含 err_code，实际: " + json);
        assertTrue(json.contains("\"err_sn\":\"SN001\""), "JSON 应含 err_sn，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        ErrorInfo err = new ErrorInfo(514001, "SN001");
        AirportOrganizationBindReply original = new AirportOrganizationBindReply(0, new Output(List.of(err)));
        String json = MessageCodec.toJson(original);
        AirportOrganizationBindReply back = MessageCodec.fromJson(json, AirportOrganizationBindReply.class);
        assertEquals(0, back.result());
        assertEquals(1, back.output().errInfos().size());
        assertEquals(514001, back.output().errInfos().get(0).errCode());
        assertEquals("SN001", back.output().errInfos().get(0).errSn());
    }

    @Test
    @DisplayName("可空字段：output 省略时为 null（result=0 且无设备级错误）")
    void testOptionalOutputNull() {
        String json = "{\"result\":0}";
        AirportOrganizationBindReply reply = MessageCodec.fromJson(json, AirportOrganizationBindReply.class);
        assertEquals(0, reply.result());
        assertNull(reply.output(), "output 省略时应为 null");
    }

    @Test
    @DisplayName("空 err_infos 列表：result=0 但 output.err_infos 为空数组（无设备级错误）")
    void testEmptyErrInfos() {
        String json = "{\"result\":0,\"output\":{\"err_infos\":[]}}";
        AirportOrganizationBindReply reply = MessageCodec.fromJson(json, AirportOrganizationBindReply.class);
        assertEquals(0, reply.result());
        assertNotNull(reply.output());
        assertTrue(reply.output().errInfos().isEmpty());
    }

    @Test
    @DisplayName("缺失 result 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", AirportOrganizationBindReply.class));
    }
}
