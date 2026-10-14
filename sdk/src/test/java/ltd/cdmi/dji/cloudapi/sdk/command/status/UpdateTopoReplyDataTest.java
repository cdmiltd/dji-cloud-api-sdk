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

package ltd.cdmi.dji.cloudapi.sdk.command.status;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link UpdateTopoReplyData} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：update_topo status_reply data 仅含 {@code result}
 * （DJI 文档输出示例无 {@code sub_type}）能反序列化为 record；缺失 result 时构造器抛 NPE。
 */
class UpdateTopoReplyDataTest {

    @Test
    @DisplayName("反序列化：{\"result\":0} → result=0")
    void testDeserialize() {
        UpdateTopoReplyData data = MessageCodec.fromJson("{\"result\":0}", UpdateTopoReplyData.class);
        assertEquals(0, data.result());
    }

    @Test
    @DisplayName("序列化：UpdateTopoReplyData → JSON 含 result，且不含 sub_type")
    void testSerialize() {
        UpdateTopoReplyData data = new UpdateTopoReplyData(0);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertFalse(json.contains("sub_type"), "JSON 不应含已删除的 sub_type 字段，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        UpdateTopoReplyData original = new UpdateTopoReplyData(0);
        String json = MessageCodec.toJson(original);
        UpdateTopoReplyData back = MessageCodec.fromJson(json, UpdateTopoReplyData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 result 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"sub_type\":0}", UpdateTopoReplyData.class));
    }
}
