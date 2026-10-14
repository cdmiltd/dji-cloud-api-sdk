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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.psdk;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增 PSDK DRC 请求 POJO 的 wire key 与往返闭环。
 */
class DrcPsdkRequestTest {

    @Test
    @DisplayName("drc_psdk_input_box_text_set：psdk_index + value")
    void testInputBoxText() {
        DrcPsdkInputBoxTextSetRequest req = new DrcPsdkInputBoxTextSetRequest(0, "hello");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":0"), json);
        assertTrue(json.contains("\"value\":\"hello\""), json);
        assertEquals(req, MessageCodec.fromJson(json, DrcPsdkInputBoxTextSetRequest.class));
    }

    @Test
    @DisplayName("drc_psdk_widget_value_set：psdk_index + index + value")
    void testWidgetValue() {
        DrcPsdkWidgetValueSetRequest req = new DrcPsdkWidgetValueSetRequest(1, 2, 3);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":1"), json);
        assertTrue(json.contains("\"index\":2"), json);
        assertTrue(json.contains("\"value\":3"), json);
        assertEquals(req, MessageCodec.fromJson(json, DrcPsdkWidgetValueSetRequest.class));
    }
}
