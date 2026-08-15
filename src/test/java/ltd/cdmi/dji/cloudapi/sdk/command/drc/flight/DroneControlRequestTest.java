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
 * 验证 {@link DroneControlRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code drone_control} 指令 data JSON（含 seq/x/y/h/w 五字段）
 *       能反序列化为 record</li>
 *   <li>缺失任一必填字段（seq/x/y/h/w）时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变（含 Double 字段精度）</li>
 * </ol>
 */
class DroneControlRequestTest {

    private static final String SAMPLE_JSON =
            "{\"seq\":1,\"x\":1.0,\"y\":0.0,\"h\":0.5,\"w\":-0.5}";

    @Test
    @DisplayName("反序列化：{\"seq\":1,\"x\":1.0,...} → seq/x/y/h/w 正确绑定")
    void testDeserialize() {
        DroneControlRequest req = MessageCodec.fromJson(SAMPLE_JSON, DroneControlRequest.class);
        assertEquals(1, req.seq());
        assertEquals(1.0, req.x());
        assertEquals(0.0, req.y());
        assertEquals(0.5, req.h());
        assertEquals(-0.5, req.w());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 seq/x/y/h/w 字段")
    void testSerialize() {
        DroneControlRequest req = new DroneControlRequest(1, 1.0, 0.0, 0.5, -0.5);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"seq\":1"), "JSON 应含 \"seq\":1，实际: " + json);
        assertTrue(json.contains("\"x\":1.0"), "JSON 应含 \"x\":1.0，实际: " + json);
        assertTrue(json.contains("\"y\":0.0"), "JSON 应含 \"y\":0.0，实际: " + json);
        assertTrue(json.contains("\"h\":0.5"), "JSON 应含 \"h\":0.5，实际: " + json);
        assertTrue(json.contains("\"w\":-0.5"), "JSON 应含 \"w\":-0.5，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        DroneControlRequest original = new DroneControlRequest(100, 2.5, -1.5, 3.0, 0.0);
        String json = MessageCodec.toJson(original);
        DroneControlRequest back = MessageCodec.fromJson(json, DroneControlRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 seq 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingSeqThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"x\":1.0,\"y\":0.0,\"h\":0.5,\"w\":-0.5}",
                        DroneControlRequest.class));
    }

    @Test
    @DisplayName("缺失 x 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingXThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"seq\":1,\"y\":0.0,\"h\":0.5,\"w\":-0.5}",
                        DroneControlRequest.class));
    }

    @Test
    @DisplayName("缺失 y 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingYThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"seq\":1,\"x\":1.0,\"h\":0.5,\"w\":-0.5}",
                        DroneControlRequest.class));
    }

    @Test
    @DisplayName("缺失 h 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingHThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"seq\":1,\"x\":1.0,\"y\":0.0,\"w\":-0.5}",
                        DroneControlRequest.class));
    }

    @Test
    @DisplayName("缺失 w 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingWThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"seq\":1,\"x\":1.0,\"y\":0.0,\"h\":0.5}",
                        DroneControlRequest.class));
    }
}
