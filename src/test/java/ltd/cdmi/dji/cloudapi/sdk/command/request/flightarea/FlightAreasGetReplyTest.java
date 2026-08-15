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

package ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea.FlightAreasGetReply.Output;
import ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea.FlightAreasGetReply.SyncFile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlightAreasGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：flight_areas_get 回复 data（result + output{file{name,checksum}}）
 * 能反序列化为嵌套 record；缺失 result 时构造器抛 NPE。
 */
class FlightAreasGetReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"file\":{\"name\":\"flight_area.geofence\",\"checksum\":\"abc123\"}}}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 Output + SyncFile")
    void testDeserialize() {
        FlightAreasGetReply reply = MessageCodec.fromJson(SAMPLE_JSON, FlightAreasGetReply.class);
        assertEquals(0, reply.result());
        SyncFile file = reply.output().file();
        assertEquals("flight_area.geofence", file.name());
        assertEquals("abc123", file.checksum());
    }

    @Test
    @DisplayName("序列化：含嵌套 Output/SyncFile 的 record → JSON")
    void testSerialize() {
        SyncFile file = new SyncFile("flight_area.geofence", "abc123");
        Output output = new Output(file);
        FlightAreasGetReply reply = new FlightAreasGetReply(0, output);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"name\":\"flight_area.geofence\""), "JSON 应含 name，实际: " + json);
        assertTrue(json.contains("\"checksum\":\"abc123\""), "JSON 应含 checksum，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        SyncFile file = new SyncFile("fa.bin", "deadbeef");
        FlightAreasGetReply original = new FlightAreasGetReply(0, new Output(file));
        String json = MessageCodec.toJson(original);
        FlightAreasGetReply back = MessageCodec.fromJson(json, FlightAreasGetReply.class);
        assertEquals(0, back.result());
        assertEquals("fa.bin", back.output().file().name());
        assertEquals("deadbeef", back.output().file().checksum());
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", FlightAreasGetReply.class));
    }
}
