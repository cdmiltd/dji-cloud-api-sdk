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
 * 验证 {@link AirportOrganizationGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：airport_organization_get 回复 data（result=0 表示绑定码与组织 ID 校验通过）
 * 能反序列化为 record；缺失 result 时构造器抛 NPE。
 */
class AirportOrganizationGetReplyTest {

    @Test
    @DisplayName("反序列化：{\"result\":0} → result()==0（校验通过）")
    void testDeserialize() {
        AirportOrganizationGetReply reply = MessageCodec.fromJson("{\"result\":0}", AirportOrganizationGetReply.class);
        assertEquals(0, reply.result());
    }

    @Test
    @DisplayName("序列化：AirportOrganizationGetReply(0) → JSON 含 \"result\":0")
    void testSerialize() {
        AirportOrganizationGetReply reply = new AirportOrganizationGetReply(0);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 \"result\":0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持 result 不变")
    void testRoundTrip() {
        AirportOrganizationGetReply original = new AirportOrganizationGetReply(0);
        String json = MessageCodec.toJson(original);
        AirportOrganizationGetReply back = MessageCodec.fromJson(json, AirportOrganizationGetReply.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AirportOrganizationGetReply.class));
    }
}
