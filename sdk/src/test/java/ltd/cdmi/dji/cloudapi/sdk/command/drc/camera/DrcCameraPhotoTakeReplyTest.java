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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcCameraPhotoTakeReply} 的 Jackson 反序列化、序列化与必填字段校验。
 *
 * <p><b>核心证明</b>：DRC 通道 drc_camera_photo_take 回复 data（result + status，@Inferred）
 * 能反序列化为 record；status 省略时为 null；缺失 result 时构造器抛 NPE。
 */
class DrcCameraPhotoTakeReplyTest {

    @Test
    @DisplayName("反序列化：{\"result\":0,\"status\":\"ok\"} → result=0, status=ok")
    void testDeserialize() {
        DrcCameraPhotoTakeReply reply =
                MessageCodec.fromJson("{\"result\":0,\"status\":\"ok\"}", DrcCameraPhotoTakeReply.class);
        assertEquals(0, reply.result());
        assertEquals("ok", reply.status());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 result 与 status")
    void testSerialize() {
        DrcCameraPhotoTakeReply reply = new DrcCameraPhotoTakeReply(0, "ok");
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"status\":\"ok\""), "JSON 应含 status，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        DrcCameraPhotoTakeReply original = new DrcCameraPhotoTakeReply(0, "ok");
        String json = MessageCodec.toJson(original);
        DrcCameraPhotoTakeReply back = MessageCodec.fromJson(json, DrcCameraPhotoTakeReply.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("可空字段：status 省略时为 null")
    void testOptionalStatusNull() {
        DrcCameraPhotoTakeReply reply = MessageCodec.fromJson("{\"result\":0}", DrcCameraPhotoTakeReply.class);
        assertEquals(0, reply.result());
        assertNull(reply.status(), "status 省略时应为 null");
    }

    @Test
    @DisplayName("缺失 result 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"status\":\"ok\"}", DrcCameraPhotoTakeReply.class));
    }
}
