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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link CameraPhotoTakeProgressData} 的 Jackson 反序列化、嵌套结构与类型化枚举绑定。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI events 事件 {@code camera_photo_take_progress} 的 data JSON（含 output.ext.camera_mode）
 *       能反序列化为 record，{@code camera_mode} int 值通过 {@link CameraMode} 的
 *       {@code @JsonCreator} 自动转为类型化枚举</li>
 *   <li>嵌套结构完整：data.output.status/progress.{current_step,percent}/ext.camera_mode 均正确绑定</li>
 *   <li>缺失 {@code result} 字段时，构造器 {@link Objects#requireNonNull} 抛出
 *       {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化→反序列化保持枚举值不变</li>
 * </ol>
 */
class CameraPhotoTakeProgressDataTest {

    /** DJI camera_photo_take_progress 事件 data JSON 样本（snake_case，含完整嵌套结构）。 */
    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"status\":\"in_progress\","
            + "\"progress\":{\"current_step\":1,\"percent\":50},"
            + "\"ext\":{\"camera_mode\":3}}}";

    @Test
    @DisplayName("Jackson 反序列化：完整 JSON → 嵌套结构 + ext.cameraMode=PANORAMA")
    void testDeserializeFullStructureWithCameraMode() {
        CameraPhotoTakeProgressData data = MessageCodec.fromJson(SAMPLE_JSON, CameraPhotoTakeProgressData.class);
        // result 字段
        assertEquals(0, data.result());
        // output.status
        assertEquals("in_progress", data.output().status());
        // output.progress.current_step / percent
        assertEquals(1, data.output().progress().currentStep());
        assertEquals(50, data.output().progress().percent());
        // output.ext.camera_mode → CameraMode.PANORAMA（类型化枚举）
        assertEquals(CameraMode.PANORAMA, data.output().ext().cameraMode());
        assertEquals(3, data.output().ext().cameraMode().code());
    }

    @Test
    @DisplayName("Jackson 反序列化：4 个 camera_mode 值全覆盖（output.ext.camera_mode）")
    void testDeserializeAllCameraModeValuesInExt() {
        for (CameraMode mode : CameraMode.values()) {
            String json = "{\"result\":0,\"output\":{\"ext\":{\"camera_mode\":" + mode.code() + "}}}";
            CameraPhotoTakeProgressData data = MessageCodec.fromJson(json, CameraPhotoTakeProgressData.class);
            assertEquals(mode, data.output().ext().cameraMode(),
                    "camera_mode=" + mode.code() + " 应反序列化为 " + mode);
        }
    }

    @Test
    @DisplayName("Jackson 序列化：data(output.ext.cameraMode=PANORAMA) → JSON 含 \"camera_mode\":3")
    void testSerializeProducesIntCode() {
        CameraPhotoTakeProgressData.Ext ext = new CameraPhotoTakeProgressData.Ext(CameraMode.PANORAMA);
        CameraPhotoTakeProgressData.Output output = new CameraPhotoTakeProgressData.Output(
                "in_progress",
                new CameraPhotoTakeProgressData.Progress(1, 50),
                ext);
        CameraPhotoTakeProgressData data = new CameraPhotoTakeProgressData(0, output);
        String json = MessageCodec.toJson(data);
        // @JsonValue 使 PANORAMA 序列化为 3
        assertTrue(json.contains("\"camera_mode\":3"), "JSON 应含 \"camera_mode\":3，实际: " + json);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 \"result\":0，实际: " + json);
        assertTrue(json.contains("\"current_step\":1"), "JSON 应含 \"current_step\":1，实际: " + json);
        assertTrue(json.contains("\"percent\":50"), "JSON 应含 \"percent\":50，实际: " + json);
    }

    @Test
    @DisplayName("Jackson 双向闭环：序列化 → 反序列化保持枚举值不变（4 个 CameraMode 全覆盖）")
    void testRoundTripPreservesEnum() {
        for (CameraMode original : CameraMode.values()) {
            CameraPhotoTakeProgressData.Ext ext = new CameraPhotoTakeProgressData.Ext(original);
            CameraPhotoTakeProgressData.Output output = new CameraPhotoTakeProgressData.Output(
                    "in_progress",
                    new CameraPhotoTakeProgressData.Progress(1, 50),
                    ext);
            CameraPhotoTakeProgressData out = new CameraPhotoTakeProgressData(0, output);
            String json = MessageCodec.toJson(out);
            CameraPhotoTakeProgressData back = MessageCodec.fromJson(json, CameraPhotoTakeProgressData.class);
            assertEquals(original, back.output().ext().cameraMode(),
                    "Round-trip 失败: " + original + " → JSON " + json + " → " + back.output().ext().cameraMode());
        }
    }

    @Test
    @DisplayName("缺失 result 字段：Jackson 反序列化后构造器抛 NullPointerException")
    void testMissingResultThrowsNpe() {
        // JSON 缺失 result → Jackson 将 result 设为 null → 构造器 Objects.requireNonNull 抛 NPE
        // MessageCodec.fromJson 将构造异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"output\":{\"ext\":{\"camera_mode\":3}}}",
                        CameraPhotoTakeProgressData.class));
    }

    @Test
    @DisplayName("非法 camera_mode int 值（-1, 4）反序列化抛异常：未知枚举值不允许绑定")
    void testDeserializeInvalidCameraModeThrows() {
        // M30 文档的 -1 与越界 4 均应通过 @JsonCreator 调用 fromCode 抛 IllegalArgumentException
        // MessageCodec.fromJson 将 Jackson 异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"result\":0,\"output\":{\"ext\":{\"camera_mode\":-1}}}",
                        CameraPhotoTakeProgressData.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"result\":0,\"output\":{\"ext\":{\"camera_mode\":4}}}",
                        CameraPhotoTakeProgressData.class));
    }

    @Test
    @DisplayName("省略 output.progress 字段：progress() 为 null（非必填，无 requireNonNull 验证）")
    void testOptionalProgressField() {
        // progress 字段无 Objects.requireNonNull 验证，可省略
        String json = "{\"result\":0,\"output\":{\"status\":\"ok\",\"ext\":{\"camera_mode\":0}}}";
        CameraPhotoTakeProgressData data = MessageCodec.fromJson(json, CameraPhotoTakeProgressData.class);
        assertEquals("ok", data.output().status());
        assertEquals(CameraMode.PHOTO, data.output().ext().cameraMode());
        // progress 未提供 → null（不抛异常）
        assertTrue(null == data.output().progress(), "progress 字段省略时应为 null");
    }
}
