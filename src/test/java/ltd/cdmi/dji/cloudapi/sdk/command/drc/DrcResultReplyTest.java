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

package ltd.cdmi.dji.cloudapi.sdk.command.drc;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcResultReply} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC 通用回复 data JSON（含 result）能反序列化为 record</li>
 *   <li>缺失 {@code result} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变（result=0 成功与非 0 错误码）</li>
 * </ol>
 */
class DrcResultReplyTest {

    @Test
    @DisplayName("反序列化：{\"result\":0} → result=0（成功）")
    void testDeserializeSuccess() {
        String json = "{\"result\":0}";
        DrcResultReply reply = MessageCodec.fromJson(json, DrcResultReply.class);
        assertEquals(0, reply.result());
    }

    @Test
    @DisplayName("反序列化：{\"result\":514} → result=514（错误码）")
    void testDeserializeError() {
        String json = "{\"result\":514}";
        DrcResultReply reply = MessageCodec.fromJson(json, DrcResultReply.class);
        assertEquals(514, reply.result());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"result\" 字段")
    void testSerialize() {
        DrcResultReply reply = new DrcResultReply(0);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 \"result\":0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变（成功与错误码全覆盖）")
    void testRoundTrip() {
        for (Integer result : new Integer[]{0, 1, 514, 999}) {
            DrcResultReply original = new DrcResultReply(result);
            String json = MessageCodec.toJson(original);
            DrcResultReply back = MessageCodec.fromJson(json, DrcResultReply.class);
            assertEquals(original, back, "Round-trip 失败: result=" + result);
        }
    }

    @Test
    @DisplayName("缺失 result 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", DrcResultReply.class));
    }
}
