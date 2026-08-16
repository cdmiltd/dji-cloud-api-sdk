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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.light;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DrcLightFineTuningSetRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drc_light_fine_tuning_set} 指令 data JSON（含 psdk_index + position + value）
 *       能反序列化为 record，snake_case {@code psdk_index} 自动映射到 camelCase {@code psdkIndex}</li>
 *   <li>缺失任一必填字段（psdk_index/position/value）时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class DrcLightFineTuningSetRequestTest {

    private static final int PSDK_INDEX = 1;

    @Test
    @DisplayName("反序列化：{\"psdk_index\":1,\"position\":0,\"value\":2} → 三字段正确绑定")
    void testDeserialize() {
        String json = "{\"psdk_index\":" + PSDK_INDEX + ",\"position\":0,\"value\":2}";
        DrcLightFineTuningSetRequest req = MessageCodec.fromJson(json, DrcLightFineTuningSetRequest.class);
        assertEquals(PSDK_INDEX, req.psdkIndex());
        assertEquals(0, req.position());
        assertEquals(2, req.value());
    }

    @Test
    @DisplayName("反序列化：负微调角度 value=-3 → 负值正确绑定")
    void testDeserializeNegativeValue() {
        String json = "{\"psdk_index\":" + PSDK_INDEX + ",\"position\":1,\"value\":-3}";
        DrcLightFineTuningSetRequest req = MessageCodec.fromJson(json, DrcLightFineTuningSetRequest.class);
        assertEquals(1, req.position());
        assertEquals(-3, req.value());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"psdk_index\" / \"position\" / \"value\" snake_case 字段")
    void testSerialize() {
        DrcLightFineTuningSetRequest req = new DrcLightFineTuningSetRequest(PSDK_INDEX, 0, 2);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"psdk_index\":" + PSDK_INDEX),
                "JSON 应含 psdk_index，实际: " + json);
        assertTrue(json.contains("\"position\":0"), "JSON 应含 \"position\":0，实际: " + json);
        assertTrue(json.contains("\"value\":2"), "JSON 应含 \"value\":2，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DrcLightFineTuningSetRequest original = new DrcLightFineTuningSetRequest(PSDK_INDEX, 1, -3);
        String json = MessageCodec.toJson(original);
        DrcLightFineTuningSetRequest back = MessageCodec.fromJson(json, DrcLightFineTuningSetRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 psdk_index 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPsdkIndexThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"position\":0,\"value\":2}",
                        DrcLightFineTuningSetRequest.class));
    }

    @Test
    @DisplayName("缺失 position 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPositionThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"psdk_index\":" + PSDK_INDEX + ",\"value\":2}",
                        DrcLightFineTuningSetRequest.class));
    }

    @Test
    @DisplayName("缺失 value 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingValueThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"psdk_index\":" + PSDK_INDEX + ",\"position\":0}",
                        DrcLightFineTuningSetRequest.class));
    }
}
