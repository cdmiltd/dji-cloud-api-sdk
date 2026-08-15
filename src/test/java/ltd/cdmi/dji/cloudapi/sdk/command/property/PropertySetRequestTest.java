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

package ltd.cdmi.dji.cloudapi.sdk.command.property;

import java.util.LinkedHashMap;
import java.util.Map;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PropertySetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：property/set 通道的 data JSON（属性名→值映射，snake_case 键）
 * 能反序列化为 {@code Map<String, Object>}，且序列化→反序列化保持映射不变。
 *
 * <p>注：record 的 {@code properties} 字段在 JSON 中以同名键 {@code "properties"} 包裹，
 * DJI 协议 data 为扁平 map（无包裹），调用方需将扁平 map 放入 {@code properties} 字段。
 */
class PropertySetRequestTest {

    private static final String SAMPLE_JSON =
            "{\"properties\":{\"battery_store_mode\":1,\"cover_state\":0}}";

    @Test
    @DisplayName("反序列化：{\"properties\":{...}} → properties 映射")
    void testDeserialize() {
        PropertySetRequest req = MessageCodec.fromJson(SAMPLE_JSON, PropertySetRequest.class);
        Map<String, Object> props = req.properties();
        assertEquals(2, props.size());
        assertEquals(1, ((Number) props.get("battery_store_mode")).intValue());
        assertEquals(0, ((Number) props.get("cover_state")).intValue());
    }

    @Test
    @DisplayName("序列化：properties 映射 → JSON 含 \"properties\" 包裹键")
    void testSerialize() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("battery_store_mode", 1);
        props.put("cover_state", 0);
        PropertySetRequest req = new PropertySetRequest(props);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"properties\":"), "JSON 应含 properties 包裹键，实际: " + json);
        assertTrue(json.contains("\"battery_store_mode\":1"), "JSON 应含 battery_store_mode:1，实际: " + json);
        assertTrue(json.contains("\"cover_state\":0"), "JSON 应含 cover_state:0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持映射不变")
    void testRoundTrip() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("silent_mode", 1);
        props.put("height_limit", 120);
        PropertySetRequest original = new PropertySetRequest(props);
        String json = MessageCodec.toJson(original);
        PropertySetRequest back = MessageCodec.fromJson(json, PropertySetRequest.class);
        assertEquals(original.properties().size(), back.properties().size());
        assertEquals(1, ((Number) back.properties().get("silent_mode")).intValue());
        assertEquals(120, ((Number) back.properties().get("height_limit")).intValue());
    }
}
