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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PropertySetReply} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：property/set_reply 的 data JSON 为扁平 map（属性名→{@link PropertySetResult}，无 "properties" 包裹键），
 * {@code @JsonAnyGetter} + {@code @JsonCreator(DELEGATING)} 实现扁平 map ↔ record 双向映射。
 */
class PropertySetReplyTest {

    /** DJI 协议 data：扁平 map，无 "properties" 包裹键 */
    private static final String SAMPLE_JSON =
            "{\"battery_store_mode\":{\"code\":0},\"cover_state\":{\"code\":0}}";

    @Test
    @DisplayName("反序列化：扁平 map → properties 映射（嵌套 PropertySetResult）")
    void testDeserialize() {
        PropertySetReply reply = MessageCodec.fromJson(SAMPLE_JSON, PropertySetReply.class);
        Map<String, PropertySetResult> props = reply.properties();
        assertEquals(2, props.size());
        assertEquals(0, props.get("battery_store_mode").code());
        assertEquals(0, props.get("cover_state").code());
    }

    @Test
    @DisplayName("序列化：properties 映射 → 扁平 map（无 \"properties\" 包裹键）")
    void testSerialize() {
        Map<String, PropertySetResult> props = new LinkedHashMap<>();
        props.put("battery_store_mode", new PropertySetResult(0));
        props.put("cover_state", new PropertySetResult(0));
        PropertySetReply reply = new PropertySetReply(props);
        String json = MessageCodec.toJson(reply);
        assertFalse(json.contains("\"properties\""), "JSON 不应含 properties 包裹键，实际: " + json);
        assertTrue(json.contains("\"battery_store_mode\":{\"code\":0}"),
                "JSON 应含嵌套 battery_store_mode:{\"code\":0}，实际: " + json);
        assertTrue(json.contains("\"cover_state\":{\"code\":0}"),
                "JSON 应含嵌套 cover_state:{\"code\":0}，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持映射与嵌套 code 不变")
    void testRoundTrip() {
        Map<String, PropertySetResult> props = new LinkedHashMap<>();
        props.put("silent_mode", new PropertySetResult(0));
        props.put("height_limit", new PropertySetResult(514001));
        PropertySetReply original = new PropertySetReply(props);
        String json = MessageCodec.toJson(original);
        PropertySetReply back = MessageCodec.fromJson(json, PropertySetReply.class);
        assertEquals(2, back.properties().size());
        assertEquals(0, back.properties().get("silent_mode").code());
        assertEquals(514001, back.properties().get("height_limit").code());
    }
}
