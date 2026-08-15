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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PoiCircleStatusData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class PoiCircleStatusDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 circle_radius/circle_speed/max_circle_speed）")
    void testDeserialize() {
        String json = "{\"status\":\"in_progress\",\"reason\":0,\"circle_radius\":10.5,"
                + "\"circle_speed\":2.0,\"max_circle_speed\":5.0}";
        PoiCircleStatusData data = MessageCodec.fromJson(json, PoiCircleStatusData.class);
        assertEquals("in_progress", data.status());
        assertEquals(0, data.reason());
        assertEquals(10.5, data.circleRadius());
        assertEquals(2.0, data.circleSpeed());
        assertEquals(5.0, data.maxCircleSpeed());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 circle_radius/max_circle_speed）")
    void testSerialize() {
        PoiCircleStatusData data = new PoiCircleStatusData("ok", 0, 10.5, 2.0, 5.0);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"circle_radius\":10.5"), "JSON 应含 circle_radius，实际: " + json);
        assertTrue(json.contains("\"max_circle_speed\":5.0"), "JSON 应含 max_circle_speed，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PoiCircleStatusData original = new PoiCircleStatusData("in_progress", 0, 10.5, 2.0, 5.0);
        String json = MessageCodec.toJson(original);
        PoiCircleStatusData back = MessageCodec.fromJson(json, PoiCircleStatusData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 status：反序列化抛 IllegalStateException")
    void testMissingStatusThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"reason\":0}", PoiCircleStatusData.class));
    }
}
