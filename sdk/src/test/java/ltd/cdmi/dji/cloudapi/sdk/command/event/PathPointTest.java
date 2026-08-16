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

package ltd.cdmi.dji.cloudapi.sdk.command.event;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PathPoint} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p>PathPoint 为 events 通道共享 record，被 FlyToPointProgressData、TakeoffToPointProgressData、
 * ReturnHomeInfoData 的 planned_path_points 数组共享使用。
 */
class PathPointTest {

    @Test
    @DisplayName("反序列化：latitude/longitude/height 同名映射 → record")
    void testDeserialize() {
        String json = "{\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0}";
        PathPoint point = MessageCodec.fromJson(json, PathPoint.class);
        assertEquals(22.5, point.latitude());
        assertEquals(113.9, point.longitude());
        assertEquals(50.0, point.height());
    }

    @Test
    @DisplayName("序列化：record → JSON 含 latitude/longitude/height")
    void testSerialize() {
        PathPoint point = new PathPoint(22.5, 113.9, 50.0);
        String json = MessageCodec.toJson(point);
        assertTrue(json.contains("\"latitude\":22.5"), "JSON 应含 latitude，实际: " + json);
        assertTrue(json.contains("\"longitude\":113.9"), "JSON 应含 longitude，实际: " + json);
        assertTrue(json.contains("\"height\":50.0"), "JSON 应含 height，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PathPoint original = new PathPoint(22.5, 113.9, 50.0);
        String json = MessageCodec.toJson(original);
        PathPoint back = MessageCodec.fromJson(json, PathPoint.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 latitude：反序列化抛 IllegalStateException")
    void testMissingLatitudeThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"longitude\":113.9,\"height\":50.0}", PathPoint.class));
    }

    @Test
    @DisplayName("缺失必填字段 height：反序列化抛 IllegalStateException")
    void testMissingHeightThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"latitude\":22.5,\"longitude\":113.9}", PathPoint.class));
    }
}
