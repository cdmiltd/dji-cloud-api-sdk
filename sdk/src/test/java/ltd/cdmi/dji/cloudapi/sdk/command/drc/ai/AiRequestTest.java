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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.ai;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 AI Request POJO 的 Jackson 反序列化、序列化与往返闭环。
 */
class AiRequestTest {

    @Test
    @DisplayName("AiModelSelectRequest: {\"index\":0} 反序列化 + 往返闭环")
    void testAiModelSelectRequest() {
        String json = "{\"index\":0}";
        AiModelSelectRequest req = MessageCodec.fromJson(json, AiModelSelectRequest.class);
        assertEquals(0, req.index());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"index\":0"), "JSON 应含 index:0，实际: " + out);
        assertEquals(req, MessageCodec.fromJson(out, AiModelSelectRequest.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AiModelSelectRequest.class));
    }

    @Test
    @DisplayName("AiIdentifySetRequest: {\"on\":1} 反序列化，on 映射为 AiSwitchState.ON")
    void testAiIdentifySetRequest() {
        String json = "{\"on\":1}";
        AiIdentifySetRequest req = MessageCodec.fromJson(json, AiIdentifySetRequest.class);
        assertEquals(AiSwitchState.ON, req.on());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"on\":1"), "JSON 应含 on:1，实际: " + out);
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", AiIdentifySetRequest.class));
    }

    @Test
    @DisplayName("AiIdentifyScoreModeSetRequest: {\"score_mode\":3} 反序列化，映射为 AiScoreMode.CUSTOM")
    void testAiIdentifyScoreModeSetRequest() {
        String json = "{\"score_mode\":3}";
        AiIdentifyScoreModeSetRequest req = MessageCodec.fromJson(json, AiIdentifyScoreModeSetRequest.class);
        assertEquals(AiScoreMode.CUSTOM, req.scoreMode());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"score_mode\":3"), "JSON 应含 score_mode:3，实际: " + out);
    }

    @Test
    @DisplayName("AiIdentifyScoreSetRequest: {\"score\":100} 反序列化 + 往返闭环")
    void testAiIdentifyScoreSetRequest() {
        String json = "{\"score\":100}";
        AiIdentifyScoreSetRequest req = MessageCodec.fromJson(json, AiIdentifyScoreSetRequest.class);
        assertEquals(100, req.score());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"score\":100"), "JSON 应含 score:100，实际: " + out);
        assertEquals(req, MessageCodec.fromJson(out, AiIdentifyScoreSetRequest.class));
    }

    @Test
    @DisplayName("AiIdentifyFilterSetRequest: {\"filters\":[1,2,3]} 反序列化 + 往返闭环")
    void testAiIdentifyFilterSetRequest() {
        String json = "{\"filters\":[1,2,3]}";
        AiIdentifyFilterSetRequest req = MessageCodec.fromJson(json, AiIdentifyFilterSetRequest.class);
        assertEquals(3, req.filters().size());
        assertEquals(1, req.filters().get(0));
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"filters\":[1,2,3]"), "JSON 应含 filters:[1,2,3]，实际: " + out);
    }

    @Test
    @DisplayName("AiSpotlightZoomSetRequest: {\"on\":0} 反序列化，on 映射为 AiSwitchState.OFF")
    void testAiSpotlightZoomSetRequest() {
        String json = "{\"on\":0}";
        AiSpotlightZoomSetRequest req = MessageCodec.fromJson(json, AiSpotlightZoomSetRequest.class);
        assertEquals(AiSwitchState.OFF, req.on());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"on\":0"), "JSON 应含 on:0，实际: " + out);
    }

    @Test
    @DisplayName("AiSpotlightZoomTrackRequest: {\"target_index\":0} 反序列化 + 往返闭环")
    void testAiSpotlightZoomTrackRequest() {
        String json = "{\"target_index\":0}";
        AiSpotlightZoomTrackRequest req = MessageCodec.fromJson(json, AiSpotlightZoomTrackRequest.class);
        assertEquals(0, req.targetIndex());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"target_index\":0"), "JSON 应含 target_index:0，实际: " + out);
        assertEquals(req, MessageCodec.fromJson(out, AiSpotlightZoomTrackRequest.class));
    }

    @Test
    @DisplayName("AiSpotlightZoomSelectRequest: 归一化坐标 ×10000 反序列化 + 往返闭环")
    void testAiSpotlightZoomSelectRequest() {
        String json = "{\"center_x\":0.0,\"center_y\":0.0,\"width\":10000.0,\"height\":10000.0}";
        AiSpotlightZoomSelectRequest req = MessageCodec.fromJson(json, AiSpotlightZoomSelectRequest.class);
        assertEquals(0.0, req.centerX());
        assertEquals(10000.0, req.width());
        String out = MessageCodec.toJson(req);
        assertTrue(out.contains("\"center_x\":0.0"), "JSON 应含 center_x:0.0，实际: " + out);
        assertTrue(out.contains("\"width\":10000.0"), "JSON 应含 width:10000.0，实际: " + out);
        assertEquals(req, MessageCodec.fromJson(out, AiSpotlightZoomSelectRequest.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"center_x\":0.0}", AiSpotlightZoomSelectRequest.class));
    }
}
