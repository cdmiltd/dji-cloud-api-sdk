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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link AiInfoPushData} 的 Jackson 反序列化与嵌套结构绑定。
 *
 * <p>使用 DJI v1.16 文档 drc_ai_info_push 的 Example JSON 验证完整解析。
 */
class AiInfoPushDataTest {

    /**
     * DJI 文档 drc_ai_info_push Example JSON（完整）。
     */
    private static final String EXAMPLE_JSON = """
            {
              "identify_on": 1,
              "spotlight_zoom_on": 1,
              "ai_spotlight_zoom": {
                "state": 0,
                "state_reason": 0
              },
              "ai_model_list": [
                {"index": 0, "signed_name": "DJI"},
                {"index": 128, "signed_name": "高速公路场景识别"}
              ],
              "selected_ai_model": {
                "index": 0,
                "score": 100,
                "score_mode": 1,
                "image_source": [1, 2, 3],
                "digital_effect": [0, 1, 2],
                "filters": [1, 2, 3],
                "labels": [
                  {"index": 0, "name": "摩托车"},
                  {"index": 1, "name": "自行车"}
                ]
              },
              "ai_wayline_state": {
                "sequence_shot": true,
                "wait_control": true,
                "record": true,
                "normal_shot": true,
                "count_down_time": 23,
                "alert_uuid": "xxxxxxxxxxxxxxx"
              }
            }""";

    @Test
    @DisplayName("反序列化 DJI Example JSON：所有字段正确绑定")
    void testDeserializeFullExample() {
        AiInfoPushData data = MessageCodec.fromJson(EXAMPLE_JSON, AiInfoPushData.class);

        // 顶层开关字段
        assertEquals(AiSwitchState.ON, data.identifyOn());
        assertEquals(AiSwitchState.ON, data.spotlightZoomOn());

        // ai_spotlight_zoom 子结构
        assertNotNull(data.aiSpotlightZoom());
        assertEquals(AiTrackState.IDLE, data.aiSpotlightZoom().state());
        assertEquals(AiTrackStateReason.NORMAL, data.aiSpotlightZoom().stateReason());

        // ai_model_list 子结构
        assertNotNull(data.aiModelList());
        assertEquals(2, data.aiModelList().size());
        assertEquals(0, data.aiModelList().get(0).index());
        assertEquals("DJI", data.aiModelList().get(0).signedName());
        assertEquals(128, data.aiModelList().get(1).index());
        assertEquals("高速公路场景识别", data.aiModelList().get(1).signedName());

        // selected_ai_model 子结构
        assertNotNull(data.selectedAiModel());
        assertEquals(0, data.selectedAiModel().index());
        assertEquals(100, data.selectedAiModel().score());
        assertEquals(AiScoreMode.COUNT, data.selectedAiModel().scoreMode());
        assertEquals(3, data.selectedAiModel().imageSource().size());
        assertEquals(1, data.selectedAiModel().imageSource().get(0));
        assertEquals(3, data.selectedAiModel().digitalEffect().size());
        assertEquals(0, data.selectedAiModel().digitalEffect().get(0));
        assertEquals(3, data.selectedAiModel().filters().size());
        assertEquals(2, data.selectedAiModel().labels().size());
        assertEquals("摩托车", data.selectedAiModel().labels().get(0).name());
        assertEquals(1, data.selectedAiModel().labels().get(1).index());

        // ai_wayline_state 子结构（@Inferred）
        assertNotNull(data.aiWaylineState());
        assertTrue(data.aiWaylineState().sequenceShot());
        assertTrue(data.aiWaylineState().waitControl());
        assertEquals(23, data.aiWaylineState().countDownTime());
        assertEquals("xxxxxxxxxxxxxxx", data.aiWaylineState().alertUuid());
    }

    @Test
    @DisplayName("序列化后含 snake_case 字段名")
    void testSerializeSnakeCase() {
        AiInfoPushData data = MessageCodec.fromJson(EXAMPLE_JSON, AiInfoPushData.class);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"identify_on\":1"), "应含 identify_on，实际: " + json);
        assertTrue(json.contains("\"spotlight_zoom_on\":1"), "应含 spotlight_zoom_on，实际: " + json);
        assertTrue(json.contains("\"ai_spotlight_zoom\""), "应含 ai_spotlight_zoom，实际: " + json);
        assertTrue(json.contains("\"ai_model_list\""), "应含 ai_model_list，实际: " + json);
        assertTrue(json.contains("\"selected_ai_model\""), "应含 selected_ai_model，实际: " + json);
        assertTrue(json.contains("\"ai_wayline_state\""), "应含 ai_wayline_state，实际: " + json);
        assertTrue(json.contains("\"signed_name\""), "应含 signed_name，实际: " + json);
        assertTrue(json.contains("\"image_source\""), "应含 image_source，实际: " + json);
        assertTrue(json.contains("\"digital_effect\""), "应含 digital_effect，实际: " + json);
        assertTrue(json.contains("\"count_down_time\""), "应含 count_down_time，实际: " + json);
        assertTrue(json.contains("\"alert_uuid\""), "应含 alert_uuid，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        AiInfoPushData original = MessageCodec.fromJson(EXAMPLE_JSON, AiInfoPushData.class);
        String json = MessageCodec.toJson(original);
        AiInfoPushData back = MessageCodec.fromJson(json, AiInfoPushData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("退出原因 state_reason=168 映射为 EXIT_RC_SIGNAL_LOST")
    void testExitStateReason() {
        String json = """
                {"identify_on":0,"spotlight_zoom_on":0,
                 "ai_spotlight_zoom":{"state":0,"state_reason":168}}""";
        AiInfoPushData data = MessageCodec.fromJson(json, AiInfoPushData.class);
        assertEquals(AiTrackStateReason.EXIT_RC_SIGNAL_LOST, data.aiSpotlightZoom().stateReason());
    }
}
