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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlighttaskProgressGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：flighttask_progress_get 回复 data（result + output{flight_id, progress{current_step,
 * percent}, status}）能反序列化为 record；缺失 result 时构造器抛 NPE。
 */
class FlighttaskProgressGetReplyTest {

    @Test
    @DisplayName("反序列化：output 具体结构绑定（flight_id/progress.current_step/percent/status）")
    void testDeserialize() {
        String json = "{\"result\":0,\"output\":{\"flight_id\":\"ft-1\","
                + "\"progress\":{\"current_step\":2,\"percent\":55},\"status\":\"sent\"}}";
        FlighttaskProgressGetReply reply = MessageCodec.fromJson(json, FlighttaskProgressGetReply.class);
        assertEquals(0, reply.result());
        assertEquals("ft-1", reply.output().flightId());
        assertEquals(2, reply.output().progress().currentStep());
        assertEquals(55, reply.output().progress().percent());
        assertEquals("sent", reply.output().status());
    }

    @Test
    @DisplayName("序列化：Output → JSON 含 wire key flight_id / current_step")
    void testSerialize() {
        FlighttaskProgressGetReply reply = new FlighttaskProgressGetReply(0,
                new FlighttaskProgressGetReply.Output("ft-1",
                        new FlighttaskProgressGetReply.Progress(2, 55), "sent"));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"flight_id\":\"ft-1\""), "JSON 应含 flight_id，实际: " + json);
        assertTrue(json.contains("\"current_step\":2"), "JSON 应含 current_step，实际: " + json);
        assertTrue(json.contains("\"percent\":55"), "JSON 应含 percent，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持结构不变")
    void testRoundTrip() {
        FlighttaskProgressGetReply original = new FlighttaskProgressGetReply(0,
                new FlighttaskProgressGetReply.Output("ft-1",
                        new FlighttaskProgressGetReply.Progress(2, 55), "sent"));
        String json = MessageCodec.toJson(original);
        FlighttaskProgressGetReply back = MessageCodec.fromJson(json, FlighttaskProgressGetReply.class);
        assertEquals(0, back.result());
        assertEquals("ft-1", back.output().flightId());
        assertEquals(2, back.output().progress().currentStep());
        assertEquals("sent", back.output().status());
    }

    @Test
    @DisplayName("可空字段：output 省略时为 null")
    void testOptionalOutputNull() {
        String json = "{\"result\":0}";
        FlighttaskProgressGetReply reply = MessageCodec.fromJson(json, FlighttaskProgressGetReply.class);
        assertEquals(0, reply.result());
        assertNull(reply.output(), "output 省略时应为 null");
    }

    @Test
    @DisplayName("缺失 result 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", FlighttaskProgressGetReply.class));
    }
}
