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

package ltd.cdmi.dji.cloudapi.sdk.websocket.data;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MapGroupRefreshData} 图层刷新推送 data 测试。
 *
 * <p>验证 {@code {ids: [group_id, ...]}} 结构的 List 序列化/反序列化。
 */
class MapGroupRefreshDataTest {

    private static final String JSON = "{\"ids\":[\"group-1\",\"group-2\",\"group-3\"]}";

    @Test
    @DisplayName("反序列化：ids 数组 → List<String>")
    void testDeserialize() {
        MapGroupRefreshData data = MessageCodec.fromJson(JSON, MapGroupRefreshData.class);
        assertNotNull(data.ids());
        assertEquals(3, data.ids().size());
        assertEquals("group-1", data.ids().get(0));
        assertEquals("group-2", data.ids().get(1));
        assertEquals("group-3", data.ids().get(2));
    }

    @Test
    @DisplayName("序列化：List<String> → ids 数组")
    void testSerialize() {
        MapGroupRefreshData data = new MapGroupRefreshData(List.of("g1", "g2"));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"ids\":[\"g1\",\"g2\"]"));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        MapGroupRefreshData original = new MapGroupRefreshData(List.of("a", "b", "c"));
        String json = MessageCodec.toJson(original);
        MapGroupRefreshData round = MessageCodec.fromJson(json, MapGroupRefreshData.class);
        assertEquals(original.ids(), round.ids());
    }

    @Test
    @DisplayName("空数组 ids 反序列化为空 List")
    void testEmptyIds() {
        MapGroupRefreshData data = MessageCodec.fromJson("{\"ids\":[]}", MapGroupRefreshData.class);
        assertNotNull(data.ids());
        assertTrue(data.ids().isEmpty());
    }

    @Test
    @DisplayName("parseWs：map_group_refresh 推送解析")
    void testParseWs() {
        String payload = "{\"biz_code\":\"map_group_refresh\",\"version\":\"1.0\",\"timestamp\":1700000000000,"
                + "\"data\":{\"ids\":[\"g1\",\"g2\"]}}";
        var msg = MessageCodec.parseWs(payload, MapGroupRefreshData.class);
        assertEquals("map_group_refresh", msg.bizCode());
        assertEquals(2, msg.data().ids().size());
        assertEquals("g1", msg.data().ids().get(0));
    }
}
