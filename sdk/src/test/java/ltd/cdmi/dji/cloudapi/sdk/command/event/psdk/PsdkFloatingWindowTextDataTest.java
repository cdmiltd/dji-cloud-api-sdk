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
 * 验证 {@link PsdkFloatingWindowTextData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class PsdkFloatingWindowTextDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 psdk_index/value）")
    void testDeserialize() {
        String json = "{\"psdk_index\":1,\"value\":\"floating-text\"}";
        PsdkFloatingWindowTextData data = MessageCodec.fromJson(json, PsdkFloatingWindowTextData.class);
        assertEquals(1, data.psdkIndex());
        assertEquals("floating-text", data.value());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 psdk_index）")
    void testSerialize() {
        PsdkFloatingWindowTextData data = new PsdkFloatingWindowTextData(1, "floating-text");
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"psdk_index\":1"), "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"value\":\"floating-text\""), "JSON 应含 value，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PsdkFloatingWindowTextData original = new PsdkFloatingWindowTextData(2, "round-trip");
        String json = MessageCodec.toJson(original);
        PsdkFloatingWindowTextData back = MessageCodec.fromJson(json, PsdkFloatingWindowTextData.class);
        assertEquals(original, back);
    }
}
