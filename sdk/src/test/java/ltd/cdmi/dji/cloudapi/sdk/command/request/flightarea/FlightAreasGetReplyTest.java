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

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea.FlightAreasGetReply.File;
import ltd.cdmi.dji.cloudapi.sdk.command.request.flightarea.FlightAreasGetReply.Output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link FlightAreasGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：flight_areas_get 回复 data（result + output{files[{name,url,checksum,size}]}）
 * 能反序列化为嵌套 record 数组；缺失 result 时构造器抛 NPE。
 */
class FlightAreasGetReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"files\":[{\"name\":\"flight_area.geofence\","
            + "\"url\":\"https://oss.example.com/fa.geofence\",\"checksum\":\"abc123\",\"size\":1024}]}}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 Output + File 数组")
    void testDeserialize() {
        FlightAreasGetReply reply = MessageCodec.fromJson(SAMPLE_JSON, FlightAreasGetReply.class);
        assertEquals(0, reply.result());
        List<File> files = reply.output().files();
        assertEquals(1, files.size());
        assertEquals("flight_area.geofence", files.get(0).name());
        assertEquals("https://oss.example.com/fa.geofence", files.get(0).url());
        assertEquals("abc123", files.get(0).checksum());
        assertEquals(1024, files.get(0).size());
    }

    @Test
    @DisplayName("序列化：含嵌套 Output/File 的 record → JSON（files 为数组）")
    void testSerialize() {
        File file = new File("flight_area.geofence", "https://oss.example.com/fa.geofence", "abc123", 1024);
        Output output = new Output(List.of(file));
        FlightAreasGetReply reply = new FlightAreasGetReply(0, output);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"files\":["), "JSON 应含 files 数组，实际: " + json);
        assertTrue(json.contains("\"url\":\"https://oss.example.com/fa.geofence\""), "JSON 应含 url，实际: " + json);
        assertTrue(json.contains("\"size\":1024"), "JSON 应含 size，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        File file = new File("fa.bin", "https://oss.example.com/fa.bin", "deadbeef", 2048);
        FlightAreasGetReply original = new FlightAreasGetReply(0, new Output(List.of(file)));
        String json = MessageCodec.toJson(original);
        FlightAreasGetReply back = MessageCodec.fromJson(json, FlightAreasGetReply.class);
        assertEquals(0, back.result());
        assertEquals("fa.bin", back.output().files().get(0).name());
        assertEquals("https://oss.example.com/fa.bin", back.output().files().get(0).url());
        assertEquals(2048, back.output().files().get(0).size());
    }

    @Test
    @DisplayName("空 files 数组：result=0 且 output.files 为空数组（无自定义飞行区）")
    void testEmptyFiles() {
        String json = "{\"result\":0,\"output\":{\"files\":[]}}";
        FlightAreasGetReply reply = MessageCodec.fromJson(json, FlightAreasGetReply.class);
        assertEquals(0, reply.result());
        assertTrue(reply.output().files().isEmpty());
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", FlightAreasGetReply.class));
    }
}
