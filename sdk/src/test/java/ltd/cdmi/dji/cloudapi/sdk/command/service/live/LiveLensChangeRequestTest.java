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

package ltd.cdmi.dji.cloudapi.sdk.command.service.live;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link LiveLensChangeRequest} 的 Jackson 反序列化、序列化与设备差异兼容性。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>Dock 场景：仅 {@code video_type}，{@code video_id} 为 null 且不序列化（{@code @JsonInclude(NON_NULL)}）</li>
 *   <li>RC Plus/RC Pro 场景：{@code video_type} + {@code video_id} 同时存在</li>
 *   <li>缺失 {@code video_type} 时构造器抛 {@link NullPointerException}</li>
 *   <li>序列化→反序列化往返闭环保持不变</li>
 * </ol>
 */
class LiveLensChangeRequestTest {

    @Test
    @DisplayName("Dock 场景：反序列化 {\"video_type\":\"normal\"} → videoId 为 null")
    void testDeserializeDock() {
        String json = "{\"video_type\":\"normal\"}";
        LiveLensChangeRequest req = MessageCodec.fromJson(json, LiveLensChangeRequest.class);
        assertEquals("normal", req.videoType());
        assertNull(req.videoId());
    }

    @Test
    @DisplayName("RC Plus/RC Pro 场景：反序列化 {\"video_type\":\"normal\",\"video_id\":\"xxx\"} → 两字段均绑定")
    void testDeserializeRc() {
        String json = "{\"video_type\":\"normal\",\"video_id\":\"xxx-yyy\"}";
        LiveLensChangeRequest req = MessageCodec.fromJson(json, LiveLensChangeRequest.class);
        assertEquals("normal", req.videoType());
        assertEquals("xxx-yyy", req.videoId());
    }

    @Test
    @DisplayName("Dock 序列化：videoId=null 时 JSON 不含 video_id 字段")
    void testSerializeDock() {
        LiveLensChangeRequest req = new LiveLensChangeRequest("normal", null);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"video_type\":\"normal\""), "JSON 应含 video_type，实际: " + json);
        assertFalse(json.contains("video_id"), "JSON 不应含 video_id（@JsonInclude NON_NULL），实际: " + json);
    }

    @Test
    @DisplayName("RC 序列化：videoId 非空时 JSON 含 video_id 字段")
    void testSerializeRc() {
        LiveLensChangeRequest req = new LiveLensChangeRequest("zoom", "rc-001");
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"video_type\":\"zoom\""), "JSON 应含 video_type，实际: " + json);
        assertTrue(json.contains("\"video_id\":\"rc-001\""), "JSON 应含 video_id，实际: " + json);
    }

    @Test
    @DisplayName("必填校验：缺失 video_type 抛 IllegalStateException（MessageCodec 包装 NPE）")
    void testMissingVideoTypeThrows() {
        String json = "{\"video_id\":\"xxx\"}";
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(json, LiveLensChangeRequest.class));
        assertTrue(ex.getMessage().contains("video_type") || ex.getMessage().contains("videoType"),
                "异常消息应提及 video_type，实际: " + ex.getMessage());
    }

    @Test
    @DisplayName("往返闭环：RC 场景序列化→反序列化保持不变")
    void testRoundTripRc() {
        LiveLensChangeRequest original = new LiveLensChangeRequest("wide", "cam-042");
        String json = MessageCodec.toJson(original);
        LiveLensChangeRequest back = MessageCodec.fromJson(json, LiveLensChangeRequest.class);
        assertEquals(original.videoType(), back.videoType());
        assertEquals(original.videoId(), back.videoId());
    }

    @Test
    @DisplayName("往返闭环：Dock 场景序列化→反序列化保持不变（videoId 始终为 null）")
    void testRoundTripDock() {
        LiveLensChangeRequest original = new LiveLensChangeRequest("normal", null);
        String json = MessageCodec.toJson(original);
        LiveLensChangeRequest back = MessageCodec.fromJson(json, LiveLensChangeRequest.class);
        assertEquals(original.videoType(), back.videoType());
        assertNull(back.videoId());
    }
}
