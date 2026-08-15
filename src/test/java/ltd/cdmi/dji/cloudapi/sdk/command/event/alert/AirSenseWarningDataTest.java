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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link AirSenseWarningData} 单元测试。
 *
 * <p>覆盖 DJI {@code airsense_warning} 事件的特殊 bare-array 结构反序列化：
 * data 直接是 JSON 数组（非对象包裹），通过 {@link AirSenseWarningDataDeserializer}
 * 自定义反序列化器包裹为 record。
 *
 * <p><b>修复历史</b>：最初用 {@code @JsonCreator(DELEGATING)} 静态工厂，但 Jackson 2.17 + record
 * 下不生效（Jackson 优先用 canonical constructor，遇到 array token 即报 MismatchedInputException）。
 * 改用 {@code @JsonDeserialize(using = AirSenseWarningDataDeserializer.class)} 后，
 * {@code mapper.readValue(bareArray, AirSenseWarningData.class)} 直接可用。
 *
 * <p>测试覆盖：直接 readValue bare-array、Alert 字段 SNAKE_CASE 映射、空数组、缺失字段容错。
 * MessageCodec.MAPPER 为 private，测试中创建等价配置的 ObjectMapper
 * （SNAKE_CASE + FAIL_ON_UNKNOWN_PROPERTIES=false）。
 */
class AirSenseWarningDataTest {

    /**
     * 等价于 MessageCodec.MAPPER 的 ObjectMapper：SNAKE_CASE 命名策略 + 容忍未知字段。
     * 用于反序列化 bare-array 为 {@code List<Alert>}。
     */
    private static ObjectMapper mapper;

    @BeforeAll
    static void setUpMapper() {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * 验证 bare JSON 数组反序列化为 AirSenseWarningData（Alert 字段映射正确）。
     *
     * <p>Given: bare-array JSON {@code [{"icao":"ABC","warning_level":3,"latitude":22.5}]}
     * When:  用 TypeReference&lt;List&lt;Alert&gt;&gt; 反序列化为 List，再用 record 构造器包裹
     * Then:  alerts 非 null，size=1，首元素 icao="ABC"、warningLevel=3、latitude=22.5
     */
    @Test
    void testBareArrayDeserialization() throws Exception {
        String json = "[{\"icao\":\"ABC\",\"warning_level\":3,\"latitude\":22.5}]";
        List<AirSenseWarningData.Alert> alerts = mapper.readValue(json,
                new TypeReference<List<AirSenseWarningData.Alert>>() {});
        AirSenseWarningData data = new AirSenseWarningData(alerts);

        assertNotNull(data, "反序列化结果不应为 null");
        assertNotNull(data.alerts(), "alerts 列表不应为 null");
        assertEquals(1, data.alerts().size(), "alerts 应有 1 个元素");

        AirSenseWarningData.Alert alert = data.alerts().get(0);
        assertEquals("ABC", alert.icao(), "icao 应为 ABC");
        assertEquals(3, alert.warningLevel(), "warning_level=3 应映射到 warningLevel=3");
        assertEquals(22.5, alert.latitude(), 0.0001, "latitude 应为 22.5");
    }

    /**
     * 验证空数组反序列化为空 alerts 列表。
     *
     * <p>Given: bare-array JSON {@code []}
     * When:  反序列化为 List&lt;Alert&gt; 再用 record 构造器包裹
     * Then:  alerts 非 null 且为空列表（compact constructor 的 requireNonNull 对空 List 通过，
     *        空数组映射为空 List 而非 null）
     */
    @Test
    void testEmptyArray() throws Exception {
        String json = "[]";
        List<AirSenseWarningData.Alert> alerts = mapper.readValue(json,
                new TypeReference<List<AirSenseWarningData.Alert>>() {});
        AirSenseWarningData data = new AirSenseWarningData(alerts);

        assertNotNull(data.alerts(), "空数组映射为空 List，非 null");
        assertTrue(data.alerts().isEmpty(), "alerts 应为空列表");
    }

    /**
     * 验证缺失部分字段时不抛异常（FAIL_ON_UNKNOWN_PROPERTIES=false + 可选字段为包装类型）。
     *
     * <p>Given: bare-array JSON {@code [{"icao":"ABC"}]}（仅 icao，缺 warning_level/latitude 等）
     * When:  反序列化为 List&lt;Alert&gt; 再用 record 构造器包裹
     * Then:  不抛异常，icao="ABC"，其余可选字段为 null
     */
    @Test
    void testNullFieldTolerance() throws Exception {
        String json = "[{\"icao\":\"ABC\"}]";
        List<AirSenseWarningData.Alert> alerts = mapper.readValue(json,
                new TypeReference<List<AirSenseWarningData.Alert>>() {});
        AirSenseWarningData data = new AirSenseWarningData(alerts);

        assertEquals(1, data.alerts().size(), "应有 1 个告警");
        AirSenseWarningData.Alert alert = data.alerts().get(0);
        assertEquals("ABC", alert.icao(), "icao 应为 ABC");
        assertNull(alert.warningLevel(), "缺失的 warning_level 应为 null");
        assertNull(alert.latitude(), "缺失的 latitude 应为 null");
        assertNull(alert.distance(), "缺失的 distance 应为 null");
    }

    /**
     * 验证 {@code readValue(bareArray, AirSenseWarningData.class)} 直接成功
     * （通过自定义反序列化器，无需 TypeReference 手动包裹）。
     *
     * <p>Given: bare-array JSON {@code [{"icao":"ABC","warning_level":3}]}
     * When:  用 readValue 直接反序列化为 AirSenseWarningData.class
     * Then:  不抛异常，alerts 非 null，size=1，icao="ABC"，warningLevel=3
     */
    @Test
    void testReadValueBareArraySucceeds() throws Exception {
        String json = "[{\"icao\":\"ABC\",\"warning_level\":3}]";
        AirSenseWarningData data = mapper.readValue(json, AirSenseWarningData.class);

        assertNotNull(data, "readValue 直接反序列化 bare-array 应成功");
        assertNotNull(data.alerts(), "alerts 不应为 null");
        assertEquals(1, data.alerts().size(), "应有 1 个告警");
        assertEquals("ABC", data.alerts().get(0).icao(), "icao 应为 ABC");
        assertEquals(3, data.alerts().get(0).warningLevel(), "warningLevel 应为 3");
    }
}
