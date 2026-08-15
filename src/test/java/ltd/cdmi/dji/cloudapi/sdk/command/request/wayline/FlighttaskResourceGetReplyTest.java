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

package ltd.cdmi.dji.cloudapi.sdk.command.request.wayline;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.request.wayline.FlighttaskResourceGetReply.File;
import ltd.cdmi.dji.cloudapi.sdk.command.request.wayline.FlighttaskResourceGetReply.Output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskResourceGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：flighttask_resource_get 回复 data（result + output{file{url,fingerprint}}）
 * 能反序列化为嵌套 record；缺失 result 时构造器抛 NPE。
 */
class FlighttaskResourceGetReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"file\":{\"url\":\"https://example.com/wayline.kmz\","
            + "\"fingerprint\":\"md5hash\"}}}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 Output + File")
    void testDeserialize() {
        FlighttaskResourceGetReply reply = MessageCodec.fromJson(SAMPLE_JSON, FlighttaskResourceGetReply.class);
        assertEquals(0, reply.result());
        File file = reply.output().file();
        assertEquals("https://example.com/wayline.kmz", file.url());
        assertEquals("md5hash", file.fingerprint());
    }

    @Test
    @DisplayName("序列化：含嵌套 Output/File 的 record → JSON")
    void testSerialize() {
        File file = new File("https://example.com/wayline.kmz", "md5hash");
        FlighttaskResourceGetReply reply = new FlighttaskResourceGetReply(0, new Output(file));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"url\":\"https://example.com/wayline.kmz\""), "JSON 应含 url，实际: " + json);
        assertTrue(json.contains("\"fingerprint\":\"md5hash\""), "JSON 应含 fingerprint，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        File file = new File("https://example.com/w.kmz", "abc123");
        FlighttaskResourceGetReply original = new FlighttaskResourceGetReply(0, new Output(file));
        String json = MessageCodec.toJson(original);
        FlighttaskResourceGetReply back = MessageCodec.fromJson(json, FlighttaskResourceGetReply.class);
        assertEquals(0, back.result());
        assertEquals("https://example.com/w.kmz", back.output().file().url());
        assertEquals("abc123", back.output().file().fingerprint());
    }

    @Test
    @DisplayName("可空字段：output 省略时为 null")
    void testOptionalOutputNull() {
        String json = "{\"result\":0}";
        FlighttaskResourceGetReply reply = MessageCodec.fromJson(json, FlighttaskResourceGetReply.class);
        assertEquals(0, reply.result());
        assertNull(reply.output(), "output 省略时应为 null");
    }

    @Test
    @DisplayName("缺失 result 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", FlighttaskResourceGetReply.class));
    }
}
