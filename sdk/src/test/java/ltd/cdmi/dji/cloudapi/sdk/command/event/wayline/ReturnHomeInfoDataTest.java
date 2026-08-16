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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ReturnHomeInfoData} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p>含跨包共享 record {@link PathPoint}（planned_path_points）与嵌套 MultiDockHomeInfo 列表。
 */
class ReturnHomeInfoDataTest {

    private static final String SAMPLE_JSON =
            "{\"planned_path_points\":[{\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0}],"
            + "\"last_point_type\":1,\"flight_id\":\"flight-001\",\"home_dock_sn\":\"dock-001\","
            + "\"multi_dock_home_info\":[{\"sn\":\"dock-002\",\"plan_status\":0,"
            + "\"estimated_battery_consumption\":30,\"home_distance\":120.5}]}";

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 planned_path_points + multi_dock_home_info 嵌套列表）")
    void testDeserialize() {
        ReturnHomeInfoData data = MessageCodec.fromJson(SAMPLE_JSON, ReturnHomeInfoData.class);
        assertEquals(1, data.lastPointType());
        assertEquals("flight-001", data.flightId());
        assertEquals("dock-001", data.homeDockSn());
        // planned_path_points
        assertNotNull(data.plannedPathPoints());
        assertEquals(1, data.plannedPathPoints().size());
        PathPoint point = data.plannedPathPoints().get(0);
        assertEquals(22.5, point.latitude());
        assertEquals(113.9, point.longitude());
        assertEquals(50.0, point.height());
        // multi_dock_home_info 嵌套列表
        assertNotNull(data.multiDockHomeInfo());
        assertEquals(1, data.multiDockHomeInfo().size());
        ReturnHomeInfoData.MultiDockHomeInfo info = data.multiDockHomeInfo().get(0);
        assertEquals("dock-002", info.sn());
        assertEquals(0, info.planStatus());
        assertEquals(30, info.estimatedBatteryConsumption());
        assertEquals(120.5, info.homeDistance());
    }

    @Test
    @DisplayName("反序列化：缺失可选字段 home_dock_sn/multi_dock_home_info（Dock1 场景）容错")
    void testDeserializeDock1Scenario() {
        String json = "{\"planned_path_points\":[{\"latitude\":22.5,\"longitude\":113.9,\"height\":50.0}],"
                + "\"last_point_type\":1,\"flight_id\":\"flight-001\"}";
        ReturnHomeInfoData data = MessageCodec.fromJson(json, ReturnHomeInfoData.class);
        assertEquals("flight-001", data.flightId());
        assertTrue(null == data.homeDockSn(), "Dock1 无 home_dock_sn 字段应为 null");
        assertTrue(null == data.multiDockHomeInfo(), "Dock1 无 multi_dock_home_info 字段应为 null");
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 home_dock_sn/multi_dock_home_info/estimated_battery_consumption）")
    void testSerialize() {
        PathPoint point = new PathPoint(22.5, 113.9, 50.0);
        ReturnHomeInfoData.MultiDockHomeInfo info = new ReturnHomeInfoData.MultiDockHomeInfo(
                "dock-002", 0, 30, 120.5);
        ReturnHomeInfoData data = new ReturnHomeInfoData(
                List.of(point), 1, "flight-001", "dock-001", List.of(info));
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"home_dock_sn\":\"dock-001\""), "JSON 应含 home_dock_sn，实际: " + json);
        assertTrue(json.contains("\"multi_dock_home_info\""), "JSON 应含 multi_dock_home_info，实际: " + json);
        assertTrue(json.contains("\"estimated_battery_consumption\":30"), "JSON 应含 estimated_battery_consumption，实际: " + json);
        assertTrue(json.contains("\"planned_path_points\""), "JSON 应含 planned_path_points，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        PathPoint point = new PathPoint(22.5, 113.9, 50.0);
        ReturnHomeInfoData.MultiDockHomeInfo info = new ReturnHomeInfoData.MultiDockHomeInfo(
                "dock-002", 0, 30, 120.5);
        ReturnHomeInfoData original = new ReturnHomeInfoData(
                List.of(point), 1, "flight-001", "dock-001", List.of(info));
        String json = MessageCodec.toJson(original);
        ReturnHomeInfoData back = MessageCodec.fromJson(json, ReturnHomeInfoData.class);
        assertEquals(original, back);
    }

    @Test
    @DisplayName("缺失必填字段 flight_id：反序列化抛 IllegalStateException")
    void testMissingFlightIdThrows() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"planned_path_points\":[],\"last_point_type\":1}",
                        ReturnHomeInfoData.class));
    }
}
