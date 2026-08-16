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
 * 验证 {@link HeartBeatRequest} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DRC {@code heart_beat} 请求 data JSON（含 timestamp）
 *       能反序列化为 record</li>
 *   <li>缺失 {@code timestamp} 字段时，构造器
 *       {@link java.util.Objects#requireNonNull} 抛出 {@link NullPointerException}（保护必填字段）</li>
 *   <li>Jackson 双向闭环：序列化 → 反序列化保持不变</li>
 * </ol>
 */
class HeartBeatRequestTest {

    private static final long TIMESTAMP = 1700000000000L;

    @Test
    @DisplayName("反序列化：{\"timestamp\":1700000000000} → timestamp 正确绑定")
    void testDeserialize() {
        String json = "{\"timestamp\":" + TIMESTAMP + "}";
        HeartBeatRequest req = MessageCodec.fromJson(json, HeartBeatRequest.class);
        assertEquals(TIMESTAMP, req.timestamp());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 \"timestamp\" 字段")
    void testSerialize() {
        HeartBeatRequest req = new HeartBeatRequest(TIMESTAMP);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"timestamp\":" + TIMESTAMP),
                "JSON 应含 \"timestamp\":" + TIMESTAMP + "，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化 → 反序列化保持不变")
    void testRoundTrip() {
        HeartBeatRequest original = new HeartBeatRequest(TIMESTAMP);
        String json = MessageCodec.toJson(original);
        HeartBeatRequest back = MessageCodec.fromJson(json, HeartBeatRequest.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失 timestamp 字段：Jackson 反序列化后构造器抛 NullPointerException（被包装为 IllegalStateException）")
    void testMissingTimestampThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", HeartBeatRequest.class));
    }
}
