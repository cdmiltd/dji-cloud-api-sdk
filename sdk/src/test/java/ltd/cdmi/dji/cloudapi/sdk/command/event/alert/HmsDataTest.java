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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link HmsData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class HmsDataTest {

    private static final String SAMPLE_JSON =
            "{\"list\":[{\"code\":1,\"level\":2,\"module\":3,\"in_the_sky\":1,"
            + "\"device_type\":\"typeA\",\"imminent\":0,"
            + "\"args\":{\"component_index\":0,\"sensor_index\":1}}]}";

    @Test
    @DisplayName("反序列化：snake_case JSON → 嵌套 list + args 结构")
    void testDeserialize() {
        HmsData data = MessageCodec.fromJson(SAMPLE_JSON, HmsData.class);
        assertNotNull(data.list());
        assertEquals(1, data.list().size());
        HmsData.Item item = data.list().get(0);
        assertEquals(1, item.code());
        assertEquals(2, item.level());
        assertEquals(3, item.module());
        assertEquals(1, item.inTheSky());
        assertEquals("typeA", item.deviceType());
        assertEquals(0, item.imminent());
        // 嵌套 args
        assertEquals(0, item.args().componentIndex());
        assertEquals(1, item.args().sensorIndex());
    }

    @Test
    @DisplayName("反序列化：多元素 list 集合验证")
    void testDeserializeMultipleItems() {
        String json = "{\"list\":[{\"code\":1},{\"code\":2},{\"code\":3}]}";
        HmsData data = MessageCodec.fromJson(json, HmsData.class);
        assertEquals(3, data.list().size());
        assertEquals(1, data.list().get(0).code());
        assertEquals(2, data.list().get(1).code());
        assertEquals(3, data.list().get(2).code());
    }

    @Test
    @DisplayName("反序列化：缺失可选字段（args/module 等）容错")
    void testDeserializeMissingOptionalFields() {
        String json = "{\"list\":[{\"code\":1,\"level\":2}]}";
        HmsData data = MessageCodec.fromJson(json, HmsData.class);
        HmsData.Item item = data.list().get(0);
        assertEquals(1, item.code());
        assertEquals(2, item.level());
        assertNull(item.module());
        assertNull(item.deviceType());
        assertNull(item.args());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 in_the_sky/device_type/component_index）")
    void testSerialize() {
        HmsData.Args args = new HmsData.Args(0, 1);
        HmsData.Item item = new HmsData.Item(1, 2, 3, 1, "typeA", 0, args);
        HmsData data = new HmsData(List.of(item));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"in_the_sky\":1"), "JSON 应含 \"in_the_sky\":1，实际: " + json);
        assertTrue(json.contains("\"device_type\":\"typeA\""), "JSON 应含 \"device_type\":\"typeA\"，实际: " + json);
        assertTrue(json.contains("\"component_index\":0"), "JSON 应含 \"component_index\":0，实际: " + json);
        assertTrue(json.contains("\"sensor_index\":1"), "JSON 应含 \"sensor_index\":1，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        HmsData.Args args = new HmsData.Args(0, 1);
        HmsData.Item item = new HmsData.Item(1, 2, 3, 1, "typeA", 0, args);
        HmsData original = new HmsData(List.of(item));
        String json = MessageCodec.toJson(original);
        HmsData back = MessageCodec.fromJson(json, HmsData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 list 字段：反序列化抛 IllegalStateException")
    void testMissingListThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", HmsData.class));
    }
}
