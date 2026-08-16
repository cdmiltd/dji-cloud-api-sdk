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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DroneOsd} record 序列化测试。
 *
 * <p>验证两项关键能力：
 * <ol>
 *   <li><b>NON_NULL 序列化</b>：部分字段为 null 时不输出，支持「分多条推送」场景</li>
 *   <li><b>extras 动态 key 展开</b>：{@code @JsonAnyGetter} 将 extras Map 展开到 JSON 顶层，
 *       用于 M30 负载索引（如 {@code "52-0-0"}）等动态 key 字段</li>
 * </ol>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/properties.html">
 * DJI 飞行器设备属性推送</a>
 */
class DroneOsdTest {

    /**
     * 构造只有前 3 个核心字段 + extras 有值的 DroneOsd，其余字段为 null。
     * <p>DroneOsd 共 36 个字段，此方法简化测试构造，避免 36 个参数的重复。
     *
     * @param modeCode 模式码
     * @param latitude 纬度
     * @param longitude 经度
     * @param extras   动态 key 容器（可为 null）
     * @return 部分字段填充的 DroneOsd
     */
    private static DroneOsd minimalOsd(Integer modeCode, Double latitude, Double longitude,
                                        Map<String, Object> extras) {
        return new DroneOsd(
                modeCode,          // 1. modeCode
                latitude,          // 2. latitude
                longitude,         // 3. longitude
                null,              // 4. height
                null,              // 5. elevation
                null,              // 6. attitudePitch
                null,              // 7. attitudeRoll
                null,              // 8. attitudeHead
                null,              // 9. horizontalSpeed
                null,              // 10. verticalSpeed
                null,              // 11. windSpeed
                null,              // 12. windDirection
                null,              // 13. ridState
                null,              // 14. rcLostAction
                null,              // 15. cameras
                null,              // 16. battery
                null,              // 17. positionState
                null,              // 18. totalFlightTime
                null,              // 19. activationTime
                null,              // 20. firmwareVersion
                null,              // 21. gear
                null,              // 22. heightLimit
                null,              // 23. homeDistance
                null,              // 24. distanceLimitStatus
                null,              // 25. rthAltitude
                null,              // 26. isNearAreaLimit
                null,              // 27. isNearHeightLimit
                null,              // 28. maintainStatus
                null,              // 29. nightLightsState
                null,              // 30. obstacleAvoidance
                null,              // 31. storage
                null,              // 32. totalFlightDistance
                null,              // 33. totalFlightSorties
                null,              // 34. trackId
                null,              // 35. country
                extras             // 36. extras
        );
    }

    /**
     * 验证 NON_NULL 配置：null 字段不输出。
     *
     * <p>Given: DroneOsd 只填 modeCode/latitude/longitude，其余字段为 null
     * When:  序列化为 JSON
     * Then:  JSON 仅含非 null 字段，不含 "height":null 等多余字段
     */
    @Test
    @DisplayName("NON_NULL：null 字段不序列化，支持部分字段构造")
    void testNonNullSerializationExcludesNullFields() {
        DroneOsd osd = minimalOsd(0, 22.5, 113.9, null);

        String json = MessageCodec.toJson(osd);

        assertTrue(json.contains("\"mode_code\":0"), "非 null 字段 mode_code 应输出");
        assertTrue(json.contains("\"latitude\":22.5"), "非 null 字段 latitude 应输出");
        assertTrue(json.contains("\"longitude\":113.9"), "非 null 字段 longitude 应输出");
        assertFalse(json.contains("\"height\""), "null 字段 height 不应输出");
        assertFalse(json.contains("\"elevation\""), "null 字段 elevation 不应输出");
        assertFalse(json.contains("\"extras\""), "extras 为 null 时不应输出 extras 键");
    }

    /**
     * 验证 extras 动态 key 展开到 JSON 顶层。
     *
     * <p>Given: DroneOsd 含 extras = {"52-0-0": {"gimbal_pitch": 0.0}}
     * When:  序列化为 JSON
     * Then:  JSON 顶层含 "52-0-0" 键（展开自 extras），且不包裹在 "extras" 键下
     *
     * <p>这模拟 M30 旧版协议用负载索引作为顶层 key 上报负载属性的场景。
     */
    @Test
    @DisplayName("extras @JsonAnyGetter：动态 key 展开到 JSON 顶层")
    void testExtrasExpandedToJsonTopLevel() {
        Map<String, Object> payload = Map.of("gimbal_pitch", 0.0, "zoom_factor", 2.0);
        Map<String, Object> extras = Map.of("52-0-0", payload);

        DroneOsd osd = minimalOsd(0, 22.5, 113.9, extras);

        String json = MessageCodec.toJson(osd);

        // 固定字段正常输出
        assertTrue(json.contains("\"mode_code\":0"), "固定字段 mode_code 应输出");
        // extras 展开到顶层
        assertTrue(json.contains("\"52-0-0\""), "extras 中的动态 key 52-0-0 应展开到 JSON 顶层");
        assertTrue(json.contains("\"gimbal_pitch\":0.0"), "负载属性 gimbal_pitch 应在 52-0-0 下输出");
        assertTrue(json.contains("\"zoom_factor\":2.0"), "负载属性 zoom_factor 应在 52-0-0 下输出");
        // extras 不应作为独立键出现
        assertFalse(json.contains("\"extras\""), "extras 不应作为包裹键出现，应展开到顶层");
    }

    /**
     * 验证固定字段与 extras 动态 key 共存于同一 JSON 对象。
     *
     * <p>Given: DroneOsd 同时含固定字段和 extras
     * When:  序列化为 JSON
     * Then:  JSON 同时含固定字段和动态 key，处于同一层级
     */
    @Test
    @DisplayName("固定字段与 extras 动态 key 合并输出到同一 JSON 层级")
    void testFixedFieldsAndExtrasCoexist() {
        Map<String, Object> extras = Map.of("42-0-0", Map.of("gimbal_yaw", 10.0));

        DroneOsd osd = minimalOsd(1, 30.0, 120.0, extras);

        String json = MessageCodec.toJson(osd);

        // 固定字段
        assertTrue(json.contains("\"mode_code\":1"), "固定字段 mode_code 应输出");
        // 动态 key
        assertTrue(json.contains("\"42-0-0\""), "动态 key 42-0-0 应展开到顶层");
        // 两者在同一层级（不是嵌套关系）
        assertFalse(json.contains("\"extras\""), "extras 不应作为包裹键");
    }
}
