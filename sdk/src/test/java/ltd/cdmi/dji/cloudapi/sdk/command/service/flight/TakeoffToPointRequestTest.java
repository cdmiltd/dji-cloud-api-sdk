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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flight;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link TakeoffToPointRequest} 的协议定义完整性。
 *
 * <p>覆盖维度（对应 tdd-test-cases.md §11.9）：
 * <ol>
 *   <li>record 组件数量与名称</li>
 *   <li>snake_case 双向序列化（反序列化 + 序列化）</li>
 *   <li>类级 {@link Verified} 与字段级 {@link Inferred} 注解元数据</li>
 *   <li>必填字段缺席时 compact constructor 抛 {@link NullPointerException}</li>
 * </ol>
 *
 * <p>注：{@link Inferred} 的 {@code @Target} 含 {@code FIELD} 但不含
 * {@code RECORD_COMPONENT}，因此通过 {@link Field#getAnnotation(Class)} 获取
 * （record component 注解传播到 field），而非 {@code RecordComponent.getAnnotation()}。
 */
class TakeoffToPointRequestTest {

    // ==================== 组件结构 ====================

    @Test
    @DisplayName("record 应有 14 个组件，含 flightId/targetLatitude/simulateMission 等")
    void shouldHave14Components_whenRecord() {
        RecordComponent[] components = TakeoffToPointRequest.class.getRecordComponents();
        assertEquals(14, components.length, "组件数量应为 14");
        List<String> names = Arrays.stream(components).map(RecordComponent::getName).toList();
        assertTrue(names.contains("flightId"), "应含 flightId");
        assertTrue(names.contains("targetLatitude"), "应含 targetLatitude");
        assertTrue(names.contains("simulateMission"), "应含 simulateMission");
    }

    // ==================== 序列化 ====================

    @Test
    @DisplayName("snake_case JSON 反序列化为 record，字段值正确映射")
    void shouldDeserializeSnakeCase_whenJsonGiven() {
        String json = "{\"flight_id\":\"f1\",\"max_speed\":10,"
                + "\"target_latitude\":22.0,\"target_longitude\":113.0,\"target_height\":50.0,"
                + "\"security_takeoff_height\":30.0}";
        TakeoffToPointRequest req = MessageCodec.fromJson(json, TakeoffToPointRequest.class);
        assertEquals("f1", req.flightId());
        assertEquals(10, req.maxSpeed());
        assertEquals(22.0, req.targetLatitude());
        assertEquals(30.0, req.securityTakeoffHeight());
    }

    @Test
    @DisplayName("record 序列化为 snake_case JSON，含 flight_id/max_speed/security_takeoff_height")
    void shouldSerializeToSnakeCase_whenRecordGiven() {
        TakeoffToPointRequest req = new TakeoffToPointRequest(
                "f1", 10, 22.0, 113.0, 50.0, 30.0,
                null, null, null, null, null, null, null, null);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"flight_id\":\"f1\""), "JSON 应含 flight_id");
        assertTrue(json.contains("\"max_speed\":10"), "JSON 应含 max_speed");
        assertTrue(json.contains("\"target_latitude\":22.0"), "JSON 应含 target_latitude");
        assertTrue(json.contains("\"security_takeoff_height\":30.0"), "JSON 应含 security_takeoff_height");
    }

    // ==================== 注解元数据 ====================

    @Test
    @DisplayName("类级 @Verified，basis 含 FlightCommandSimulator")
    void shouldBeAnnotatedWithVerified_whenClassLevel() {
        Verified v = TakeoffToPointRequest.class.getAnnotation(Verified.class);
        assertNotNull(v, "类必须标注 @Verified");
        assertTrue(v.basis().contains("FlightCommandSimulator"), "basis 应含 FlightCommandSimulator");
    }

    @Test
    @DisplayName("flightSafetyAdvanceCheck 标注 @Inferred，reason 含布尔开关，verifyPoint 含真机")
    void shouldBeAnnotatedWithInferred_whenFlightSafetyAdvanceCheckField() throws NoSuchFieldException {
        Field field = TakeoffToPointRequest.class.getDeclaredField("flightSafetyAdvanceCheck");
        Inferred inf = field.getAnnotation(Inferred.class);
        assertNotNull(inf, "flightSafetyAdvanceCheck 必须标注 @Inferred");
        assertTrue(inf.reason().contains("布尔开关"), "reason 应含「布尔开关」");
        assertTrue(inf.verifyPoint().contains("真机"), "verifyPoint 应含「真机」");
    }

    // ==================== 必填字段校验 ====================

    @Test
    @DisplayName("缺 flight_id 时反序列化抛 IllegalStateException（包装 NPE），message 含 flightId 必填")
    void shouldThrowWhenRequiredFieldMissing_whenFlightIdNull() {
        String json = "{\"max_speed\":10,\"target_latitude\":22.0,"
                + "\"target_longitude\":113.0,\"target_height\":50.0}";
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(json, TakeoffToPointRequest.class));
        assertTrue(ex.getMessage().contains("flightId 必填"),
                "异常消息应含「flightId 必填」，实际: " + ex.getMessage());
    }

    @Test
    @DisplayName("缺 target_latitude 时反序列化抛 IllegalStateException（包装 NPE），message 含 targetLatitude 必填")
    void shouldThrowWhenRequiredFieldMissing_whenTargetLatitudeNull() {
        String json = "{\"flight_id\":\"f1\",\"target_longitude\":113.0,\"target_height\":50.0}";
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(json, TakeoffToPointRequest.class));
        assertTrue(ex.getMessage().contains("targetLatitude 必填"),
                "异常消息应含「targetLatitude 必填」，实际: " + ex.getMessage());
    }
}
