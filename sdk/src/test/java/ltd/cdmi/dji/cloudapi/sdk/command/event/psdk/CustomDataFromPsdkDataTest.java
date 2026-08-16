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

package ltd.cdmi.dji.cloudapi.sdk.command.event.psdk;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link CustomDataFromPsdkData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class CustomDataFromPsdkDataTest {

    @Test
    @DisplayName("反序列化：value 文本 → record")
    void testDeserialize() {
        String json = "{\"value\":\"hello-psdk\"}";
        CustomDataFromPsdkData data = MessageCodec.fromJson(json, CustomDataFromPsdkData.class);
        assertEquals("hello-psdk", data.value());
    }

    @Test
    @DisplayName("反序列化：长文本 value（接近 256 长度限制）")
    void testDeserializeLongValue() {
        String longValue = "y".repeat(255);
        String json = "{\"value\":\"" + longValue + "\"}";
        CustomDataFromPsdkData data = MessageCodec.fromJson(json, CustomDataFromPsdkData.class);
        assertEquals(longValue, data.value());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"value\"")
    void testSerialize() {
        CustomDataFromPsdkData data = new CustomDataFromPsdkData("psdk-payload");
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"value\":\"psdk-payload\""), "JSON 应含 \"value\":\"psdk-payload\"，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        CustomDataFromPsdkData original = new CustomDataFromPsdkData("round-trip-psdk");
        String json = MessageCodec.toJson(original);
        CustomDataFromPsdkData back = MessageCodec.fromJson(json, CustomDataFromPsdkData.class);
        assertEquals(original, back);
    }
}
