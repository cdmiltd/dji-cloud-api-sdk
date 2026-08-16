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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.flight;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link StickControlRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code stick_control} 指令 data JSON（含 roll/pitch/throttle/yaw 四通道）
 *       能反序列化为 record</li>
 *   <li>缺失任一必填字段（roll/pitch/throttle/yaw）时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class StickControlRequestTest {

    private static final String SAMPLE_JSON =
            "{\"roll\":100,\"pitch\":-100,\"throttle\":0,\"yaw\":500}";

    @Test
    @DisplayName("反序列化：{\"roll\":100,\"pitch\":-100,...} → 四通道值正确绑定")
    void testDeserialize() {
        StickControlRequest req = MessageCodec.fromJson(SAMPLE_JSON, StickControlRequest.class);
        assertEquals(100, req.roll());
        assertEquals(-100, req.pitch());
        assertEquals(0, req.throttle());
        assertEquals(500, req.yaw());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 roll/pitch/throttle/yaw 字段")
    void testSerialize() {
        StickControlRequest req = new StickControlRequest(100, -100, 0, 500);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"roll\":100"), "JSON 应含 \"roll\":100，实际: " + json);
        assertTrue(json.contains("\"pitch\":-100"), "JSON 应含 \"pitch\":-100，实际: " + json);
        assertTrue(json.contains("\"throttle\":0"), "JSON 应含 \"throttle\":0，实际: " + json);
        assertTrue(json.contains("\"yaw\":500"), "JSON 应含 \"yaw\":500，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        StickControlRequest original = new StickControlRequest(660, -330, 100, 0);
        String json = MessageCodec.toJson(original);
        StickControlRequest back = MessageCodec.fromJson(json, StickControlRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 roll 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingRollThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"pitch\":-100,\"throttle\":0,\"yaw\":500}",
                        StickControlRequest.class));
    }

    @Test
    @DisplayName("缺失 pitch 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingPitchThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"roll\":100,\"throttle\":0,\"yaw\":500}",
                        StickControlRequest.class));
    }

    @Test
    @DisplayName("缺失 throttle 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingThrottleThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"roll\":100,\"pitch\":-100,\"yaw\":500}",
                        StickControlRequest.class));
    }

    @Test
    @DisplayName("缺失 yaw 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingYawThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"roll\":100,\"pitch\":-100,\"throttle\":0}",
                        StickControlRequest.class));
    }
}
