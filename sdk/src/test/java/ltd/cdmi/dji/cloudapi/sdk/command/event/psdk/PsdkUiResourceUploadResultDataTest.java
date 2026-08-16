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
 * 验证 {@link PsdkUiResourceUploadResultData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class PsdkUiResourceUploadResultDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 psdk_index/object_key/size/result）")
    void testDeserialize() {
        String json = "{\"psdk_index\":1,\"object_key\":\"resource/ui.zip\",\"size\":1024,\"result\":0}";
        PsdkUiResourceUploadResultData data = MessageCodec.fromJson(json, PsdkUiResourceUploadResultData.class);
        assertEquals(1, data.psdkIndex());
        assertEquals("resource/ui.zip", data.objectKey());
        assertEquals(1024L, data.size());
        assertEquals(0, data.result());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 psdk_index/object_key/size/result）")
    void testSerialize() {
        PsdkUiResourceUploadResultData data = new PsdkUiResourceUploadResultData(1, "resource/ui.zip", 1024L, 0);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"psdk_index\":1"), "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"object_key\":\"resource/ui.zip\""), "JSON 应含 object_key，实际: " + json);
        assertTrue(json.contains("\"size\":1024"), "JSON 应含 size，实际: " + json);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PsdkUiResourceUploadResultData original = new PsdkUiResourceUploadResultData(2, "res/x.zip", 2048L, 1);
        String json = MessageCodec.toJson(original);
        PsdkUiResourceUploadResultData back = MessageCodec.fromJson(json, PsdkUiResourceUploadResultData.class);
        assertEquals(original, back);
    }
}
