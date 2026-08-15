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

package ltd.cdmi.dji.cloudapi.sdk.command.service.camera;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link CameraModeSwitchRequest} 的 Jackson 反序列化、必填字段校验与类型化枚举绑定。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI services 指令 {@code camera_mode_switch} 的 data JSON（含 payload_index + camera_mode）
 *       能反序列化为 record，{@code camera_mode} int 值通过 {@link CameraMode} 的
 *       {@code @JsonCreator} 自动转为类型化枚举</li>
 *   <li>缺失 {@code camera_mode} 或 {@code payload_index} 字段时，构造器
 *       {@link Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>序列化产出 DJI 协议期望的 int 值（通过 {@code @JsonValue}），与原始 Integer 字段格式一致</li>
 *   <li>Jackson 双向闭环：所有 4 个 CameraMode 值序列化→反序列化保持不变</li>
 * </ol>
 */
class CameraModeSwitchRequestTest {

    private static final String PAYLOAD_INDEX = "165-0-7";

    @Test
    @DisplayName("Jackson 反序列化：{\"payload_index\":\"165-0-7\",\"camera_mode\":3} → cameraMode()=PANORAMA")
    void testDeserializeCameraMode3ToPanorama() {
        CameraModeSwitchRequest req = MessageCodec.fromJson(
                "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"camera_mode\":3}",
                CameraModeSwitchRequest.class);
        assertEquals(PAYLOAD_INDEX, req.payloadIndex());
        assertEquals(CameraMode.PANORAMA, req.cameraMode());
        assertEquals(3, req.cameraMode().code());
    }

    @Test
    @DisplayName("Jackson 反序列化：4 个 camera_mode 值全覆盖（0=PHOTO, 1=VIDEO, 2=HYPER_LIGHT, 3=PANORAMA）")
    void testDeserializeAllCameraModeValues() {
        for (CameraMode mode : CameraMode.values()) {
            CameraModeSwitchRequest req = MessageCodec.fromJson(
                    "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"camera_mode\":" + mode.code() + "}",
                    CameraModeSwitchRequest.class);
            assertEquals(mode, req.cameraMode(),
                    "camera_mode=" + mode.code() + " 应反序列化为 " + mode);
        }
    }

    @Test
    @DisplayName("Jackson 序列化：CameraModeSwitchRequest(PANORAMA) → JSON 含 \"camera_mode\":3 与 \"payload_index\"")
    void testSerializeProducesIntCode() {
        CameraModeSwitchRequest req = new CameraModeSwitchRequest(PAYLOAD_INDEX, CameraMode.PANORAMA);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"camera_mode\":3"), "JSON 应含 \"camera_mode\":3，实际: " + json);
        assertTrue(json.contains("\"payload_index\":\"" + PAYLOAD_INDEX + "\""),
                "JSON 应含 payload_index，实际: " + json);
    }

    @Test
    @DisplayName("Jackson 双向闭环：4 个 CameraMode 值序列化→反序列化保持不变")
    void testRoundTripPreservesEnum() {
        for (CameraMode original : CameraMode.values()) {
            CameraModeSwitchRequest out = new CameraModeSwitchRequest(PAYLOAD_INDEX, original);
            String json = MessageCodec.toJson(out);
            CameraModeSwitchRequest back = MessageCodec.fromJson(json, CameraModeSwitchRequest.class);
            assertEquals(original, back.cameraMode(),
                    "Round-trip 失败: " + original + " → JSON " + json + " → " + back.cameraMode());
            assertEquals(PAYLOAD_INDEX, back.payloadIndex());
        }
    }

    @Test
    @DisplayName("缺失 camera_mode 字段：Jackson 反序列化后构造器抛 NullPointerException")
    void testMissingCameraModeThrowsNpe() {
        // JSON 缺失 camera_mode → Jackson 将 cameraMode 设为 null → 构造器 Objects.requireNonNull 抛 NPE
        // MessageCodec.fromJson 将构造异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"payload_index\":\"" + PAYLOAD_INDEX + "\"}",
                        CameraModeSwitchRequest.class));
    }

    @Test
    @DisplayName("缺失 payload_index 字段：Jackson 反序列化后构造器抛 NullPointerException")
    void testMissingPayloadIndexThrowsNpe() {
        // JSON 缺失 payload_index → Jackson 将 payloadIndex 设为 null → 构造器 Objects.requireNonNull 抛 NPE
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"camera_mode\":3}", CameraModeSwitchRequest.class));
    }

    @Test
    @DisplayName("非法 camera_mode int 值（-1, 4）反序列化抛异常：未知枚举值不允许绑定")
    void testDeserializeInvalidCameraModeThrows() {
        // M30 文档的 -1 与越界 4 均应通过 @JsonCreator 调用 fromCode 抛 IllegalArgumentException
        // MessageCodec.fromJson 将 Jackson 异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"camera_mode\":-1}",
                        CameraModeSwitchRequest.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"payload_index\":\"" + PAYLOAD_INDEX + "\",\"camera_mode\":4}",
                        CameraModeSwitchRequest.class));
    }
}
