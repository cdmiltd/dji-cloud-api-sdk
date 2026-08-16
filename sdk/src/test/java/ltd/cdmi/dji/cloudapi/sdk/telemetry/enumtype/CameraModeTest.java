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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import ltd.cdmi.dji.cloudapi.sdk.command.service.camera.CameraModeSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link CameraMode} 枚举的 code 反查、描述准确性与 Jackson 双向绑定。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI M3D properties 文档定义的 4 个 camera_mode 数值（0=拍照, 1=录像, 2=智能低光, 3=全景拍照）
 *       能通过 {@link CameraMode#fromCode(int)} 反查到枚举常量</li>
 *   <li>-1（M30 文档「不支持的模式」）/ 4 / 255 等越界值均抛出 {@link IllegalArgumentException}
 *       （M3D 文档为准，-1 不混入正常值域）</li>
 *   <li>Jackson 通过 {@link com.fasterxml.jackson.annotation.JsonValue} /
 *       {@link com.fasterxml.jackson.annotation.JsonCreator} 实现 int↔enum 双向绑定，
 *       POJO 字段可直接用类型化枚举替代原始 int（见 {@link CameraModeSwitchRequest}）</li>
 * </ol>
 */
class CameraModeTest {

    @Test
    @DisplayName("fromCode 反查 DJI M3D 文档定义的 4 个数值（0, 1, 2, 3）")
    void testFromCodeAllValues() {
        assertEquals(CameraMode.PHOTO, CameraMode.fromCode(0));
        assertEquals(CameraMode.VIDEO, CameraMode.fromCode(1));
        assertEquals(CameraMode.HYPER_LIGHT, CameraMode.fromCode(2));
        assertEquals(CameraMode.PANORAMA, CameraMode.fromCode(3));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含 M30 文档的 -1，M3D 文档为准不纳入）")
    void testFromCodeUnknownThrows() {
        // M30 文档额外含 -1=不支持的模式，本枚举以 M3D 文档为准，-1 视为越界
        assertThrows(IllegalArgumentException.class, () -> CameraMode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> CameraMode.fromCode(4));
        assertThrows(IllegalArgumentException.class, () -> CameraMode.fromCode(5));
        assertThrows(IllegalArgumentException.class, () -> CameraMode.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 4（拍照 + 录像 + 智能低光 + 全景拍照）")
    void testTotalCount() {
        assertEquals(4, CameraMode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：0→PHOTO, 1→VIDEO, 2→HYPER_LIGHT, 3→PANORAMA")
    void testCodeRoundTrip() {
        assertEquals(0, CameraMode.PHOTO.code());
        assertEquals(1, CameraMode.VIDEO.code());
        assertEquals(2, CameraMode.HYPER_LIGHT.code());
        assertEquals(3, CameraMode.PANORAMA.code());
        // 双向闭环
        for (CameraMode mode : CameraMode.values()) {
            assertEquals(mode, CameraMode.fromCode(mode.code()));
        }
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("拍照", CameraMode.PHOTO.description());
        assertEquals("录像", CameraMode.VIDEO.description());
        assertEquals("智能低光", CameraMode.HYPER_LIGHT.description());
        assertEquals("全景拍照", CameraMode.PANORAMA.description());
        for (CameraMode mode : CameraMode.values()) {
            assertTrue(!mode.description().isBlank());
        }
    }

    // ==================== Jackson 双向绑定（@JsonValue + @JsonCreator）====================

    @Test
    @DisplayName("Jackson 序列化：CameraModeSwitchRequest(PANORAMA) → JSON 含 \"camera_mode\":3")
    void testJacksonSerializeEnumAsIntCode() {
        CameraModeSwitchRequest req = new CameraModeSwitchRequest("165-0-7", CameraMode.PANORAMA);
        String json = MessageCodec.toJson(req);
        // @JsonValue 使 PANORAMA 序列化为 3（int 而非枚举名 "PANORAMA"）
        assertTrue(json.contains("\"camera_mode\":3"), "JSON 应包含 \"camera_mode\":3，实际: " + json);
    }

    @Test
    @DisplayName("Jackson 反序列化：JSON {\"camera_mode\":3} → CameraModeSwitchRequest.cameraMode()=PANORAMA")
    void testJacksonDeserializeIntCodeToEnum() {
        CameraModeSwitchRequest req =
                MessageCodec.fromJson("{\"payload_index\":\"165-0-7\",\"camera_mode\":3}", CameraModeSwitchRequest.class);
        // @JsonCreator 使 int 3 反序列化为 PANORAMA（而非用枚举名匹配）
        assertEquals(CameraMode.PANORAMA, req.cameraMode());
        assertEquals(3, req.cameraMode().code());
    }

    @Test
    @DisplayName("Jackson 双向闭环：序列化 → 反序列化保持枚举值不变（4 个值全覆盖）")
    void testJacksonRoundTripPreservesEnum() {
        for (CameraMode original : CameraMode.values()) {
            CameraModeSwitchRequest out = new CameraModeSwitchRequest("165-0-7", original);
            String json = MessageCodec.toJson(out);
            CameraModeSwitchRequest back =
                    MessageCodec.fromJson(json, CameraModeSwitchRequest.class);
            assertEquals(original, back.cameraMode(),
                    "Round-trip 失败: " + original + " → JSON " + json + " → " + back.cameraMode());
        }
    }

    @Test
    @DisplayName("Jackson 反序列化非法 int（-1, 4）抛异常：未知枚举值不允许绑定")
    void testJacksonDeserializeInvalidIntThrows() {
        // M30 的 -1 与越界 4 均应通过 @JsonCreator 调用 fromCode 抛 IllegalArgumentException
        // MessageCodec.fromJson 将 Jackson 异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"payload_index\":\"165-0-7\",\"camera_mode\":-1}", CameraModeSwitchRequest.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"payload_index\":\"165-0-7\",\"camera_mode\":4}", CameraModeSwitchRequest.class));
    }
}
